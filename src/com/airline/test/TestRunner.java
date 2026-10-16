package com.airline.test;

import com.airline.engine.*;
import com.airline.model.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lightweight Unit Test Runner for validating all control flow engine behaviors.
 */
public class TestRunner {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("🧪 RUNNING SUITE OF CONTROL FLOW ENGINE TESTS...\n");

        testBaggageEngine();
        testSeatHoldEngine();
        testCancellationRefundEngine();
        testLoyaltyTierDispatcher();
        testItineraryCostEvaluator();
        testExpiredSeatReleaser();

        System.out.printf("\n==================================================\n");
        System.out.printf("Test Summary: %d Total | %d Passed | %d Failed\n", totalTests, passedTests, failedTests);
        System.out.printf("==================================================\n");

        if (failedTests > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  ✅ PASS: " + testName);
        } else {
            failedTests++;
            System.err.println("  ❌ FAIL: " + testName);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        totalTests++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            passedTests++;
            System.out.println("  ✅ PASS: " + testName);
        } else {
            failedTests++;
            System.err.println("  ❌ FAIL: " + testName + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void testBaggageEngine() {
        System.out.println("--- Testing Baggage Allowance & Fee Assessment (if / else if / else) ---");
        Passenger elitePass = new Passenger("P1", "Gold User", "gold@air.com", LoyaltyTier.GOLD);
        Passenger regPass = new Passenger("P2", "Regular User", "reg@air.com", LoyaltyTier.REGULAR);

        // 1. FIRST Class allows 2 free bags
        BaggageAssessment b1 = BaggageEngine.assessBaggage(TicketClass.FIRST, regPass, 2, 46.0);
        assertEquals("FIRST class gets 2 free bags", 2, b1.getFreeBagsAllowed());
        assertEquals("FIRST class 2 standard bags has $0 excess fee", 0.0, b1.getExcessFee());

        // 2. Elite Frequent Flyer in Economy allows 2 free bags
        BaggageAssessment b2 = BaggageEngine.assessBaggage(TicketClass.ECONOMY, elitePass, 2, 40.0);
        assertEquals("Elite Frequent Flyer gets 2 free bags even in Economy", 2, b2.getFreeBagsAllowed());
        assertEquals("Elite within weight limit has $0 excess fee", 0.0, b2.getExcessFee());

        // 3. BUSINESS Class allows 1 free bag
        BaggageAssessment b3 = BaggageEngine.assessBaggage(TicketClass.BUSINESS, regPass, 1, 23.0);
        assertEquals("BUSINESS class gets 1 free bag", 1, b3.getFreeBagsAllowed());
        assertEquals("BUSINESS class 1 bag has $0 fee", 0.0, b3.getExcessFee());

        // 4. ECONOMY Class non-elite has 0 free bags and charges per kg
        BaggageAssessment b4 = BaggageEngine.assessBaggage(TicketClass.ECONOMY, regPass, 1, 20.0);
        assertEquals("ECONOMY non-elite gets 0 free bags", 0, b4.getFreeBagsAllowed());
        assertEquals("ECONOMY non-elite charges 20kg * $15 = $300", 300.0, b4.getExcessFee());
    }

    private static void testSeatHoldEngine() {
        System.out.println("\n--- Testing Seat Availability & Hold Check (if / else) ---");
        AircraftSeatMap seatMap = AircraftSeatMap.createDefaultLayout("TEST-FL");

        // 1. Available unheld seat locks for 10 minutes
        SeatHoldEngine.SeatHoldResponse resp1 = SeatHoldEngine.checkAndHoldSeat(seatMap, "3C", "PASS-1");
        assertTrue("Available seat 3C holds successfully", resp1.isSuccess());
        assertEquals("Hold duration is 600s", 600L, resp1.getHoldDurationSeconds());
        assertTrue("Seat is marked held", seatMap.getSeat("3C").isHeld());
        assertTrue("Seat is no longer available", !seatMap.getSeat("3C").isAvailable());

        // 2. Already held seat fails and provides aisle suggestions
        SeatHoldEngine.SeatHoldResponse resp2 = SeatHoldEngine.checkAndHoldSeat(seatMap, "3C", "PASS-2");
        assertTrue("Already held seat returns failure", !resp2.isSuccess());
        assertEquals("Failure message matches blueprint prompt", "Seat no longer available", resp2.getMessage());
        assertTrue("Alternative aisle suggestions provided", !resp2.getAlternativeAisles().isEmpty());
        assertTrue("Suggestions do not include the locked seat 3C", !resp2.getAlternativeAisles().contains("3C"));
    }

    private static void testCancellationRefundEngine() {
        System.out.println("\n--- Testing Cancellation Refund Eligibility (if / else if / else) ---");
        double fare = 600.0;

        // 1. >= 48 hours and refundable -> 100% refund
        RefundResult r1 = CancellationRefundEngine.evaluateRefundEligibility(50.0, true, fare);
        assertEquals(">=48h refundable gives 100% refund", RefundResult.RefundEligibility.FULL_REFUND_100, r1.getEligibility());
        assertEquals("Cash refund is full amount", fare, r1.getCashRefundAmount());
        assertEquals("Voucher amount is 0", 0.0, r1.getVoucherAmount());

        // 2. >= 24 hours -> 50% travel voucher
        RefundResult r2 = CancellationRefundEngine.evaluateRefundEligibility(25.0, true, fare);
        assertEquals(">=24h gives 50% voucher", RefundResult.RefundEligibility.TRAVEL_VOUCHER_50, r2.getEligibility());
        assertEquals("Cash refund is 0", 0.0, r2.getCashRefundAmount());
        assertEquals("Voucher amount is 50% ($300)", 300.0, r2.getVoucherAmount());

        // 3. < 24 hours -> Non-refundable
        RefundResult r3 = CancellationRefundEngine.evaluateRefundEligibility(18.0, true, fare);
        assertEquals("<24h flags as non-refundable", RefundResult.RefundEligibility.NON_REFUNDABLE, r3.getEligibility());
        assertEquals("Cash refund is 0", 0.0, r3.getCashRefundAmount());
        assertEquals("Voucher amount is 0", 0.0, r3.getVoucherAmount());
    }

    private static void testLoyaltyTierDispatcher() {
        System.out.println("\n--- Testing Loyalty Tier Multiplier (switch-case) ---");
        double distance = 1000.0;

        // 1. PLATINUM: 3.0x multiplier + Lounge Pass
        LoyaltyReward plat = LoyaltyTierDispatcher.calculateByTierString("PLATINUM", distance);
        assertEquals("PLATINUM multiplier is 3.0x", 3.0, plat.getMultiplier());
        assertEquals("PLATINUM total miles is 3000", 3000.0, plat.getTotalMilesEarned());
        assertTrue("PLATINUM has lounge pass", plat.hasLoungePass());
        assertTrue("PLATINUM has priority boarding", plat.hasPriorityBoarding());

        // 2. GOLD: 2.0x multiplier + Priority Boarding
        LoyaltyReward gold = LoyaltyTierDispatcher.calculateByTierString("GOLD", distance);
        assertEquals("GOLD multiplier is 2.0x", 2.0, gold.getMultiplier());
        assertEquals("GOLD total miles is 2000", 2000.0, gold.getTotalMilesEarned());
        assertTrue("GOLD has no lounge pass", !gold.hasLoungePass());
        assertTrue("GOLD has priority boarding", gold.hasPriorityBoarding());

        // 3. SILVER: 1.5x multiplier
        LoyaltyReward silver = LoyaltyTierDispatcher.calculateByTierString("SILVER", distance);
        assertEquals("SILVER multiplier is 1.5x", 1.5, silver.getMultiplier());
        assertEquals("SILVER total miles is 1500", 1500.0, silver.getTotalMilesEarned());

        // 4. DEFAULT/REGULAR: 1.0x base miles
        LoyaltyReward reg = LoyaltyTierDispatcher.calculateByTierString("REGULAR", distance);
        assertEquals("REGULAR multiplier is 1.0x", 1.0, reg.getMultiplier());
        assertEquals("REGULAR total miles is 1000", 1000.0, reg.getTotalMilesEarned());
    }

    private static void testItineraryCostEvaluator() {
        System.out.println("\n--- Testing Multi-Leg Itinerary Cost Evaluator (for-loop) ---");
        LocalDateTime dep = LocalDateTime.now().plusDays(2);
        FlightLeg leg1 = new FlightLeg("L1", "AA-1", "A", "B", dep, dep.plusHours(2), 100.0, 20.0, 10.0, 500.0);
        FlightLeg leg2 = new FlightLeg("L2", "AA-2", "B", "C", dep.plusHours(3), dep.plusHours(5), 150.0, 30.0, 15.0, 800.0);

        TripItinerary itinerary = new TripItinerary("ITIN-1", List.of(leg1, leg2));
        ItineraryCostSummary summary = ItineraryCostEvaluator.evaluateItineraryCost(itinerary);

        assertEquals("Total legs is 2", 2, summary.getTotalLegs());
        assertEquals("Total base fare is 250.0", 250.0, summary.getTotalBaseFare());
        assertEquals("Total tax is 50.0", 50.0, summary.getTotalAirportTax());
        assertEquals("Total fuel is 25.0", 25.0, summary.getTotalFuelSurcharge());
        assertEquals("Grand total is 325.0", 325.0, summary.getGrandTotalCost());
        assertEquals("Total air time is 240m", 240L, summary.getTotalFlightDurationMinutes());
        assertEquals("Total layover is 60m", 60L, summary.getTotalLayoverDurationMinutes());
    }

    private static void testExpiredSeatReleaser() {
        System.out.println("\n--- Testing Expired Reservation Seat Releaser (while-loop) ---");
        AircraftSeatMap seatMap = AircraftSeatMap.createDefaultLayout("TEST-REL");

        Seat s1 = seatMap.getSeat("6A");
        Seat s2 = seatMap.getSeat("6B");
        Seat s3 = seatMap.getSeat("6C");

        // Expired holds
        s1.lockSeat("P1", -10);
        s2.lockSeat("P2", -5);
        // Valid hold
        s3.lockSeat("P3", 600);

        assertTrue("Expired seats detected before sweep", ExpiredSeatReleaser.hasExpiredHeldSeats(seatMap.getAllSeats()));

        ExpiredSeatReleaser.ReleaserSummary summary = ExpiredSeatReleaser.releaseExpiredHeldSeats(seatMap);

        assertEquals("Sweeper released exactly 2 expired seats", 2, summary.getReleasedCount());
        assertTrue("Seat 6A is now available", s1.isAvailable() && !s1.isHeld());
        assertTrue("Seat 6B is now available", s2.isAvailable() && !s2.isHeld());
        assertTrue("Seat 6C remains held", !s3.isAvailable() && s3.isHeld());
        assertTrue("No expired seats remaining", !ExpiredSeatReleaser.hasExpiredHeldSeats(seatMap.getAllSeats()));
    }
}
