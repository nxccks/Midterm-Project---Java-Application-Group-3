package com.airline.engine;

import com.airline.model.FlightLeg;
import com.airline.model.ItineraryCostSummary;
import com.airline.model.TripItinerary;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Control Flow Application: Loops (for)
 * 
 * Blueprint Specification:
 * Multi-Leg Itinerary Cost Evaluator (for flightLeg in tripItinerary):
 * Iterates across connecting flights to calculate base fares, local airport taxes,
 * fuel surcharges, and total flight duration.
 */
public class ItineraryCostEvaluator {

    /**
     * Iterates across connecting flight legs using a FOR loop to aggregate costs and timings.
     *
     * @param tripItinerary The multi-leg travel itinerary
     * @return ItineraryCostSummary Aggregated breakdown of base fares, taxes, surcharges, and durations
     */
    public static ItineraryCostSummary evaluateItineraryCost(TripItinerary tripItinerary) {
        if (tripItinerary == null || tripItinerary.isEmpty()) {
            return new ItineraryCostSummary(0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0.0);
        }

        double totalBaseFare = 0.0;
        double totalAirportTax = 0.0;
        double totalFuelSurcharge = 0.0;
        long totalFlightDurationMinutes = 0L;
        long totalLayoverDurationMinutes = 0L;
        double totalDistanceMiles = 0.0;
        int legCount = 0;

        List<FlightLeg> legs = tripItinerary.getLegs();
        LocalDateTime previousArrivalTime = null;

        // =========================================================================
        // CONTROL FLOW: LOOP (for flightLeg in tripItinerary)
        // =========================================================================
        for (FlightLeg flightLeg : legs) {
            legCount++;

            // Accumulate base fares
            totalBaseFare += flightLeg.getBaseFare();

            // Accumulate local airport taxes
            totalAirportTax += flightLeg.getLocalAirportTax();

            // Accumulate fuel surcharges
            totalFuelSurcharge += flightLeg.getFuelSurcharge();

            // Accumulate distance in miles
            totalDistanceMiles += flightLeg.getDistanceMiles();

            // Accumulate flight airborne duration
            totalFlightDurationMinutes += flightLeg.getFlightDurationMinutes();

            // Calculate layover duration between connecting flights
            if (previousArrivalTime != null && flightLeg.getDepartureTime().isAfter(previousArrivalTime)) {
                long layoverMinutes = Duration.between(previousArrivalTime, flightLeg.getDepartureTime()).toMinutes();
                totalLayoverDurationMinutes += layoverMinutes;
            }

            // Track arrival time for subsequent layover check
            previousArrivalTime = flightLeg.getArrivalTime();
        }

        double grandTotalCost = totalBaseFare + totalAirportTax + totalFuelSurcharge;
        long totalJourneyDurationMinutes = totalFlightDurationMinutes + totalLayoverDurationMinutes;

        return new ItineraryCostSummary(
                legCount,
                Math.round(totalBaseFare * 100.0) / 100.0,
                Math.round(totalAirportTax * 100.0) / 100.0,
                Math.round(totalFuelSurcharge * 100.0) / 100.0,
                Math.round(grandTotalCost * 100.0) / 100.0,
                totalFlightDurationMinutes,
                totalLayoverDurationMinutes,
                totalJourneyDurationMinutes,
                totalDistanceMiles
        );
    }
}
