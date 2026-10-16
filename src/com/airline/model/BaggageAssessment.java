package com.airline.model;

/**
 * Result of the Baggage Allowance & Fee Assessment conditional logic.
 */
public class BaggageAssessment {
    private final int numberOfCheckedBags;
    private final double totalWeightKg;
    private final int freeBagsAllowed;
    private final int excessBagsCount;
    private final double excessWeightKg;
    private final double excessFee;
    private final String policyExplanation;

    public BaggageAssessment(int numberOfCheckedBags, double totalWeightKg, int freeBagsAllowed,
                             int excessBagsCount, double excessWeightKg, double excessFee,
                             String policyExplanation) {
        this.numberOfCheckedBags = numberOfCheckedBags;
        this.totalWeightKg = totalWeightKg;
        this.freeBagsAllowed = freeBagsAllowed;
        this.excessBagsCount = excessBagsCount;
        this.excessWeightKg = excessWeightKg;
        this.excessFee = excessFee;
        this.policyExplanation = policyExplanation;
    }

    public int getNumberOfCheckedBags() {
        return numberOfCheckedBags;
    }

    public double getTotalWeightKg() {
        return totalWeightKg;
    }

    public int getFreeBagsAllowed() {
        return freeBagsAllowed;
    }

    public int getExcessBagsCount() {
        return excessBagsCount;
    }

    public double getExcessWeightKg() {
        return excessWeightKg;
    }

    public double getExcessFee() {
        return excessFee;
    }

    public String getPolicyExplanation() {
        return policyExplanation;
    }

    @Override
    public String toString() {
        return String.format("BaggageAssessment[Free Bags: %d, Excess Bags: %d, Excess Wt: %.1fkg, Fee: $%.2f | Notes: %s]",
                freeBagsAllowed, excessBagsCount, excessWeightKg, excessFee, policyExplanation);
    }
}
