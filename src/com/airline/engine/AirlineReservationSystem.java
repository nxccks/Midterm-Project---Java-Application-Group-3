package com.airline.engine;

import com.airline.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✈️ Automated Airline Ticket Booking & Dynamic Seat Reservation Engine
 * 
 * Central platform orchestrating:
 * 1. Tier-Based Rule Engine (Conditional checks: baggage, seats, refunds)
 * 2. Loyalty Tier Dispatcher (Switch-case: tier multipliers and perks)
 * 3. Itinerary Aggregate Processor (For-loops: multi-leg pricing and flight durations)
 * 4. Expired Hold Sweeper (While-loops: lapsed seat recovery)
 */
public class AirlineReservationSystem {

    private final Map<String, FlightLeg> flightRepository;
    private final Map<String, Passenger> passengerRepository;
    private final Map<String, Booking> bookingRepository;

    public AirlineReservationSystem() {
        this.flightRepository = new ConcurrentHashMap<>();
        this.passengerRepository = new ConcurrentHashMap<>();
        this.bookingRepository = new ConcurrentHashMap<>();
        initializeDemoData();
    }

    public void registerFlightLeg(FlightLeg leg) {
        flightRepository.put(leg.getLegId(), leg);
    }

    public FlightLeg getFlightLeg(String legId) {
        return flightRepository.get(legId);
    }

    public Collection<FlightLeg> getAllFlightLegs() {
        return flightRepository.values();
    }

    public void registerPassenger(Passenger passenger) {
        passengerRepository.put(passenger.getId(), passenger);
    }

    public Passenger getPassenger(String passengerId) {
        return passengerRepository.get(passengerId);
    }

    public Collection<Passenger> getAllPassengers() {
        return passengerRepository.values();
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.get(bookingId);
    }

    public Collection<Booking> getAllBookings() {
        return bookingRepository.values();
    }

    /**
     * Creates and initializes sample flight legs, passengers, and routes.
     */
    public void initializeDemoData() {
        flightRepository.clear();
        passengerRepository.clear();
        bookingRepository.clear();

        // Sample Passengers across all loyalty tiers
        registerPassenger(new Passenger("P101", "Alice Vance", "alice@example.com", LoyaltyTier.PLATINUM));
        registerPassenger(new Passenger("P102", "Gordon Freeman", "gordon@example.com", LoyaltyTier.GOLD));
        registerPassenger(new Passenger("P103", "Barney Calhoun", "barney@example.com", LoyaltyTier.SILVER));
        registerPassenger(new Passenger("P104", "Eli Vance", "eli@example.com", LoyaltyTier.REGULAR));

        // Sample Flights
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // Multi-leg route: JFK -> ORD -> LAX -> HNL
        FlightLeg leg1 = new FlightLeg(
                "FL-101", "AA-101", "JFK (New York)", "ORD (Chicago)",
                now.plusDays(3).withHour(8), now.plusDays(3).withHour(10).plusMinutes(30),
                220.00, 35.00, 25.00, 740.0
        );

        FlightLeg leg2 = new FlightLeg(
                "FL-102", "AA-202", "ORD (Chicago)", "LAX (Los Angeles)",
                now.plusDays(3).withHour(12).plusMinutes(0), now.plusDays(3).withHour(16).plusMinutes(15),
                310.00, 42.00, 38.00, 1745.0
        );

        FlightLeg leg3 = new FlightLeg(
                "FL-103", "AA-303", "LAX (Los Angeles)", "HNL (Honolulu)",
                now.plusDays(3).withHour(18).plusMinutes(30), now.plusDays(3).withHour(23).plusMinutes(0),
                450.00, 55.00, 48.00, 2556.0
        );

        // Direct Route: LHR -> DXB
        FlightLeg leg4 = new FlightLeg(
                "FL-201", "EK-008", "LHR (London)", "DXB (Dubai)",
                now.plusDays(2).withHour(14), now.plusDays(2).withHour(23).plusMinutes(30),
                680.00, 85.00, 60.00, 3420.0
        );

        registerFlightLeg(leg1);
        registerFlightLeg(leg2);
        registerFlightLeg(leg3);
        registerFlightLeg(leg4);
    }

    /**
     * Builds a TripItinerary from a sequence of flight leg IDs.
     */
    public TripItinerary buildItinerary(String itineraryId, List<String> legIds) {
        TripItinerary itinerary = new TripItinerary(itineraryId);
        for (String legId : legIds) {
            FlightLeg leg = flightRepository.get(legId);
            if (leg != null) {
                itinerary.addLeg(leg);
            }
        }
        return itinerary;
    }

    /**
     * Step 1: Evaluates Itinerary Cost using For-Loop.
     */
    public ItineraryCostSummary evaluateItinerary(TripItinerary itinerary) {
        return ItineraryCostEvaluator.evaluateItineraryCost(itinerary);
    }

