package org.sc.converter;

public class MetricConverter {

    private static final int A_THOUSAND = 1000;

    public int getMetersFromKm(int km) {
        return km * A_THOUSAND;
    }

    public int getKmFromMeters(int m) {
        return m / A_THOUSAND;
    }
}
