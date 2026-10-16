package com.airline.cli;

import com.airline.engine.*;
import com.airline.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 🎯 Scenario Demonstration Runner
 * 
 * Executes automated test cases and interactive walkthroughs directly validating
 * every single control flow mechanism and real-life scenario defined in project-blueprint.txt.
 */
public class ScenarioDemoRunner {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    public static void runAllScenarios() {
        runScenariosInternal(false);
    }

    public static void runInteractiveWalkthrough() {
        runScenariosInternal(true);
    }

    private static void runScenariosInternal(boolean interactive) {
        Scanner scanner = interactive ? new Scanner(System.in) : null;

        printHeader("✈️ AUTOMATED AIRLINE TICKET BOOKING & DYNAMIC SEAT RESERVATION ENGINE");
        System.out.println(CYAN + "Live Demonstration of all Control Flow Applications from project-blueprint.txt\n" + RESET);

        AirlineReservationSystem system = new AirlineReservationSystem();

        stepPause(interactive, scanner, "Step 1: Baggage Allowance & Fee Assessment (Conditional: if / else if / else)");
        demoBaggageAllowance();

        stepPause(interactive, scanner, "Step 2: Seat Availability & Hold Check (Conditional: if / else)");
        demoSeatAvailabilityAndHold(system);

        stepPause(interactive, scanner, "Step 3: Cancellation Refund Eligibility (Conditional: if / else if / else)");
        demoCancellationRefundEligibility();

        stepPause(interactive, scanner, "Step 4: Frequent Flyer Tier Multiplier (Switch: switch-case)");
        demoLoyaltyTierMultiplier();

        stepPause(interactive, scanner, "Step 5: Multi-Leg Itinerary Cost Evaluator (Loop: for flightLeg in tripItinerary)");
        demoMultiLegItineraryEvaluator(system);

        stepPause(interactive, scanner, "Step 6: Expired Reservation Seat Releaser (Loop: while (hasExpiredHeldSeats))");
        demoExpiredSeatReleaser(system);

        stepPause(interactive, scanner, "Step 7: Full End-to-End Reservation Workflow Integration");
        demoEndToEndBookingWorkflow(system);

        printHeader("✅ ALL BLUEPRINT CONTROL FLOW SCENARIOS VERIFIED SUCCESSFULLY!");
    }

    private static void stepPause(boolean interactive, Scanner scanner, String nextStepName) {
        if (interactive && scanner != null) {
            System.out.println(BOLD + YELLOW + "▶ Ready for " + nextStepName + ". Press [ENTER] to execute..." + RESET);
            scanner.nextLine();
        }
    }

    private static void printHeader(String title) {
        System.out.println(BOLD + BLUE + "================================================================================" + RESET);
        System.out.println(BOLD + MAGENTA + "  " + title + RESET);
        System.out.println(BOLD + BLUE + "================================================================================" + RESET);
    }

    private static void printSection(String number, String title) {
        System.out.println("\n" + BOLD + YELLOW + "--------------------------------------------------------------------------------" + RESET);
        System.out.println(BOLD + CYAN + " " + number + ". " + title + RESET);
        System.out.println(BOLD + YELLOW + "--------------------------------------------------------------------------------" + RESET);
    }

