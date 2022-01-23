package org.sc.job.scalingalgorithms;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class ProportionalScaling implements ScalingAlgorithm {
    private final float resizePercentage;

    public ProportionalScaling(float resizePercentage) {
        this.resizePercentage = resizePercentage;
    }

    @Override
    public BufferedImage scale(BufferedImage image) {
        int outputWidth = (int) (image.getWidth() * resizePercentage);
        int outputHeight = (int) (image.getHeight() * resizePercentage);
        Image resultingImage = image.getScaledInstance(outputWidth, outputHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

        return outputImage;
    }

}
