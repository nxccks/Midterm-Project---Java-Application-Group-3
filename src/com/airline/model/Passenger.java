package com.airline.model;

/**
 * Passenger representation with loyalty status.
 */
public class Passenger {
    private final String id;
    private final String name;
    private final String email;
    private LoyaltyTier loyaltyTier;
    private Boolean eliteOverride; // Optional explicit override

    public Passenger(String id, String name, String email, LoyaltyTier loyaltyTier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.loyaltyTier = loyaltyTier;
        this.eliteOverride = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(LoyaltyTier loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public void setEliteOverride(Boolean eliteOverride) {
        this.eliteOverride = eliteOverride;
    }

    /**
     * Determines if the passenger holds elite frequent flyer status.
     * PLATINUM and GOLD members qualify as elite frequent flyers.
     */
    public boolean isEliteFrequentFlyer() {
        if (eliteOverride != null) {
            return eliteOverride;
        }
        return loyaltyTier == LoyaltyTier.PLATINUM || loyaltyTier == LoyaltyTier.GOLD;
    }

    @Override
    public String toString() {
        return String.format("Passenger[ID:%s, Name:%s, Tier:%s, Elite:%s]",
                id, name, loyaltyTier, isEliteFrequentFlyer());
    }
}