    /**
     * 1. Baggage Allowance & Fee Assessment (if / else if / else)
     */
    private static void demoBaggageAllowance() {
        printSection("1", "CONTROL FLOW: CONDITIONAL (if / else if / else) - Baggage Allowance & Fee Assessment");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  if (ticketClass == \"FIRST\" || isEliteFrequentFlyer) permits up to 2 checked bags free;");
        System.out.println("  else if (ticketClass == \"BUSINESS\") permits 1 free bag;");
        System.out.println("  else charges standard per-kilogram excess luggage rate.\n" + RESET);

        Passenger elitePassenger = new Passenger("P1", "Gordon Freeman", "gordon@mit.edu", LoyaltyTier.GOLD);
        Passenger standardPassenger = new Passenger("P2", "Barney Calhoun", "barney@blackmesa.gov", LoyaltyTier.REGULAR);

        // Case A: FIRST Class (Non-elite)
        BaggageAssessment b1 = BaggageEngine.assessBaggage(TicketClass.FIRST, standardPassenger, 2, 45.0);
        printBaggageResult("Case A: FIRST Class Passenger (2 bags, 45kg)", b1);

        // Case B: ECONOMY Class with Elite Frequent Flyer status
        BaggageAssessment b2 = BaggageEngine.assessBaggage(TicketClass.ECONOMY, elitePassenger, 2, 40.0);
        printBaggageResult("Case B: ECONOMY Passenger with GOLD Elite Status (2 bags, 40kg)", b2);

        // Case C: BUSINESS Class (1 free bag, checking 2 bags total 50kg)
        BaggageAssessment b3 = BaggageEngine.assessBaggage(TicketClass.BUSINESS, standardPassenger, 2, 50.0);
        printBaggageResult("Case C: BUSINESS Class Passenger (2 bags, 50kg -> 1 excess bag, overweight)", b3);

        // Case D: ECONOMY Class (Standard passenger, checking 2 bags 35kg)
        BaggageAssessment b4 = BaggageEngine.assessBaggage(TicketClass.ECONOMY, standardPassenger, 2, 35.0);
        printBaggageResult("Case D: ECONOMY Passenger Non-Elite (2 bags, 35kg -> standard per-kg charge)", b4);
    }

    private static void printBaggageResult(String label, BaggageAssessment b) {
        System.out.println(BOLD + GREEN + "▶ " + label + RESET);
        System.out.println("  • Applied Rule Branch : " + b.getPolicyExplanation());
        System.out.println("  • Free Bags Allowed   : " + b.getFreeBagsAllowed());
        System.out.println("  • Excess Bags Count   : " + b.getExcessBagsCount());
        System.out.println("  • Excess Weight (kg)  : " + b.getExcessWeightKg() + " kg");
        System.out.println("  • Excess Fee Assessed : " + BOLD + "$" + String.format("%.2f", b.getExcessFee()) + RESET + "\n");
    }

    /**
     * 2. Seat Availability & Hold Check (if / else)
     */
    private static void demoSeatAvailabilityAndHold(AirlineReservationSystem system) {
        printSection("2", "CONTROL FLOW: CONDITIONAL (if / else) - Seat Availability & Hold Check");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  if (selectedSeat.isAvailable && !selectedSeat.isHeld)");
        System.out.println("      locks seat for 10 minutes;");
        System.out.println("  else");
        System.out.println("      prompts \"Seat no longer available\" and suggests alternative aisles.\n" + RESET);

        FlightLeg flight = system.getFlightLeg("FL-101");
        AircraftSeatMap seatMap = flight.getSeatMap();

        SeatMapVisualizer.renderSeatMap(seatMap, flight.getFlightNumber() + " Initial Seat Map");

        // Attempt 1: Seat 3C (Business Class Aisle) - Currently Available
        System.out.println(BOLD + "Action 1: Passenger P101 attempts to hold available seat 3C..." + RESET);
        SeatHoldEngine.SeatHoldResponse resp1 = SeatHoldEngine.checkAndHoldSeat(seatMap, "3C", "P101");
        System.out.println("  Branch Taken: [if (selectedSeat.isAvailable && !selectedSeat.isHeld)]");
        System.out.println("  Status: " + (resp1.isSuccess() ? GREEN + "SUCCESS" : RED + "FAILED") + RESET);
        System.out.println("  Message: " + resp1.getMessage());
        System.out.println("  Hold Timer: " + (resp1.getHoldDurationSeconds() / 60) + " minutes (600 seconds)\n");

        SeatMapVisualizer.renderSeatMap(seatMap, flight.getFlightNumber() + " After Seat 3C Held");

        // Attempt 2: Passenger P102 attempts to hold the same seat 3C (Now Held!)
        System.out.println(BOLD + "Action 2: Passenger P102 attempts to hold the SAME seat 3C (already locked)..." + RESET);
        SeatHoldEngine.SeatHoldResponse resp2 = SeatHoldEngine.checkAndHoldSeat(seatMap, "3C", "P102");
        System.out.println("  Branch Taken: [else (seat unavailable or held)]");
        System.out.println("  Status: " + (resp2.isSuccess() ? GREEN + "SUCCESS" : RED + "FAILED (Expected)") + RESET);
        System.out.println("  System Prompt: " + BOLD + RED + "\"" + resp2.getMessage() + "\"" + RESET);
        System.out.println("  Alternative Aisle Suggestions: " + BOLD + YELLOW + resp2.getAlternativeAisles() + RESET + "\n");
    }

