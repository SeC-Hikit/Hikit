package org.sc.converter;

public class MetricConverter {

    private static final int A_THOUSAND = 1000;

    public static double getMetersFromKm(double km) {
        return km * A_THOUSAND;
    }

    public static double getKmFromMeters(double m) {
        return m / A_THOUSAND;
    }
}
