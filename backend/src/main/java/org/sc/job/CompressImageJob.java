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
import java.util.Iterator;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class CompressImageJob {

    public static final String STARTING_COMPRESSION_JOB = "Going to run images compression job (batch size: %s)...";
    public static final String DONE_COMPRESSION_JOB = "Done with images compression job.";
    public static final String COMPRESSION_PROGRESS = "Compressed image %s of batch with size %s...";
    public static final String COMPRESSION_FILENAME_PROGRESS = "Processing image '%s'...";
    public static final String COMPRESSION_FILENAME_DONE_PROGRESS = "Done Processing image '%s'...";

    private static final Logger LOGGER = getLogger(CompressImageJob.class);
    private final MediaMapper mapper;
    private final MediaManager mediaManager;
    private final FileManagementUtil fileManagementUtil;
    private final int imageBatchSize;

    @Autowired
    public CompressImageJob(final MediaManager mediaManager,
                            final MediaMapper mapper,
                            final AppProperties appProperties,
                            final FileManagementUtil fileManagementUtil) {
        this.mediaManager = mediaManager;
        this.mapper = mapper;
        this.imageBatchSize = appProperties.getJobImageBatchSize();
        this.fileManagementUtil = fileManagementUtil;
    }

    @Scheduled(cron = "0 */5 0-8 * * *")
    public void doGenerateImages() {

        int elaboratedImages = 0;

        LOGGER.info(format(STARTING_COMPRESSION_JOB, imageBatchSize));

        while (hasUncompressImageAndBelowThreshold(elaboratedImages)) {

            Media media = mapper.mapToObject(mediaManager.getMediaNotGenerated().iterator().next());
            String resolvedFileAddress = fileManagementUtil.getMediaStoragePath() + media.getFileName();
            File file = new File(resolvedFileAddress);

            try {
                BufferedImage originalImage = ImageIO.read(file);
                String mime = media.getMime();

                LOGGER.info(format(COMPRESSION_FILENAME_PROGRESS, resolvedFileAddress));
                for (Resolution resolution : Resolution.values()) {

                    final File compressedImageFile = new File(generateCompressedFileUrl(resolvedFileAddress, resolution.getSuffix()));

                    try (OutputStream os = new FileOutputStream(compressedImageFile);
                         ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {

                        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mime);
                        ImageWriter writer = writers.next();

                        writer.setOutput(ios);

                        ImageWriteParam param = writer.getDefaultWriteParam();

                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(resolution.getCompressionQuality());
                        writer.write(null, new IIOImage(originalImage, null, null), param);

                        writer.dispose();
                    }
                }
                LOGGER.info(format(COMPRESSION_FILENAME_DONE_PROGRESS, resolvedFileAddress));
                LOGGER.info(format(COMPRESSION_PROGRESS, elaboratedImages, imageBatchSize));
                mediaManager.updateCompressed(media);
                elaboratedImages++;

            } catch (IOException e) {
                LOGGER.error("Exception when compressing image {}", media.getFileUrl(), e);
            }
        }

        LOGGER.info(DONE_COMPRESSION_JOB);

    }

    private boolean hasUncompressImageAndBelowThreshold(int elaboratedImages) {
        return mediaManager.getMediaNotGenerated().iterator().hasNext() && elaboratedImages <= imageBatchSize;
    }

    protected String generateCompressedFileUrl(String fileUrl, String resolution) {
        int lastDotIndex = fileUrl.lastIndexOf('.');
        return fileUrl.substring(0, lastDotIndex) + resolution + fileUrl.substring(lastDotIndex);
    }

    private enum Resolution {

        H("_h", 0.8f),
        M("_m", 0.5f),
        L("_l", 0.1f),
        XL("_xl", 0.05f);

        private final String suffix;
        private final float compressionQuality;

        Resolution(String suffix, float compressionQuality) {
            this.suffix = suffix;
            this.compressionQuality = compressionQuality;
        }

        public String getSuffix() {
            return suffix;
        }

        public float getCompressionQuality() {
            return compressionQuality;
        }
    }

}
