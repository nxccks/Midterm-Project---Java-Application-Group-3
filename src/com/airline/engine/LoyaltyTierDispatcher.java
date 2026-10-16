package com.airline.engine;

import com.airline.model.LoyaltyReward;
import com.airline.model.LoyaltyTier;
import com.airline.model.Passenger;

/**
 * Control Flow Application: Switch (switch-case)
 * 
 * Blueprint Specification:
 * Frequent Flyer Tier Multiplier: Assigns reward point multipliers based on customer membership status:
 * - case "PLATINUM": Applies 3.0× bonus mile multiplier and lounge pass.
 * - case "GOLD": Applies 2.0× bonus mile multiplier and priority boarding.
 * - case "SILVER": Applies 1.5× bonus mile multiplier.
 * - default: Awards standard 1.0× base miles.
 */
public class LoyaltyTierDispatcher {

    /**
     * Calculates bonus miles and tier privileges using a Switch-Case structure.
     *
     * @param passenger  The passenger whose tier is being evaluated
     * @param flightMiles The base flight distance in miles
     * @return LoyaltyReward Calculated reward multiplier, bonus points, and membership perks
     */
    public static LoyaltyReward calculateLoyaltyReward(Passenger passenger, double flightMiles) {
        String tierString = (passenger != null && passenger.getLoyaltyTier() != null)
                ? passenger.getLoyaltyTier().name().toUpperCase()
                : "REGULAR";

        return calculateByTierString(tierString, flightMiles);
    }

    /**
     * Dispatches tier multipliers and perks using switch-case logic on string tier names.
     */
    public static LoyaltyReward calculateByTierString(String tierName, double flightMiles) {
        double multiplier;
        boolean loungePass = false;
        boolean priorityBoarding = false;
        String perksDescription;
        LoyaltyTier resolvedTier;

        // =========================================================================
        // CONTROL FLOW: SWITCH (switch-case)
        // =========================================================================
        switch (tierName != null ? tierName.toUpperCase() : "REGULAR") {
            case "PLATINUM":
                multiplier = 3.0;
                loungePass = true;
                priorityBoarding = true;
                perksDescription = "Platinum Status: 3.0x Bonus Mile Multiplier + Complimentary Airport Lounge Pass + Priority Boarding.";
                resolvedTier = LoyaltyTier.PLATINUM;
                break;

            case "GOLD":
                multiplier = 2.0;
                loungePass = false;
                priorityBoarding = true;
                perksDescription = "Gold Status: 2.0x Bonus Mile Multiplier + Priority Boarding.";
                resolvedTier = LoyaltyTier.GOLD;
                break;

            case "SILVER":
                multiplier = 1.5;
                loungePass = false;
                priorityBoarding = false;
                perksDescription = "Silver Status: 1.5x Bonus Mile Multiplier.";
                resolvedTier = LoyaltyTier.SILVER;
                break;

            default:
                multiplier = 1.0;
                loungePass = false;
                priorityBoarding = false;
                perksDescription = "Standard Membership: 1.0x Base Miles Awarded.";
                resolvedTier = LoyaltyTier.REGULAR;
                break;
        }

        double totalMiles = flightMiles * multiplier;
        double bonusMiles = totalMiles - flightMiles;

        return new LoyaltyReward(
                resolvedTier,
                multiplier,
                flightMiles,
                bonusMiles,
                totalMiles,
                loungePass,
                priorityBoarding,
                perksDescription
        );
    }
}
