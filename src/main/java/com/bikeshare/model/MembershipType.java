package com.bikeshare.model;

/**
 * Enumeration representing different membership types in the bike sharing system.
 */
public enum MembershipType {
    /**
     * Basic membership with no discounts.
     */
    BASIC("Basic", 0.0),
    
    /**
     * Premium membership with moderate discounts.
     */
    PREMIUM("Premium", 0.15),
    
    /**
     * VIP membership with highest discounts.
     */
    VIP("VIP", 0.25),
    
    /**
     * Student membership with special student pricing.
     */
    STUDENT("Student", 0.20),
    
    /**
     * Corporate membership for business accounts.
     */
    CORPORATE("Corporate", 0.10);
    
    private final String displayName;
    private final double discountRate;
    
    /**
     * Constructor for MembershipType enum.
     * @param displayName Human-readable name for the membership type
     * @param discountRate Discount rate as a decimal (0.15 = 15% discount)
     */
    MembershipType(String displayName, double discountRate) {
        this.displayName = displayName;
        this.discountRate = discountRate;
    }
    
    /**
     * Gets the display name of the membership type.
     * @return The human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the discount rate for this membership type.
     * @return The discount rate as a decimal (0.15 = 15%)
     */
    public double getDiscountRate() {
        return discountRate;
    }
    
    /**
     * Checks if this membership type offers any discount.
     * @return true if discount rate > 0, false otherwise
     */
    public boolean hasDiscount() {
        return discountRate > 0.0;
    }
    
    /**
     * Gets the annual fee for this membership type.
     * @return Annual membership fee in currency units
     */
    public double getAnnualFee() {
        switch (this) {
            case PREMIUM:
                return 59.99;
            case VIP:
                return 99.99;
            case STUDENT:
                return 29.99;
            case CORPORATE:
                return 149.99;
            case BASIC:
            default:
                return 0.0;
        }
    }
    
    /**
     * Gets the maximum number of free minutes per month for this membership.
     * @return Free minutes per month
     */
    public int getFreeMinutesPerMonth() {
        switch (this) {
            case PREMIUM:
                return 60;
            case VIP:
                return 120;
            case STUDENT:
                return 45;
            case CORPORATE:
                return 90;
            case BASIC:
            default:
                return 0;
        }
    }
}
