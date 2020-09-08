package com.iit.cs442.fall20.shantanoo.converter;

public enum Unit {
    MILE("Miles", "Mi", 1.60934),
    KILOMETER("Kilometers", "Km", 0.621371);

    public final String label;
    public final String unit;
    public final Double conversionFactor;

    private Unit(String label, String unit, Double conversionFactor) {
        this.label = label;
        this.unit = unit;
        this.conversionFactor = conversionFactor;
    }

    public String getLabel() {
        return label;
    }

    public String getUnit() {
        return unit;
    }

    public Double getConversionFactor() {
        return conversionFactor;
    }
}
