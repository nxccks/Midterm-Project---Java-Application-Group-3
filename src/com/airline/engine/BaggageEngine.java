package com.airline.engine;

import com.airline.model.BaggageAssessment;
import com.airline.model.Passenger;
import com.airline.model.TicketClass;

/**
 * Control Flow Application: Conditional (if / else if / else)
 * 
 * Blueprint Specification:
 * Baggage Allowance & Fee Assessment:
 * if (ticketClass == "FIRST" || isEliteFrequentFlyer) permits up to 2 checked bags free;
 * else if (ticketClass == "BUSINESS") permits 1 free bag;
 * else charges standard per-kilogram excess luggage rate.
 */
public class BaggageEngine {

    public static final double STANDARD_PER_KG_RATE = 15.00;     // $15 per kilogram
    public static final double STANDARD_BAG_WEIGHT_LIMIT_KG = 23.0; // 23kg per standard bag
    public static final double EXTRA_PIECE_FEE = 40.00;           // Handling fee for extra piece

    /**
     * Assesses baggage allowance and fees using conditional control flow.
     *
     * @param ticketClass         Ticket class (FIRST, BUSINESS, ECONOMY)
     * @param passenger           Passenger information including loyalty/elite status
     * @param numberOfCheckedBags Total number of checked bags passenger wishes to check
     * @param totalWeightKg       Total weight of all checked bags combined
     * @return BaggageAssessment  Complete assessment with breakdown and explanation
     */
    public static BaggageAssessment assessBaggage(TicketClass ticketClass,
                                                 Passenger passenger,
                                                 int numberOfCheckedBags,
                                                 double totalWeightKg) {
        int freeBagsAllowed;
        String policyExplanation;
        boolean isElite = passenger != null && passenger.isEliteFrequentFlyer();

        // =========================================================================
        // CONTROL FLOW: CONDITIONAL (if / else if / else)
        // =========================================================================
        if (ticketClass == TicketClass.FIRST || isElite) {
            // First Class or Elite Frequent Flyer: up to 2 checked bags free
            freeBagsAllowed = 2;
            String eliteTag = isElite ? " [Elite Status Active]" : "";
            policyExplanation = "FIRST class fare or Elite Frequent Flyer tier allows up to 2 complimentary checked bags" + eliteTag + ".";
        } else if (ticketClass == TicketClass.BUSINESS) {
            // Business Class: 1 checked bag free
            freeBagsAllowed = 1;
            policyExplanation = "BUSINESS class fare allows 1 complimentary checked bag.";
        } else {
            // Economy Class (or Standard / Non-Elite): standard per-kilogram excess rate applied
            freeBagsAllowed = 0;
            policyExplanation = "ECONOMY standard fare: No complimentary checked bags included. Standard per-kilogram excess luggage rate applied.";
        }

        // Compute excess piece count and excess weight
        int excessBagsCount = Math.max(0, numberOfCheckedBags - freeBagsAllowed);
        double maxFreeWeightAllowed = freeBagsAllowed * STANDARD_BAG_WEIGHT_LIMIT_KG;
        double excessWeightKg = Math.max(0.0, totalWeightKg - maxFreeWeightAllowed);

        // Compute fee
        double excessFee = 0.0;
        if (numberOfCheckedBags > 0) {
            if (freeBagsAllowed == 0) {
                // Economy: charges per-kilogram for the full luggage weight
                excessFee = totalWeightKg * STANDARD_PER_KG_RATE;
            } else {
                // Excess pieces or overweight beyond free limit
                double pieceFee = excessBagsCount * EXTRA_PIECE_FEE;
                double weightFee = excessWeightKg * STANDARD_PER_KG_RATE;
                excessFee = pieceFee + weightFee;
            }
        }

        return new BaggageAssessment(
                numberOfCheckedBags,
                totalWeightKg,
                freeBagsAllowed,
                excessBagsCount,
                excessWeightKg,
                Math.round(excessFee * 100.0) / 100.0,
                policyExplanation
        );
    }
}
