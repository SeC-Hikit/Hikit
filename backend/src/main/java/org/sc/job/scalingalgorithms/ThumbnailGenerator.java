package org.sc.job.scalingalgorithms;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class ThumbnailGenerator implements ScalingAlgorithm {
    private static final int THUMB_WIDTH = 315;
    private static final int THUMB_HEIGHT = 315;

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
     * @param image original image
     * @return a {@code THUMB_WIDTH}x{@code THUMB_HEIGHT} thumbnail
     *
     */
    @Override
    public BufferedImage scale(BufferedImage image) {
        BufferedImage scaledImage = new BufferedImage(THUMB_WIDTH * 2, THUMB_HEIGHT * 2, BufferedImage.TYPE_INT_RGB);
        Image scaledInstance = image.getScaledInstance(THUMB_WIDTH * 2, -1, BufferedImage.SCALE_FAST);
        scaledImage.createGraphics().drawImage(scaledInstance, 0, 0, null);

        int scaledHeight = image.getHeight() * THUMB_WIDTH * 2 / image.getWidth();
        int yCoordinate = Math.max(scaledHeight / 2 - THUMB_WIDTH / 2 - 1, 0);

        scaledImage = scaledImage.getSubimage(THUMB_WIDTH / 2 - 1, yCoordinate, THUMB_WIDTH, THUMB_HEIGHT);

        return scaledImage;
    }
}
