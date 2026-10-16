package com.airline.engine;

import com.airline.model.Booking;
import com.airline.model.RefundResult;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Control Flow Application: Conditional (if / else if / else)
 * 
 * Blueprint Specification:
 * Cancellation Refund Eligibility:
 * if (hoursBeforeFlight >= 48 && isRefundableFare)
 *     issues 100% refund;
 * else if (hoursBeforeFlight >= 24)
 *     issues 50% travel voucher;
 * else
 *     flags ticket as non-refundable.
 */
public class CancellationRefundEngine {

    /**
     * Evaluates cancellation refund eligibility based on remaining hours before flight departure and fare type.
     *
     * @param hoursBeforeFlight Hours remaining until flight departs
     * @param isRefundableFare  Whether the purchased fare basis is refundable
     * @param totalPaidAmount   Total monetary amount paid for the ticket
     * @return RefundResult     Detailed refund outcome
     */
    public static RefundResult evaluateRefundEligibility(double hoursBeforeFlight,
                                                          boolean isRefundableFare,
                                                          double totalPaidAmount) {
        RefundResult.RefundEligibility eligibility;
        double refundPercentage;
        double cashRefundAmount = 0.0;
        double voucherAmount = 0.0;
        String decisionReason;

        // =========================================================================
        // CONTROL FLOW: CONDITIONAL (if / else if / else)
        // =========================================================================
        if (hoursBeforeFlight >= 48.0 && isRefundableFare) {
            // Issues 100% full refund
            eligibility = RefundResult.RefundEligibility.FULL_REFUND_100;
            refundPercentage = 100.0;
            cashRefundAmount = totalPaidAmount;
            voucherAmount = 0.0;
            decisionReason = String.format("Cancelled %.1f hours before flight on a refundable fare: 100%% cash refund issued.", hoursBeforeFlight);
        } else if (hoursBeforeFlight >= 24.0) {
            // Issues 50% travel voucher
            eligibility = RefundResult.RefundEligibility.TRAVEL_VOUCHER_50;
            refundPercentage = 50.0;
            cashRefundAmount = 0.0;
            voucherAmount = totalPaidAmount * 0.50;
            decisionReason = String.format("Cancelled %.1f hours before flight (>= 24h window): 50%% airline travel voucher issued.", hoursBeforeFlight);
        } else {
            // Flags ticket as non-refundable
            eligibility = RefundResult.RefundEligibility.NON_REFUNDABLE;
            refundPercentage = 0.0;
            cashRefundAmount = 0.0;
            voucherAmount = 0.0;
            decisionReason = String.format("Cancelled with under 24 hours remaining (%.1f hours) or non-refundable terms: Ticket is non-refundable.", hoursBeforeFlight);
        }

        return new RefundResult(
                hoursBeforeFlight,
                isRefundableFare,
                eligibility,
                refundPercentage,
                Math.round(cashRefundAmount * 100.0) / 100.0,
                Math.round(voucherAmount * 100.0) / 100.0,
                decisionReason
        );
    }

    /**
     * Evaluates refund directly from a booking record against current time or simulated departure time.
     */
    public static RefundResult cancelAndProcessRefund(Booking booking, LocalDateTime simulatedCurrentTime) {
        if (booking == null || booking.getItinerary() == null || booking.getItinerary().getFirstDepartureTime() == null) {
            return new RefundResult(0.0, false, RefundResult.RefundEligibility.NON_REFUNDABLE, 0.0, 0.0, 0.0, "Invalid booking or itinerary.");
        }

        LocalDateTime departure = booking.getItinerary().getFirstDepartureTime();
        long minutesBefore = Duration.between(simulatedCurrentTime, departure).toMinutes();
        double hoursBefore = Math.max(0.0, minutesBefore / 60.0);

        RefundResult result = evaluateRefundEligibility(hoursBefore, booking.isRefundableFare(), booking.getTotalAmountPaid());
        booking.setRefundResult(result);
        booking.setStatus(Booking.Status.CANCELLED);
        return result;
    }
}