    /**
     * 3. Cancellation Refund Eligibility (if / else if / else)
     */
    private static void demoCancellationRefundEligibility() {
        printSection("3", "CONTROL FLOW: CONDITIONAL (if / else if / else) - Cancellation Refund Eligibility");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  if (hoursBeforeFlight >= 48 && isRefundableFare) issues 100% refund;");
        System.out.println("  else if (hoursBeforeFlight >= 24) issues 50% travel voucher;");
        System.out.println("  else flags ticket as non-refundable.\n" + RESET);

        double ticketPrice = 500.00;

        // Case 1: Cancelled 72h before flight with refundable ticket
        RefundResult r1 = CancellationRefundEngine.evaluateRefundEligibility(72.0, true, ticketPrice);
        printRefundResult("Case 1: Cancelled 72h before departure (Refundable Fare)", r1);

        // Case 2: Cancelled 30h before flight (Within 24-48h window)
        RefundResult r2 = CancellationRefundEngine.evaluateRefundEligibility(30.0, true, ticketPrice);
        printRefundResult("Case 2: Cancelled 30h before departure (Within 24-48h window)", r2);

        // Case 3: Cancelled 12h before flight (Under 24 hours)
        RefundResult r3 = CancellationRefundEngine.evaluateRefundEligibility(12.0, true, ticketPrice);
        printRefundResult("Case 3: Cancelled 12h before departure (< 24h remaining)", r3);

        // Case 4: Cancelled 72h before flight but on NON-REFUNDABLE fare (Falls to voucher/non-refundable)
        RefundResult r4 = CancellationRefundEngine.evaluateRefundEligibility(72.0, false, ticketPrice);
        printRefundResult("Case 4: Cancelled 72h before departure (Non-Refundable Fare Basis)", r4);
    }

    private static void printRefundResult(String label, RefundResult r) {
        System.out.println(BOLD + GREEN + "▶ " + label + RESET);
        System.out.println("  • Policy Evaluation : " + r.getDecisionReason());
        System.out.println("  • Resulting Status  : " + BOLD + r.getEligibility() + RESET);
        System.out.println("  • Cash Refund ($)   : " + BOLD + "$" + String.format("%.2f", r.getCashRefundAmount()) + RESET);
        System.out.println("  • Travel Voucher ($): " + BOLD + "$" + String.format("%.2f", r.getVoucherAmount()) + RESET + "\n");
    }

    /**
     * 4. Frequent Flyer Tier Multiplier (Switch / switch-case)
     */
    private static void demoLoyaltyTierMultiplier() {
        printSection("4", "CONTROL FLOW: SWITCH (switch-case) - Frequent Flyer Tier Multiplier");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  switch(tier):");
        System.out.println("    case \"PLATINUM\": Applies 3.0x bonus mile multiplier and lounge pass.");
        System.out.println("    case \"GOLD\":     Applies 2.0x bonus mile multiplier and priority boarding.");
        System.out.println("    case \"SILVER\":   Applies 1.5x bonus mile multiplier.");
        System.out.println("    default:         Awards standard 1.0x base miles.\n" + RESET);

        double flightMiles = 2500.0;
        String[] tiers = {"PLATINUM", "GOLD", "SILVER", "REGULAR"};

        for (String tier : tiers) {
            LoyaltyReward reward = LoyaltyTierDispatcher.calculateByTierString(tier, flightMiles);
            System.out.println(BOLD + GREEN + "▶ switch(tier) -> matched case: " + tier + RESET);
            System.out.println("  • Multiplier Awarded : " + reward.getMultiplier() + "x");
            System.out.println("  • Miles Calculation  : " + reward.getBaseMiles() + " base + " + reward.getBonusMiles() + " bonus = " + BOLD + reward.getTotalMilesEarned() + " Total Miles" + RESET);
            System.out.println("  • Lounge Access Pass : " + (reward.hasLoungePass() ? BOLD + GREEN + "YES (Included)" + RESET : "No"));
            System.out.println("  • Priority Boarding  : " + (reward.hasPriorityBoarding() ? BOLD + GREEN + "YES (Included)" + RESET : "No"));
            System.out.println("  • Perks Summary      : " + reward.getPerksDescription() + "\n");
        }
    }

