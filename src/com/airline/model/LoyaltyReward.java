package com.airline.model;

/**
 * Result of Loyalty Tier Dispatcher switch-case evaluation.
 */
public class LoyaltyReward {
    private final LoyaltyTier tier;
    private final double multiplier;
    private final double baseMiles;
    private final double bonusMiles;
    private final double totalMilesEarned;
    private final boolean loungePass;
    private final boolean priorityBoarding;
    private final String perksDescription;

    public LoyaltyReward(LoyaltyTier tier, double multiplier, double baseMiles, double bonusMiles,
                         double totalMilesEarned, boolean loungePass, boolean priorityBoarding,
                         String perksDescription) {
        this.tier = tier;
        this.multiplier = multiplier;
        this.baseMiles = baseMiles;
        this.bonusMiles = bonusMiles;
        this.totalMilesEarned = totalMilesEarned;
        this.loungePass = loungePass;
        this.priorityBoarding = priorityBoarding;
        this.perksDescription = perksDescription;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public double getBaseMiles() {
        return baseMiles;
    }

    public double getBonusMiles() {
        return bonusMiles;
    }

    public double getTotalMilesEarned() {
        return totalMilesEarned;
    }

    public boolean hasLoungePass() {
        return loungePass;
    }

    public boolean hasPriorityBoarding() {
        return priorityBoarding;
    }

    public String getPerksDescription() {
        return perksDescription;
    }

    @Override
    public String toString() {
        return String.format("LoyaltyReward[Tier: %s | Multiplier: %.1fx | Base: %.0f | Bonus: %.0f | Total: %.0f | Lounge: %s | Priority Boarding: %s]",
                tier, multiplier, baseMiles, bonusMiles, totalMilesEarned, loungePass ? "YES" : "NO", priorityBoarding ? "YES" : "NO");
    }
}
