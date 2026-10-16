package com.airline.cli;

import com.airline.engine.*;
import com.airline.model.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Interactive Terminal Interface for the Airline Ticket Booking Engine.
 */
public class InteractiveConsole {

    private final AirlineReservationSystem system;
    private final Scanner scanner;

    public InteractiveConsole(AirlineReservationSystem system) {
        this.system = system;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("👉 Select an option (1-9, or 0 to exit): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewFlightsAndSeatMap();
                    break;
                case "2":
                    handleSeatHold();
                    break;
                case "3":
                    handleItineraryCostEvaluation();
                    break;
                case "4":
                    handleBaggageAssessment();
                    break;
                case "5":
                    handleLoyaltyTierEvaluation();
                    break;
                case "6":
                    handleBookingCreation();
                    break;
                case "7":
                    handleTicketCancellation();
                    break;
                case "8":
                    handleSweepExpiredHolds();
                    break;
                case "9":
                    ScenarioDemoRunner.runAllScenarios();
                    break;
                case "0":
                    System.out.println("\n👋 Thank you for using the Airline Reservation Engine. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid option. Please choose between 0 and 9.");
                    break;
            }

            if (running) {
                System.out.println("\nPress ENTER to return to menu...");
                scanner.nextLine();
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n================================================================================");
        System.out.println(" ✈️  AIRLINE TICKET BOOKING & DYNAMIC SEAT RESERVATION SYSTEM");
        System.out.println("================================================================================");
        System.out.println("  1. 🛫 View Available Flights & Cabin Seat Maps");
        System.out.println("  2. 💺 Dynamic Seat Hold & Availability Check [if/else]");
        System.out.println("  3. 🗺️  Multi-Leg Itinerary Cost Evaluator [for-loop]");
        System.out.println("  4. 🧳 Baggage Allowance & Fee Assessment [if/else if/else]");
        System.out.println("  5. 🌟 Frequent Flyer Loyalty Tier Multiplier [switch-case]");
        System.out.println("  6. 💳 Book & Confirm Flight Reservation");
        System.out.println("  7. ❌ Cancel Reservation & Process Refund [if/else if/else]");
        System.out.println("  8. ⏱️  Sweep & Release Expired Seat Holds [while-loop]");
        System.out.println("  9. 🎯 Run All Automated Blueprint Scenarios");
        System.out.println("  0. 🚪 Exit");
        System.out.println("================================================================================");
    }

    private void viewFlightsAndSeatMap() {
        System.out.println("\n--- AVAILABLE FLIGHTS ---");
        for (FlightLeg leg : system.getAllFlightLegs()) {
            System.out.printf("[%s] %s: %s -> %s | Dep: %s | Base: $%.2f | Dur: %d min\n",
                    leg.getLegId(), leg.getFlightNumber(), leg.getOriginAirport(), leg.getDestinationAirport(),
                    leg.getDepartureTime(), leg.getBaseFare(), leg.getFlightDurationMinutes());
        }

        System.out.print("\nEnter Flight Leg ID to inspect seat map (or press ENTER to skip): ");
        String legId = scanner.nextLine().trim().toUpperCase();
        if (legId.isEmpty()) return;

        FlightLeg leg = system.getFlightLeg(legId);
        if (leg == null) {
            System.out.println("Flight leg not found.");
            return;
        }

        SeatMapVisualizer.renderSeatMap(leg.getSeatMap(), leg.getFlightNumber() + " (" + leg.getOriginAirport() + " -> " + leg.getDestinationAirport() + ")");

    }

    private void handleSeatHold() {
        System.out.println("\n--- SEAT AVAILABILITY & HOLD CHECK (if / else) ---");
        System.out.print("Enter Flight Leg ID (e.g., FL-101): ");
        String legId = scanner.nextLine().trim().toUpperCase();

        FlightLeg leg = system.getFlightLeg(legId);
        if (leg == null) {
            System.out.println("Flight leg not found.");
            return;
        }

        System.out.print("Enter Seat Number to lock (e.g., 1A, 3C, 6D): ");
        String seatNum = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Passenger ID (e.g., P101): ");
        String passId = scanner.nextLine().trim();

        SeatHoldEngine.SeatHoldResponse response = SeatHoldEngine.checkAndHoldSeat(leg.getSeatMap(), seatNum, passId);
        System.out.println("\nResult:");
        if (response.isSuccess()) {
            System.out.println("✅ " + response.getMessage());
            System.out.println("   Seat " + response.getSeatNumber() + " is now locked for " + (response.getHoldDurationSeconds() / 60) + " minutes.");
        } else {
            System.out.println("❌ " + response.getMessage());
            System.out.println("   💡 Recommended alternative aisle seats: " + response.getAlternativeAisles());
        }
    }

    private void handleItineraryCostEvaluation() {
        System.out.println("\n--- MULTI-LEG ITINERARY COST EVALUATOR (for-loop) ---");
        System.out.println("Available Legs: FL-101 (JFK->ORD), FL-102 (ORD->LAX), FL-103 (LAX->HNL), FL-201 (LHR->DXB)");
        System.out.print("Enter comma-separated Leg IDs for your route (e.g., FL-101,FL-102,FL-103): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        List<String> legIds = Arrays.stream(input.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .toList();

        TripItinerary itinerary = system.buildItinerary("ITIN-CUSTOM", legIds);
        if (itinerary.isEmpty()) {
            System.out.println("No valid legs found for itinerary.");
            return;
        }

        ItineraryCostSummary summary = system.evaluateItinerary(itinerary);
        System.out.println("\n📊 Multi-Leg Calculation Breakdown:");
        System.out.println("  • Connecting Legs Count  : " + summary.getTotalLegs());
        System.out.println("  • Aggregated Base Fare   : $" + String.format("%.2f", summary.getTotalBaseFare()));
        System.out.println("  • Local Airport Taxes    : $" + String.format("%.2f", summary.getTotalAirportTax()));
        System.out.println("  • Fuel Surcharges        : $" + String.format("%.2f", summary.getTotalFuelSurcharge()));
        System.out.println("  • Grand Total Flight Cost: $" + String.format("%.2f", summary.getGrandTotalCost()));
        System.out.println("  • Total Air Duration     : " + summary.getTotalFlightDurationMinutes() + " minutes");
        System.out.println("  • Total Layover Duration : " + summary.getTotalLayoverDurationMinutes() + " minutes");
        System.out.println("  • Total Travel Duration  : " + summary.getTotalJourneyDurationMinutes() + " minutes");
        System.out.println("  • Total Distance         : " + String.format("%.0f", summary.getTotalDistanceMiles()) + " miles");
    }

    private void handleBaggageAssessment() {
        System.out.println("\n--- BAGGAGE ALLOWANCE & FEE ASSESSMENT (if / else if / else) ---");
        System.out.print("Select Ticket Class (1: FIRST, 2: BUSINESS, 3: ECONOMY): ");
        String classChoice = scanner.nextLine().trim();
        TicketClass ticketClass = switch (classChoice) {
            case "1" -> TicketClass.FIRST;
            case "2" -> TicketClass.BUSINESS;
            default -> TicketClass.ECONOMY;
        };

        System.out.print("Is passenger an Elite Frequent Flyer? (y/n): ");
        boolean isElite = scanner.nextLine().trim().equalsIgnoreCase("y");

        Passenger passenger = new Passenger("TEMP-P", "Guest", "guest@example.com", isElite ? LoyaltyTier.GOLD : LoyaltyTier.REGULAR);

        System.out.print("Number of checked bags: ");
        int bags = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Total combined luggage weight (kg): ");
        double weight = Double.parseDouble(scanner.nextLine().trim());

        BaggageAssessment assessment = BaggageEngine.assessBaggage(ticketClass, passenger, bags, weight);

        System.out.println("\n🧳 Baggage Assessment Summary:");
        System.out.println("  • Applied Policy    : " + assessment.getPolicyExplanation());
        System.out.println("  • Free Bags Allowed : " + assessment.getFreeBagsAllowed());
        System.out.println("  • Excess Bags Count : " + assessment.getExcessBagsCount());
        System.out.println("  • Excess Weight     : " + assessment.getExcessWeightKg() + " kg");
        System.out.println("  • Total Excess Fee  : $" + String.format("%.2f", assessment.getExcessFee()));
    }

    private void handleLoyaltyTierEvaluation() {
        System.out.println("\n--- LOYALTY TIER DISPATCHER (switch-case) ---");
        System.out.print("Enter Membership Tier (PLATINUM, GOLD, SILVER, REGULAR): ");
        String tier = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Flight Distance in Miles (e.g., 2500): ");
        double miles = Double.parseDouble(scanner.nextLine().trim());

        LoyaltyReward reward = LoyaltyTierDispatcher.calculateByTierString(tier, miles);

        System.out.println("\n🌟 Loyalty Calculation Result:");
        System.out.println("  • Multiplier Applied: " + reward.getMultiplier() + "x");
        System.out.println("  • Base Miles        : " + reward.getBaseMiles());
        System.out.println("  • Bonus Miles       : " + reward.getBonusMiles());
        System.out.println("  • Total Miles Earned: " + reward.getTotalMilesEarned());
        System.out.println("  • Lounge Access Pass: " + (reward.hasLoungePass() ? "YES" : "NO"));
        System.out.println("  • Priority Boarding : " + (reward.hasPriorityBoarding() ? "YES" : "NO"));
        System.out.println("  • Perks Summary     : " + reward.getPerksDescription());
    }

    private void handleBookingCreation() {
        System.out.println("\n--- COMPLETE FLIGHT RESERVATION ---");
        System.out.print("Enter Passenger ID (P101, P102, P103, P104): ");
        String pid = scanner.nextLine().trim().toUpperCase();
        Passenger passenger = system.getPassenger(pid);
        if (passenger == null) {
            passenger = new Passenger(pid, "Guest Passenger", "guest@airline.com", LoyaltyTier.REGULAR);
            system.registerPassenger(passenger);
        }

        System.out.print("Enter comma-separated flight legs (e.g., FL-101,FL-102): ");
        String legsInput = scanner.nextLine().trim();
        List<String> legs = Arrays.stream(legsInput.split(",")).map(String::trim).map(String::toUpperCase).toList();

        TripItinerary itinerary = system.buildItinerary("ITIN-" + System.currentTimeMillis(), legs);

        System.out.print("Select Class (1: FIRST, 2: BUSINESS, 3: ECONOMY): ");
        String c = scanner.nextLine().trim();
        TicketClass tc = switch (c) {
            case "1" -> TicketClass.FIRST;
            case "2" -> TicketClass.BUSINESS;
            default -> TicketClass.ECONOMY;
        };

        Map<String, String> seats = new HashMap<>();
        for (FlightLeg leg : itinerary.getLegs()) {
            System.out.print("Select seat for leg " + leg.getFlightNumber() + " (" + leg.getOriginAirport() + "->" + leg.getDestinationAirport() + "): ");
            String seat = scanner.nextLine().trim().toUpperCase();
            seats.put(leg.getLegId(), seat);
        }

        System.out.print("Number of checked bags: ");
        int bags = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Total bag weight (kg): ");
        double weight = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Is this a refundable fare? (y/n): ");
        boolean refundable = scanner.nextLine().trim().equalsIgnoreCase("y");

        String bookingId = "BK-" + (System.currentTimeMillis() % 100000);
        Booking booking = system.createAndConfirmBooking(bookingId, passenger, itinerary, tc, seats, bags, weight, refundable);

        System.out.println("\n🎉 BOOKING CONFIRMED!");
        System.out.println("  • Booking Reference: " + booking.getBookingId());
        System.out.println("  • Passenger Name   : " + booking.getPassenger().getName());
        System.out.println("  • Grand Total Paid : $" + booking.getTotalAmountPaid());
        System.out.println("  • Miles Credited   : " + booking.getLoyaltyReward().getTotalMilesEarned());
    }

    private void handleTicketCancellation() {
        System.out.println("\n--- CANCELLATION REFUND ELIGIBILITY (if / else if / else) ---");
        System.out.print("Enter Booking Reference ID (or enter simulated hours directly): ");
        String bid = scanner.nextLine().trim();

        Booking booking = system.getBooking(bid);
        if (booking != null) {
            System.out.print("Enter simulated hours remaining before flight (e.g. 72, 36, 12): ");
            double hours = Double.parseDouble(scanner.nextLine().trim());
            RefundResult result = CancellationRefundEngine.evaluateRefundEligibility(hours, booking.isRefundableFare(), booking.getTotalAmountPaid());
            booking.setStatus(Booking.Status.CANCELLED);
            booking.setRefundResult(result);

            System.out.println("\nRefund Assessment:");
            System.out.println("  • Result       : " + result.getEligibility());
            System.out.println("  • Cash Refund  : $" + result.getCashRefundAmount());
            System.out.println("  • Voucher Given: $" + result.getVoucherAmount());
            System.out.println("  • Policy Reason: " + result.getDecisionReason());
        } else {
            System.out.print("Enter simulated ticket purchase price ($): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter hours before flight: ");
            double hours = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Is refundable fare? (y/n): ");
            boolean ref = scanner.nextLine().trim().equalsIgnoreCase("y");

            RefundResult result = CancellationRefundEngine.evaluateRefundEligibility(hours, ref, price);
            System.out.println("\nRefund Assessment:");
            System.out.println("  • Result       : " + result.getEligibility());
            System.out.println("  • Cash Refund  : $" + result.getCashRefundAmount());
            System.out.println("  • Voucher Given: $" + result.getVoucherAmount());
            System.out.println("  • Policy Reason: " + result.getDecisionReason());
        }
    }

    private void handleSweepExpiredHolds() {
        System.out.println("\n--- SWEEP EXPIRED SEAT HOLDS (while-loop) ---");
        ExpiredSeatReleaser.ReleaserSummary summary = system.sweepAllExpiredSeats();
        System.out.println("🧹 Expired Seat Sweep Complete:");
        System.out.println("  • Total Released: " + summary.getReleasedCount());
        System.out.println("  • Released Seats: " + summary.getReleasedSeatNumbers());
    }
}