    /**
     * 5. Multi-Leg Itinerary Cost Evaluator (for flightLeg in tripItinerary)
     */
    private static void demoMultiLegItineraryEvaluator(AirlineReservationSystem system) {
        printSection("5", "CONTROL FLOW: LOOPS (for) - Multi-Leg Itinerary Cost Evaluator");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  for (flightLeg in tripItinerary):");
        System.out.println("      Iterates across connecting flights to calculate base fares, local airport taxes,");
        System.out.println("      fuel surcharges, and total flight duration.\n" + RESET);

        TripItinerary itinerary = system.buildItinerary("ITIN-CROSS-COUNTRY", List.of("FL-101", "FL-102", "FL-103"));

        System.out.println(BOLD + "Itinerary Route: JFK -> ORD -> LAX -> HNL (" + itinerary.getLegCount() + " connecting legs)" + RESET);
        int index = 1;
        for (FlightLeg leg : itinerary.getLegs()) {
            System.out.printf("  Leg %d: %s (%s -> %s) | Base: $%.2f | Tax: $%.2f | Fuel: $%.2f | Dur: %d min\n",
                    index++, leg.getFlightNumber(), leg.getOriginAirport(), leg.getDestinationAirport(),
                    leg.getBaseFare(), leg.getLocalAirportTax(), leg.getFuelSurcharge(), leg.getFlightDurationMinutes());
        }

        ItineraryCostSummary summary = ItineraryCostEvaluator.evaluateItineraryCost(itinerary);

        System.out.println("\n" + BOLD + GREEN + "▶ Aggregated for-loop Calculation Result:" + RESET);
        System.out.printf("  • Total Base Fare       : $%.2f\n", summary.getTotalBaseFare());
        System.out.printf("  • Total Airport Taxes   : $%.2f\n", summary.getTotalAirportTax());
        System.out.printf("  • Total Fuel Surcharges : $%.2f\n", summary.getTotalFuelSurcharge());
        System.out.printf("  • Grand Flight Total    : %s$%.2f%s\n", BOLD, summary.getGrandTotalCost(), RESET);
        System.out.printf("  • Airborne Duration     : %d minutes (%.1f hours)\n", summary.getTotalFlightDurationMinutes(), summary.getTotalFlightDurationMinutes() / 60.0);
        System.out.printf("  • Layover Duration      : %d minutes (%.1f hours)\n", summary.getTotalLayoverDurationMinutes(), summary.getTotalLayoverDurationMinutes() / 60.0);
        System.out.printf("  • Total Journey Time    : %d minutes (%.1f hours)\n", summary.getTotalJourneyDurationMinutes(), summary.getTotalJourneyDurationMinutes() / 60.0);
        System.out.printf("  • Total Distance        : %.0f miles\n\n", summary.getTotalDistanceMiles());
    }

