package org.sc.processor;

public class MetricConverter {
    private static final int A_THOUSAND = 1000;
    public static double toM(double km) {
        return km * A_THOUSAND;
    }
    public static double toKm(double m) {
        return m / A_THOUSAND;
    }
}
