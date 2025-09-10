package com.bikeshare.model;

/**
 * Enumeration representing different types of bikes available in the system.
 */
public enum BikeType {
        /**
     * Standard city bike - most common and affordable option
     * Suitable for short trips and casual riding
     */
    STANDARD("Standard Bike", 0.50),

    /**
     * Electric-assisted bike - premium option with battery support
     * Ideal for longer distances and hilly terrain
     */
    ELECTRIC("Electric Bike", 1.00),

    /**
     * Mountain bike - designed for off-road and rough terrain
     * Features robust construction and specialized tires
     */
    MOUNTAIN("Mountain Bike", 0.70),

    /**
     * Cargo bike - heavy-duty bike for transporting goods
     * Equipped with large cargo area for deliveries
     */
    CARGO("Cargo Bike", 1.20);
    
    private final String displayName;
    private final double pricePerMinute;
    
    /**
     * Constructor for BikeType enum.
     * @param displayName Human-readable name for the bike type
     * @param pricePerMinute Cost per minute for this bike type
     */
    BikeType(String displayName, double pricePerMinute) {
        this.displayName = displayName;
        this.pricePerMinute = pricePerMinute;
    }
    
    /**
     * Gets the display name of the bike type.
     * @return The human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the price per minute for this bike type.
     * @return The cost per minute in currency units
     */
    public double getPricePerMinute() {
        return pricePerMinute;
    }
    
    /**
     * Checks if this bike type has electric assistance.
     * @return true if electric, false otherwise
     */
    public boolean isElectric() {
        return this == ELECTRIC;
    }
    
    /**
     * Gets the maximum speed for this bike type in km/h.
     * @return Maximum speed in kilometers per hour
     */
    public int getMaxSpeedKmh() {
        switch (this) {
            case ELECTRIC:
                return 25;
            case MOUNTAIN:
                return 30;
            case CARGO:
                return 20;
            case STANDARD:
            default:
                return 25;
        }
    }
    
    /**
     * Gets the weight category for this bike type.
     * @return Weight category as a string
     */
    public String getWeightCategory() {
        switch (this) {
            case ELECTRIC:
                return "Heavy";
            case MOUNTAIN:
                return "Medium-Heavy";
            case CARGO:
                return "Very Heavy";
            case STANDARD:
            default:
                return "Light";
        }
    }
}