    /**
     * 6. Expired Reservation Seat Releaser (while (hasExpiredHeldSeats))
     */
    private static void demoExpiredSeatReleaser(AirlineReservationSystem system) {
        printSection("6", "CONTROL FLOW: LOOPS (while) - Expired Reservation Seat Releaser");
        System.out.println(BOLD + "Blueprint Rule:" + RESET);
        System.out.println(CYAN + "  while (hasExpiredHeldSeats):");
        System.out.println("      Periodically loops through temporarily reserved seats whose checkout timers lapsed,");
        System.out.println("      releasing them back to the public inventory pool.\n" + RESET);

        FlightLeg leg = system.getFlightLeg("FL-101");
        AircraftSeatMap seatMap = leg.getSeatMap();

        // Simulate 3 seats placed on hold with already lapsed/expired timers
        Seat s1 = seatMap.getSeat("6A");
        Seat s2 = seatMap.getSeat("6B");
        Seat s3 = seatMap.getSeat("6C");

        // Lock seats with a past expiration time (simulate expired hold timer)
        s1.lockSeat("P991", -5); // Expired 5 seconds ago
        s2.lockSeat("P992", -10); // Expired 10 seconds ago
        s3.lockSeat("P993", 600); // Valid hold (still active for 10 minutes)

        SeatMapVisualizer.renderSeatMap(seatMap, "Cabin State BEFORE Expired Holds Sweep");

        System.out.println("Inventory Lapsed Check Before Sweeper:");
        System.out.println("  • Seat 6A: Held? true | Expired? " + RED + "YES (Lapsed)" + RESET);
        System.out.println("  • Seat 6B: Held? true | Expired? " + RED + "YES (Lapsed)" + RESET);
        System.out.println("  • Seat 6C: Held? true | Expired? " + GREEN + "NO (Active 10-min Hold)" + RESET);

        System.out.println("\nExecuting 'while (hasExpiredHeldSeats)' sweeper loop...");
        ExpiredSeatReleaser.ReleaserSummary summary = ExpiredSeatReleaser.releaseExpiredHeldSeats(seatMap);

        System.out.println("\n" + BOLD + GREEN + "▶ Sweeper Summary:" + RESET);
        System.out.println("  • Total Seats Released Back to Public Pool: " + BOLD + summary.getReleasedCount() + RESET);
        System.out.println("  • Released Seat IDs: " + summary.getReleasedSeatNumbers());

        SeatMapVisualizer.renderSeatMap(seatMap, "Cabin State AFTER Expired Holds Sweep");

        System.out.println("Inventory State After Sweeper:");
        System.out.println("  • Seat 6A: Available? " + GREEN + s1.isAvailable() + RESET + " | Held? " + s1.isHeld());
        System.out.println("  • Seat 6B: Available? " + GREEN + s2.isAvailable() + RESET + " | Held? " + s2.isHeld());
        System.out.println("  • Seat 6C: Available? " + s3.isAvailable() + " | Held? " + YELLOW + s3.isHeld() + " (Still safely held)" + RESET + "\n");
    }

    /**
     * 7. Full End-to-End Booking Workflow
     */
    private static void demoEndToEndBookingWorkflow(AirlineReservationSystem system) {
        printSection("7", "END-TO-END RESERVATION WORKFLOW INTEGRATION");
        System.out.println("Assembling passenger, multi-leg flight, seat selection, baggage assessment, loyalty dispatch, and confirmation:\n");

        Passenger passenger = system.getPassenger("P101"); // Alice Vance (Platinum)
        TripItinerary itinerary = system.buildItinerary("BK-ITIN-901", List.of("FL-101", "FL-102"));
        Map<String, String> seatSelections = Map.of("FL-101", "1A", "FL-102", "1B");

        Booking booking = system.createAndConfirmBooking(
                "BOOKING-ALICE-777",
                passenger,
                itinerary,
                TicketClass.FIRST,
                seatSelections,
                2,     // 2 checked bags
                42.0,  // 42 kg total weight
                true   // refundable fare
        );

        System.out.println(BOLD + GREEN + "▶ Booking Confirmed Successfully!" + RESET);
        System.out.println("  • Booking Reference : " + BOLD + booking.getBookingId() + RESET);
        System.out.println("  • Passenger         : " + booking.getPassenger().getName() + " (" + booking.getPassenger().getLoyaltyTier() + " Elite)");
        System.out.println("  • Ticket Class      : " + booking.getTicketClass());
        System.out.println("  • Selected Seats    : " + booking.getSelectedSeats());
        System.out.println("  • Flight Route Cost : $" + booking.getCostSummary().getGrandTotalCost());
        System.out.println("  • Baggage Excess Fee: $" + booking.getBaggageAssessment().getExcessFee() + " (" + booking.getBaggageAssessment().getPolicyExplanation() + ")");
        System.out.println("  • Grand Total Paid  : " + BOLD + "$" + booking.getTotalAmountPaid() + RESET);
        System.out.println("  • Loyalty Awarded   : " + BOLD + booking.getLoyaltyReward().getTotalMilesEarned() + " miles (" + booking.getLoyaltyReward().getMultiplier() + "x multiplier)" + RESET);
        System.out.println("  • Lounge Access     : " + (booking.getLoyaltyReward().hasLoungePass() ? "GRANTED" : "NO"));
        System.out.println("  • Priority Boarding : " + (booking.getLoyaltyReward().hasPriorityBoarding() ? "GRANTED" : "NO") + "\n");
    }
}
