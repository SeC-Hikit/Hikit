package org.sc.converter;

public class MetricConverter {

    private static final int A_THOUSAND = 1000;

    /**
     * Convert km to meters
     * @param km
     * @return
     */
    public static double toM(double km) {
        return km * A_THOUSAND;
    }

    /**
     * Convert meters to km
     * @param m
     * @return
     */
    public static double toKm(double m) {
        return m / A_THOUSAND;
    }
}
