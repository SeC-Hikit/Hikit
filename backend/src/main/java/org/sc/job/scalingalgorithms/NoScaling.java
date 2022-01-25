package org.sc.job.scalingalgorithms;

import java.awt.image.BufferedImage;

public class NoScaling implements ScalingAlgorithm {
    @Override
    public BufferedImage scale(BufferedImage image) {
        return image;
    }
}
