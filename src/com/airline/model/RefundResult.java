package com.airline.model;

/**
 * Result of Cancellation Refund Eligibility conditional evaluation.
 */
public class RefundResult {
    public enum RefundEligibility {
        FULL_REFUND_100,
        TRAVEL_VOUCHER_50,
        NON_REFUNDABLE
    }

    private final double hoursBeforeFlight;
    private final boolean refundableFare;
    private final RefundEligibility eligibility;
    private final double refundPercentage;
    private final double cashRefundAmount;
    private final double voucherAmount;
    private final String decisionReason;

    public RefundResult(double hoursBeforeFlight, boolean refundableFare,
                        RefundEligibility eligibility, double refundPercentage,
                        double cashRefundAmount, double voucherAmount,
                        String decisionReason) {
        this.hoursBeforeFlight = hoursBeforeFlight;
        this.refundableFare = refundableFare;
        this.eligibility = eligibility;
        this.refundPercentage = refundPercentage;
        this.cashRefundAmount = cashRefundAmount;
        this.voucherAmount = voucherAmount;
        this.decisionReason = decisionReason;
    }

    public double getHoursBeforeFlight() {
        return hoursBeforeFlight;
    }

    public boolean isRefundableFare() {
        return refundableFare;
    }

    public RefundEligibility getEligibility() {
        return eligibility;
    }

    public double getRefundPercentage() {
        return refundPercentage;
    }

    public double getCashRefundAmount() {
        return cashRefundAmount;
    }

    public double getVoucherAmount() {
        return voucherAmount;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    @Override
    public String toString() {
        return String.format("RefundResult[%s | HoursBefore: %.1f | RefundableFare: %s | Cash: $%.2f | Voucher: $%.2f | Reason: %s]",
                eligibility, hoursBeforeFlight, refundableFare, cashRefundAmount, voucherAmount, decisionReason);
    }
}
