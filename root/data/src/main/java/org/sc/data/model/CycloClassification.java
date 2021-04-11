package org.sc.data.model;

public enum CycloClassification {
    TC("TC"), TC_PLUS("TC+"),
    MC("MC"), MC_PLUS("MC+"),
    BC("BC"), BC_PLUS("BC+"),
    OC("OC"), OC_PLUS("OC+"),
    EC("EC"), NO("NO"), UNCLASSIFIED("UNCLASSIFIED");

    private final String classification;

    CycloClassification(String classification) {
        this.classification = classification;
    }

    public String getClassification() {
        return classification;
    }
}
