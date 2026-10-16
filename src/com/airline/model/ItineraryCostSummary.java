package com.airline.model;

import java.util.List;

/**
 * Result of the Multi-Leg Itinerary Cost Evaluator (for-loop).
 */
public class ItineraryCostSummary {
    private final int totalLegs;
    private final double totalBaseFare;
    private final double totalAirportTax;
    private final double totalFuelSurcharge;
    private final double grandTotalCost;
    private final long totalFlightDurationMinutes;
    private final long totalLayoverDurationMinutes;
    private final long totalJourneyDurationMinutes;
    private final double totalDistanceMiles;

    public ItineraryCostSummary(int totalLegs, double totalBaseFare, double totalAirportTax,
                                double totalFuelSurcharge, double grandTotalCost,
                                long totalFlightDurationMinutes, long totalLayoverDurationMinutes,
                                long totalJourneyDurationMinutes, double totalDistanceMiles) {
        this.totalLegs = totalLegs;
        this.totalBaseFare = totalBaseFare;
        this.totalAirportTax = totalAirportTax;
        this.totalFuelSurcharge = totalFuelSurcharge;
        this.grandTotalCost = grandTotalCost;
        this.totalFlightDurationMinutes = totalFlightDurationMinutes;
        this.totalLayoverDurationMinutes = totalLayoverDurationMinutes;
        this.totalJourneyDurationMinutes = totalJourneyDurationMinutes;
        this.totalDistanceMiles = totalDistanceMiles;
    }

    public int getTotalLegs() {
        return totalLegs;
    }

    public double getTotalBaseFare() {
        return totalBaseFare;
    }

    public double getTotalAirportTax() {
        return totalAirportTax;
    }

    public double getTotalFuelSurcharge() {
        return totalFuelSurcharge;
    }

    public double getGrandTotalCost() {
        return grandTotalCost;
    }

    public long getTotalFlightDurationMinutes() {
        return totalFlightDurationMinutes;
    }

    public long getTotalLayoverDurationMinutes() {
        return totalLayoverDurationMinutes;
    }

    public long getTotalJourneyDurationMinutes() {
        return totalJourneyDurationMinutes;
    }

    public double getTotalDistanceMiles() {
        return totalDistanceMiles;
    }

    @Override
    public String toString() {
        return String.format("ItineraryCostSummary[Legs: %d | Base: $%.2f | Tax: $%.2f | Fuel: $%.2f | Total: $%.2f | Air Time: %dm | Layover: %dm | Total Trip: %dm]",
                totalLegs, totalBaseFare, totalAirportTax, totalFuelSurcharge, grandTotalCost,
                totalFlightDurationMinutes, totalLayoverDurationMinutes, totalJourneyDurationMinutes);
    }
}
