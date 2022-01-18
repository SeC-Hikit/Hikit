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
import java.awt.Image;
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

    private static final String STARTING_COMPRESSION_JOB = "Going to run images compression job (batch size: %s)...";
    private static final String DONE_COMPRESSION_JOB = "Done with images compression job.";
    private static final String COMPRESSION_PROGRESS = "Compressed image %s of batch with size %s...";
    private static final String COMPRESSION_FILENAME_PROGRESS = "Processing image '%s'...";
    private static final String COMPRESSION_FILENAME_DONE_PROGRESS = "Done Processing image '%s'...";
    private static final String COMPRESSION_BUT_NO_DELETION = "Done compressing, but could not delete '%s'...";
    private static final int THUMB_WIDTH = 315;
    private static final int THUMB_HEIGHT = 315;

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

//    @Scheduled(cron = "0 */5 0-8 * * *")
    @Scheduled(cron = "* * * * * *")
    public void doCompressImages() {

        int elaboratedImages = 0;

        LOGGER.info(format(STARTING_COMPRESSION_JOB, imageBatchSize));

        while (hasUncompressImageAndBelowThreshold(elaboratedImages)) {

            final Media media = mapper.mapToObject(mediaManager.getUncompressedMedia().iterator().next());
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

                        IIOImage iioImage = new IIOImage(resolution == Resolution.THUMB ? scale(originalImage) : originalImage, null, null);
                        writer.write(null, iioImage, param);
                        writer.dispose();
                    }
                }
                LOGGER.info(format(COMPRESSION_FILENAME_DONE_PROGRESS, resolvedFileAddress));
                LOGGER.info(format(COMPRESSION_PROGRESS, elaboratedImages, imageBatchSize));
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

        LOGGER.info(DONE_COMPRESSION_JOB);

    }

    private String getFileName(final Media media) {
        return media.getFileName() + "." + media.getExtension();
    }

    private void updateDbOnCompress(Media media, Resolution[] resolutionValues) {
        media.setResolutions(Arrays.stream(resolutionValues)
                .map(a -> a.suffix).collect(Collectors.toList()));
        mediaManager.updateCompressed(media);
    }

    private boolean hasUncompressImageAndBelowThreshold(int elaboratedImages) {
        return mediaManager.getUncompressedMedia().iterator().hasNext() && elaboratedImages <= imageBatchSize;
    }

    protected String generateCompressedFileUrl(String fileUrl, String resolution) {
        final int lastDotIndex = fileUrl.lastIndexOf('.');
        return fileUrl.substring(0, lastDotIndex) + resolution + fileUrl.substring(lastDotIndex);
    }

    /**
     * It creates a scaled image of the given-in {@code image} with {@code THUMB_WIDTH} x {@code THUMB_HEIGHT} size.
     *
     * The algorithm is:
     * <ol>
     *  <li>it is created a BufferedImage of {@code THUMB_WIDTH*2} and {@code THUMB_HEIGHT*2}, because we have to fit a rectangular image into a square</li>
     *  <li>the input {@code image} is fit into it. So that at the bottom there will we empty space, due to the aspect ratio of photo images
     *      N.B aspect ratio is respected due to the negative height.</li>
     *  <li>The y coordinate of the center is evaluated as follows:
     *      <ol>
     *          <li>it's retrieved the height of the resized image (considering that the width is equals to {@code THUMB_WIDTH * 2}</li>
     *          <li>from the center its coordinate it's {@code THUMB_WIDTH / 2 - 1} px above, so that it's completely included. <br/>
     *              Max is used to avoid negative height.</li>
     *      </ol>
     *  </li>
     * </ol>
     *
     * <pre>
     *                    THUMB_WIDTH*2
     *  +-----------------------X----------------------+
     *  -
     *  -                 ############                    <- scaledHeight / 2 - THUMB_WIDTH / 2 - 1
     *  -                 #          #
     *  -                 #          #                    <- scaledHeight / 2
     *  -                 #          #
     *  -                 ############
     *  -
     *  -                                                 <- image.getHeight() * THUMB_WIDTH * 2 / image.getWidth()
     *  ++++++++++++++++++++++++++++++++++++++++++++++++
     *  ++++++++++++++++++++++++++++++++++++++++++++++++
     *  ++++++++++ Empty due to aspect ratio +++++++++++
     *  ++++++++++++++++++++++++++++++++++++++++++++++++
     *  ++++++++++++++++++++++++++++++++++++++++++++++++
     * </pre>
     *
     * @param image
     * @return scaled image
     *
     * @throws IOException
     */
    private BufferedImage scale(BufferedImage image) throws IOException {
        BufferedImage scaledImage = new BufferedImage(THUMB_WIDTH * 2, THUMB_HEIGHT * 2, BufferedImage.TYPE_INT_RGB);
        Image scaledInstance = image.getScaledInstance(THUMB_WIDTH * 2, -1, BufferedImage.SCALE_FAST);
        scaledImage.createGraphics().drawImage(scaledInstance, 0, 0, null);

        int scaledHeight = image.getHeight() * THUMB_WIDTH * 2 / image.getWidth();
        int yCoordinate = Math.max(scaledHeight / 2 - THUMB_WIDTH / 2 - 1, 0);

        scaledImage = scaledImage.getSubimage(THUMB_WIDTH / 2 - 1, yCoordinate, THUMB_WIDTH, THUMB_HEIGHT);

        return scaledImage;
    }

    public enum Resolution {

        H("_h", 0.8f),
        M("_m", 0.5f),
        L("_l", 0.1f),
        XL("_xl", 0.05f),
        THUMB("_thumbnail", 0.8f);

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
