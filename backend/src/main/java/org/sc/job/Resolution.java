package org.sc.job;

import org.sc.job.scalingalgorithms.NoScaling;
import org.sc.job.scalingalgorithms.ProportionalScaling;
import org.sc.job.scalingalgorithms.ScalingAlgorithm;
import org.sc.job.scalingalgorithms.ThumbnailGenerator;

public enum Resolution {

    H("_h", 0.8f, new NoScaling()),
    M("_m", 0.6f, new NoScaling()),
    L("_l", 0.6f, new ProportionalScaling(0.5f)),
    XL("_xl", 0.6f, new ProportionalScaling(0.25f)),
    THUMB("_thumbnail", 0.8f, new ThumbnailGenerator());

    private final String suffix;
    private final float compressionQuality;
    private final ScalingAlgorithm scalingAlgorithm;

    Resolution(String suffix, float compressionQuality, ScalingAlgorithm scalingAlgorithm) {
        this.suffix = suffix;
        this.compressionQuality = compressionQuality;
        this.scalingAlgorithm = scalingAlgorithm;
    }

    public String getSuffix() {
        return suffix;
    }

    public float getCompressionQuality() {
        return compressionQuality;
    }

    public ScalingAlgorithm getScalingAlgorithm() {
        return scalingAlgorithm;
    }
}