    /**
     * Step 2: Evaluates Seat Availability and Holds Seat using Conditional (if/else).
     */
    public SeatHoldEngine.SeatHoldResponse holdSeat(String legId, String seatNumber, String passengerId) {
        FlightLeg leg = flightRepository.get(legId);
        if (leg == null) {
            return new SeatHoldEngine.SeatHoldResponse(false, "Flight leg not found", seatNumber, 0, List.of());
        }
        return SeatHoldEngine.checkAndHoldSeat(leg.getSeatMap(), seatNumber, passengerId);
    }

    /**
     * Step 3: Assesses Baggage Allowance & Fees using Conditional (if/else if/else).
     */
    public BaggageAssessment assessBaggage(TicketClass ticketClass, Passenger passenger, int bags, double weightKg) {
        return BaggageEngine.assessBaggage(ticketClass, passenger, bags, weightKg);
    }

    /**
     * Step 4: Calculates Frequent Flyer Rewards using Switch-Case.
     */
    public LoyaltyReward calculateLoyaltyReward(Passenger passenger, double distanceMiles) {
        return LoyaltyTierDispatcher.calculateLoyaltyReward(passenger, distanceMiles);
    }

    /**
     * Step 5: Completes Booking and confirms seats permanently.
     */
    public Booking createAndConfirmBooking(String bookingId,
                                          Passenger passenger,
                                          TripItinerary itinerary,
                                          TicketClass ticketClass,
                                          Map<String, String> legSeatSelections,
                                          int checkedBags,
                                          double totalBagWeightKg,
                                          boolean refundableFare) {

        Booking booking = new Booking(bookingId, passenger, itinerary, ticketClass, refundableFare);

        // 1. Evaluate Itinerary Cost
        ItineraryCostSummary costSummary = ItineraryCostEvaluator.evaluateItineraryCost(itinerary);
        booking.setCostSummary(costSummary);

        // 2. Assess Baggage
        BaggageAssessment baggage = BaggageEngine.assessBaggage(ticketClass, passenger, checkedBags, totalBagWeightKg);
        booking.setBaggageAssessment(baggage);

        // 3. Dispatch Loyalty Perks
        LoyaltyReward loyalty = LoyaltyTierDispatcher.calculateLoyaltyReward(passenger, costSummary.getTotalDistanceMiles());
        booking.setLoyaltyReward(loyalty);

        // 4. Confirm seats across legs
        double seatExtras = 0.0;
        for (Map.Entry<String, String> entry : legSeatSelections.entrySet()) {
            String legId = entry.getKey();
            String seatNum = entry.getValue();
            FlightLeg leg = flightRepository.get(legId);
            if (leg != null) {
                Seat seat = leg.getSeatMap().getSeat(seatNum);
                if (seat != null) {
                    seat.confirmBooking(passenger.getId());
                    booking.assignSeat(legId, seatNum);
                    seatExtras += seat.getExtraPrice();
                }
            }
        }

        // 5. Total Paid Calculation
        double totalPaid = costSummary.getGrandTotalCost() + baggage.getExcessFee() + seatExtras;
        booking.setTotalAmountPaid(Math.round(totalPaid * 100.0) / 100.0);
        booking.setStatus(Booking.Status.CONFIRMED);

        bookingRepository.put(booking.getBookingId(), booking);
        return booking;
    }

    /**
     * Step 6: Evaluates and processes Cancellation Refund using Conditional (if/else if/else).
     */
    public RefundResult cancelBooking(String bookingId, LocalDateTime simulatedCurrentTime) {
        Booking booking = bookingRepository.get(bookingId);
        if (booking == null) {
            return new RefundResult(0.0, false, RefundResult.RefundEligibility.NON_REFUNDABLE, 0.0, 0.0, 0.0, "Booking not found.");
        }

        // Release all confirmed seats
        for (Map.Entry<String, String> entry : booking.getSelectedSeats().entrySet()) {
            FlightLeg leg = flightRepository.get(entry.getKey());
            if (leg != null) {
                Seat seat = leg.getSeatMap().getSeat(entry.getValue());
                if (seat != null) {
                    seat.releaseSeat();
                }
            }
        }

        return CancellationRefundEngine.cancelAndProcessRefund(booking, simulatedCurrentTime);
    }

    /**
     * Step 7: Sweeps and releases all expired held seats across all flights using While-Loop.
     */
    public ExpiredSeatReleaser.ReleaserSummary sweepAllExpiredSeats() {
        int totalReleased = 0;
        List<String> allReleased = new ArrayList<>();

        for (FlightLeg leg : flightRepository.values()) {
            ExpiredSeatReleaser.ReleaserSummary summary = ExpiredSeatReleaser.releaseExpiredHeldSeats(leg.getSeatMap());
            totalReleased += summary.getReleasedCount();
            for (String s : summary.getReleasedSeatNumbers()) {
                allReleased.add(leg.getFlightNumber() + ":" + s);
            }
        }

        return new ExpiredSeatReleaser.ReleaserSummary(totalReleased, allReleased);
    }
}
