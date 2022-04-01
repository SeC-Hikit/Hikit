package org.sc.job;

import org.apache.logging.log4j.Logger;
import org.sc.configuration.AppProperties;
import org.sc.data.entity.mapper.MediaMapper;
import org.sc.data.model.Media;
import org.sc.manager.MediaManager;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class CompressImageJob {
    private static final Logger LOGGER = getLogger(CompressImageJob.class);

    private static final String STARTING_COMPRESSION_JOB = "Going to run images compression job for instance Id '%s' (batch size: %s)...";
    private static final String DONE_COMPRESSION_JOB = "Done with images compression job.";
    private static final String COMPRESSION_PROGRESS = "Compressed image %s of batch with size %s...";
    private static final String COMPRESSION_FILENAME_PROGRESS = "Processing image '%s'...";
    private static final String COMPRESSION_FILENAME_DONE_PROGRESS = "Done Processing image '%s'...";
    private static final String COMPRESSION_BUT_NO_DELETION = "Done compressing, but could not delete '%s'...";

    private final MediaMapper mapper;
    private final MediaManager mediaManager;
    private final FileManagementUtil fileManagementUtil;
    private final AppProperties appProperties;

    @Autowired
    public CompressImageJob(final MediaManager mediaManager,
                            final MediaMapper mapper,
                            final AppProperties appProperties,
                            final FileManagementUtil fileManagementUtil) {
        this.mediaManager = mediaManager;
        this.mapper = mapper;
        this.appProperties = appProperties;
        this.fileManagementUtil = fileManagementUtil;
    }

    @Scheduled(cron = "0 */2 0-23 * * *")
    public void doCompressImages() {

        int elaboratedImages = 0;

        final int batchSize = appProperties.getJobImageBatchSize();
        final String instanceId = appProperties.getInstanceId();

        LOGGER.trace(format(STARTING_COMPRESSION_JOB, instanceId, batchSize));

        while (hasUncompressImageAndBelowThreshold(elaboratedImages, batchSize, instanceId)) {
            final Media media = mapper.mapToObject(
                    mediaManager.getUncompressedMedia(instanceId)
                    .iterator().next());
            final String resolvedFileAddress = fileManagementUtil.getMediaStoragePath() + getFileName(media);
            final File file = new File(resolvedFileAddress);
            final Resolution[] resolutionValues = Resolution.values();

            try {
                final BufferedImage originalImage = ImageIO.read(file);
                final String mime = media.getMime();
                LOGGER.info(format(COMPRESSION_FILENAME_PROGRESS, resolvedFileAddress));
                for (Resolution resolution : resolutionValues) {

                    final File compressedImageFile = new File(generateCompressedFileUrl(resolvedFileAddress, resolution.getSuffix()));

                    try (OutputStream os = new FileOutputStream(compressedImageFile);
                         ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {

                        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mime);
                        final ImageWriter writer = writers.next();

                        writer.setOutput(ios);

                        final ImageWriteParam param = writer.getDefaultWriteParam();
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(resolution.getCompressionQuality());

                        BufferedImage scaledImage = resolution.getScalingAlgorithm().scale(originalImage);
                        IIOImage iioImage = new IIOImage(scaledImage, null, null);
                        writer.write(null, iioImage, param);
                        writer.dispose();
                    }
                }
                LOGGER.info(format(COMPRESSION_FILENAME_DONE_PROGRESS, resolvedFileAddress));
                LOGGER.info(format(COMPRESSION_PROGRESS, elaboratedImages, batchSize));
                updateDbOnCompress(media, resolutionValues);

                final boolean delete = file.delete();
                if (!delete) {
                    LOGGER.error(format(COMPRESSION_BUT_NO_DELETION, resolvedFileAddress));
                }

                elaboratedImages++;

            } catch (IOException e) {
                LOGGER.error("Exception when compressing image {}", media.getFileUrl(), e);
            }
        }

        LOGGER.trace(DONE_COMPRESSION_JOB);

    }

    private String getFileName(final Media media) {
        return media.getFileName() + "." + media.getExtension();
    }

    private void updateDbOnCompress(Media media, Resolution[] resolutionValues) {
        media.setResolutions(Arrays.stream(resolutionValues)
                .map(Resolution::getSuffix).collect(Collectors.toList()));
        mediaManager.updateCompressed(media);
    }

    private boolean hasUncompressImageAndBelowThreshold(int elaboratedImages, int batchSize, String instanceId) {
        return areUncompressedMediaPresent(instanceId) && elaboratedImages <=
                batchSize;
    }

    private boolean areUncompressedMediaPresent(String instanceId) {
        return mediaManager.getUncompressedMedia(instanceId)
                .iterator().hasNext();
    }

    protected String generateCompressedFileUrl(String fileUrl, String resolution) {
        final int lastDotIndex = fileUrl.lastIndexOf('.');
        return fileUrl.substring(0, lastDotIndex) + resolution + fileUrl.substring(lastDotIndex);
    }

}
