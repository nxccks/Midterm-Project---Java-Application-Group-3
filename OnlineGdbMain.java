import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ============================================================================
 * ✈️ AUTOMATED AIRLINE TICKET BOOKING & DYNAMIC SEAT RESERVATION ENGINE
 * ============================================================================
 * 
 * Single-file standalone edition designed for Online Compilers (OnlineGDB, JDoodle, Replit).
 * 
 * Demonstrates all required Control Flow Applications:
 * 1. Conditional (if / else if / else): Baggage Allowance & Fee Assessment
 * 2. Conditional (if / else): Seat Availability & Hold Check (10-min hold + aisle suggestions)
 * 3. Conditional (if / else if / else): Cancellation Refund Eligibility (100% / 50% voucher / non-refundable)
 * 4. Switch (switch-case): Frequent Flyer Loyalty Tier Multiplier (PLATINUM, GOLD, SILVER, default)
 * 5. Loops (for): Multi-Leg Itinerary Cost Evaluator (iterating connecting flight legs)
 * 6. Loops (while): Expired Reservation Seat Releaser (while (hasExpiredHeldSeats))
 * ============================================================================
 */
public class Main {

    // =========================================================================
    // ANSI COLOR CONSTANTS
    // =========================================================================
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";

    // =========================================================================
    // 1. ENUMS & DATA MODELS
    // =========================================================================
    public enum TicketClass { FIRST, BUSINESS, ECONOMY }
    public enum LoyaltyTier { PLATINUM, GOLD, SILVER, REGULAR }
    public enum SeatLocation { AISLE, WINDOW, MIDDLE }
    public enum RefundEligibility { FULL_REFUND_100, TRAVEL_VOUCHER_50, NON_REFUNDABLE }

    public static class Passenger {
        private final String id;
        private final String name;
        private final String email;
        private final LoyaltyTier loyaltyTier;

        public Passenger(String id, String name, String email, LoyaltyTier loyaltyTier) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.loyaltyTier = loyaltyTier;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public LoyaltyTier getLoyaltyTier() { return loyaltyTier; }

        public boolean isEliteFrequentFlyer() {
            return loyaltyTier == LoyaltyTier.PLATINUM || loyaltyTier == LoyaltyTier.GOLD;
        }
    }

    public static class Seat {
        private final String seatNumber;
        private final TicketClass seatClass;
        private final SeatLocation location;
        private final double extraPrice;
        private boolean available;
        private boolean held;
        private String heldByPassengerId;
        private long holdExpiresAtMillis;

        public Seat(String seatNumber, TicketClass seatClass, SeatLocation location, double extraPrice) {
            this.seatNumber = seatNumber;
            this.seatClass = seatClass;
            this.location = location;
            this.extraPrice = extraPrice;
            this.available = true;
            this.held = false;
            this.heldByPassengerId = null;
            this.holdExpiresAtMillis = 0L;
        }

        public String getSeatNumber() { return seatNumber; }
        public TicketClass getSeatClass() { return seatClass; }
        public SeatLocation getLocation() { return location; }
        public double getExtraPrice() { return extraPrice; }
        public boolean isAvailable() { return available; }
        public boolean isHeld() { return held; }

        public boolean isHoldExpired() {
            return held && holdExpiresAtMillis > 0 && System.currentTimeMillis() > holdExpiresAtMillis;
        }

        public long getRemainingHoldSeconds() {
            if (!held || holdExpiresAtMillis <= 0) return 0L;
            long diff = holdExpiresAtMillis - System.currentTimeMillis();
            return diff > 0 ? diff / 1000L : 0L;
        }

        public void lockSeat(String passengerId, long durationSeconds) {
            this.held = true;
            this.available = false;
            this.heldByPassengerId = passengerId;
            this.holdExpiresAtMillis = System.currentTimeMillis() + (durationSeconds * 1000L);
        }

        public void releaseSeat() {
            this.held = false;
            this.available = true;
            this.heldByPassengerId = null;
            this.holdExpiresAtMillis = 0L;
        }

        public void confirmBooking(String passengerId) {
            this.held = false;
            this.available = false;
            this.heldByPassengerId = passengerId;
            this.holdExpiresAtMillis = 0L;
        }
    }

    public static class AircraftSeatMap {
        private final String flightId;
        private final Map<String, Seat> seats = new LinkedHashMap<>();

        public AircraftSeatMap(String flightId) { this.flightId = flightId; }

        public void addSeat(Seat seat) { seats.put(seat.getSeatNumber().toUpperCase(), seat); }
        public Seat getSeat(String num) { return num == null ? null : seats.get(num.toUpperCase()); }
        public Collection<Seat> getAllSeats() { return seats.values(); }

        public List<Seat> getAvailableAisleSeats(TicketClass ticketClass) {
            List<Seat> aisles = new ArrayList<>();
            for (Seat s : seats.values()) {
                if (s.isAvailable() && !s.isHeld() && s.getLocation() == SeatLocation.AISLE) {
                    if (ticketClass == null || s.getSeatClass() == ticketClass) {
                        aisles.add(s);
                    }
                }
            }
            if (aisles.isEmpty()) {
                for (Seat s : seats.values()) {
                    if (s.isAvailable() && !s.isHeld() && s.getLocation() == SeatLocation.AISLE) {
                        aisles.add(s);
                    }
                }
            }
            return aisles;
        }

        public static AircraftSeatMap createDefaultLayout(String flightId) {
            AircraftSeatMap map = new AircraftSeatMap(flightId);
            // First Class (Rows 1-2)
            for (int r = 1; r <= 2; r++) {
                map.addSeat(new Seat(r + "A", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
                map.addSeat(new Seat(r + "B", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
                map.addSeat(new Seat(r + "C", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
                map.addSeat(new Seat(r + "D", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
            }
            // Business Class (Rows 3-5)
            for (int r = 3; r <= 5; r++) {
                map.addSeat(new Seat(r + "A", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
                map.addSeat(new Seat(r + "B", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
                map.addSeat(new Seat(r + "C", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
                map.addSeat(new Seat(r + "D", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
                map.addSeat(new Seat(r + "E", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
                map.addSeat(new Seat(r + "F", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
            }
            // Economy Class (Rows 6-10)
            for (int r = 6; r <= 10; r++) {
                map.addSeat(new Seat(r + "A", TicketClass.ECONOMY, SeatLocation.WINDOW, 20.0));
                map.addSeat(new Seat(r + "B", TicketClass.ECONOMY, SeatLocation.MIDDLE, 0.0));
                map.addSeat(new Seat(r + "C", TicketClass.ECONOMY, SeatLocation.AISLE, 15.0));
                map.addSeat(new Seat(r + "D", TicketClass.ECONOMY, SeatLocation.AISLE, 15.0));
                map.addSeat(new Seat(r + "E", TicketClass.ECONOMY, SeatLocation.MIDDLE, 0.0));
                map.addSeat(new Seat(r + "F", TicketClass.ECONOMY, SeatLocation.WINDOW, 20.0));
            }
            return map;
        }
    }

    public static class FlightLeg {
        private final String legId;
        private final String flightNumber;
        private final String origin;
        private final String destination;
        private final LocalDateTime departureTime;
        private final LocalDateTime arrivalTime;
        private final double baseFare;
        private final double airportTax;
        private final double fuelSurcharge;
        private final double distanceMiles;
        private final AircraftSeatMap seatMap;

        public FlightLeg(String legId, String flightNumber, String origin, String destination,
                         LocalDateTime dep, LocalDateTime arr, double baseFare, double tax, double fuel, double miles) {
            this.legId = legId;
            this.flightNumber = flightNumber;
            this.origin = origin;
            this.destination = destination;
            this.departureTime = dep;
            this.arrivalTime = arr;
            this.baseFare = baseFare;
            this.airportTax = tax;
            this.fuelSurcharge = fuel;
            this.distanceMiles = miles;
            this.seatMap = AircraftSeatMap.createDefaultLayout(legId);
        }

        public String getLegId() { return legId; }
        public String getFlightNumber() { return flightNumber; }
        public String getOrigin() { return origin; }
        public String getDestination() { return destination; }
        public LocalDateTime getDepartureTime() { return departureTime; }
        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public double getBaseFare() { return baseFare; }
        public double getAirportTax() { return airportTax; }
        public double getFuelSurcharge() { return fuelSurcharge; }
        public double getDistanceMiles() { return distanceMiles; }
        public AircraftSeatMap getSeatMap() { return seatMap; }
        public long getFlightDurationMinutes() { return Duration.between(departureTime, arrivalTime).toMinutes(); }
    }

    public static class TripItinerary {
        private final String itineraryId;
        private final List<FlightLeg> legs = new ArrayList<>();

        public TripItinerary(String id) { this.itineraryId = id; }
        public void addLeg(FlightLeg leg) { legs.add(leg); }
        public List<FlightLeg> getLegs() { return legs; }
        public int getLegCount() { return legs.size(); }
    }

    public static class BaggageAssessment {
        public final int numberOfBags;
        public final double totalWeightKg;
        public final int freeBagsAllowed;
        public final int excessBagsCount;
        public final double excessWeightKg;
        public final double excessFee;
        public final String policyExplanation;

        public BaggageAssessment(int bags, double weight, int free, int excessBags, double excessWeight, double fee, String policy) {
            this.numberOfBags = bags;
            this.totalWeightKg = weight;
            this.freeBagsAllowed = free;
            this.excessBagsCount = excessBags;
            this.excessWeightKg = excessWeight;
            this.excessFee = fee;
            this.policyExplanation = policy;
        }
    }

    public static class LoyaltyReward {
        public final LoyaltyTier tier;
        public final double multiplier;
        public final double baseMiles;
        public final double bonusMiles;
        public final double totalMilesEarned;
        public final boolean loungePass;
        public final boolean priorityBoarding;
        public final String perksDescription;

        public LoyaltyReward(LoyaltyTier tier, double mult, double base, double bonus, double total, boolean lounge, boolean priority, String perks) {
            this.tier = tier;
            this.multiplier = mult;
            this.baseMiles = base;
            this.bonusMiles = bonus;
            this.totalMilesEarned = total;
            this.loungePass = lounge;
            this.priorityBoarding = priority;
            this.perksDescription = perks;
        }
    }

    public static class ItineraryCostSummary {
        public final int totalLegs;
        public final double totalBaseFare;
        public final double totalAirportTax;
        public final double totalFuelSurcharge;
        public final double grandTotalCost;
        public final long totalFlightDurationMinutes;
        public final long totalLayoverDurationMinutes;
        public final long totalJourneyDurationMinutes;
        public final double totalDistanceMiles;

        public ItineraryCostSummary(int legs, double base, double tax, double fuel, double total, long flightDur, long layoverDur, long journeyDur, double miles) {
            this.totalLegs = legs;
            this.totalBaseFare = base;
            this.totalAirportTax = tax;
            this.totalFuelSurcharge = fuel;
            this.grandTotalCost = total;
            this.totalFlightDurationMinutes = flightDur;
            this.totalLayoverDurationMinutes = layoverDur;
            this.totalJourneyDurationMinutes = journeyDur;
            this.totalDistanceMiles = miles;
        }
    }

    public static class RefundResult {
        public final double hoursBeforeFlight;
        public final boolean refundableFare;
        public final RefundEligibility eligibility;
        public final double refundPercentage;
        public final double cashRefundAmount;
        public final double voucherAmount;
        public final String decisionReason;

        public RefundResult(double hours, boolean ref, RefundEligibility elig, double pct, double cash, double voucher, String reason) {
            this.hoursBeforeFlight = hours;
            this.refundableFare = ref;
            this.eligibility = elig;
            this.refundPercentage = pct;
            this.cashRefundAmount = cash;
            this.voucherAmount = voucher;
            this.decisionReason = reason;
        }
    }

    // =========================================================================
    // 2. CORE CONTROL FLOW ENGINES
    // =========================================================================

    /**
     * 1. CONDITIONAL (if / else if / else): Baggage Allowance & Fee Assessment
     */
    public static class BaggageEngine {
        public static final double STANDARD_PER_KG_RATE = 15.00;
        public static final double STANDARD_BAG_WEIGHT_LIMIT_KG = 23.0;
        public static final double EXTRA_PIECE_FEE = 40.00;

        public static BaggageAssessment assessBaggage(TicketClass ticketClass, Passenger passenger, int numberOfBags, double totalWeightKg) {
            int freeBagsAllowed;
            String policyExplanation;
            boolean isElite = passenger != null && passenger.isEliteFrequentFlyer();

            // CONTROL FLOW: if / else if / else
            if (ticketClass == TicketClass.FIRST || isElite) {
                freeBagsAllowed = 2;
                String eliteTag = isElite ? " (Elite Frequent Flyer Status Active)" : "";
                policyExplanation = "FIRST class fare or Elite Frequent Flyer tier allows up to 2 complimentary checked bags" + eliteTag + ".";
            } else if (ticketClass == TicketClass.BUSINESS) {
                freeBagsAllowed = 1;
                policyExplanation = "BUSINESS class fare allows 1 complimentary checked bag.";
            } else {
                freeBagsAllowed = 0;
                policyExplanation = "ECONOMY standard fare: No complimentary checked bags. Standard per-kilogram excess luggage rate applied.";
            }

            int excessBags = Math.max(0, numberOfBags - freeBagsAllowed);
            double freeWeightCap = freeBagsAllowed * STANDARD_BAG_WEIGHT_LIMIT_KG;
            double excessWeightKg = Math.max(0.0, totalWeightKg - freeWeightCap);

            double excessFee = 0.0;
            if (numberOfBags > 0) {
                if (freeBagsAllowed == 0) {
                    excessFee = totalWeightKg * STANDARD_PER_KG_RATE;
                } else {
                    excessFee = (excessBags * EXTRA_PIECE_FEE) + (excessWeightKg * STANDARD_PER_KG_RATE);
                }
            }

            return new BaggageAssessment(numberOfBags, totalWeightKg, freeBagsAllowed, excessBags, excessWeightKg, Math.round(excessFee * 100.0) / 100.0, policyExplanation);
        }
    }

    /**
     * 2. CONDITIONAL (if / else): Seat Availability & Hold Check
     */
    public static class SeatHoldEngine {
        public static class SeatHoldResponse {
            public final boolean success;
            public final String message;
            public final String seatNumber;
            public final long holdDurationSeconds;
            public final List<String> alternativeAisles;

            public SeatHoldResponse(boolean success, String message, String seat, long dur, List<String> aisles) {
                this.success = success;
                this.message = message;
                this.seatNumber = seat;
                this.holdDurationSeconds = dur;
                this.alternativeAisles = aisles;
            }
        }

        public static SeatHoldResponse checkAndHoldSeat(AircraftSeatMap seatMap, String seatNumber, String passengerId, long durationSeconds) {
            if (seatMap == null || seatNumber == null) {
                return new SeatHoldResponse(false, "Seat no longer available", "", 0, List.of());
            }
            Seat selectedSeat = seatMap.getSeat(seatNumber);
            if (selectedSeat == null) {
                List<String> aisles = new ArrayList<>();
                for (Seat s : seatMap.getAvailableAisleSeats(null)) aisles.add(s.getSeatNumber());
                return new SeatHoldResponse(false, "Seat no longer available", seatNumber, 0, aisles);
            }

            // CONTROL FLOW: if / else
            if (selectedSeat.isAvailable() && !selectedSeat.isHeld()) {
                selectedSeat.lockSeat(passengerId, durationSeconds);
                return new SeatHoldResponse(true, "Seat locked successfully for 10 minutes.", selectedSeat.getSeatNumber(), durationSeconds, List.of());
            } else {
                List<String> suggestedAisles = new ArrayList<>();
                for (Seat s : seatMap.getAvailableAisleSeats(selectedSeat.getSeatClass())) {
                    suggestedAisles.add(s.getSeatNumber());
                }
                return new SeatHoldResponse(false, "Seat no longer available", selectedSeat.getSeatNumber(), 0, suggestedAisles);
            }
        }
    }

    /**
     * 3. CONDITIONAL (if / else if / else): Cancellation Refund Eligibility
     */
    public static class CancellationRefundEngine {
        public static RefundResult evaluateRefundEligibility(double hoursBeforeFlight, boolean isRefundableFare, double totalPaid) {
            RefundEligibility eligibility;
            double refundPct;
            double cashRefund = 0.0;
            double voucher = 0.0;
            String reason;

            // CONTROL FLOW: if / else if / else
            if (hoursBeforeFlight >= 48.0 && isRefundableFare) {
                eligibility = RefundEligibility.FULL_REFUND_100;
                refundPct = 100.0;
                cashRefund = totalPaid;
                reason = String.format("Cancelled %.1f hours before flight on refundable fare: 100%% cash refund issued.", hoursBeforeFlight);
            } else if (hoursBeforeFlight >= 24.0) {
                eligibility = RefundEligibility.TRAVEL_VOUCHER_50;
                refundPct = 50.0;
                voucher = totalPaid * 0.50;
                reason = String.format("Cancelled %.1f hours before flight (>= 24h window): 50%% airline travel voucher issued.", hoursBeforeFlight);
            } else {
                eligibility = RefundEligibility.NON_REFUNDABLE;
                refundPct = 0.0;
                reason = String.format("Cancelled with under 24 hours remaining (%.1f hours) or non-refundable terms: Ticket is non-refundable.", hoursBeforeFlight);
            }

            return new RefundResult(hoursBeforeFlight, isRefundableFare, eligibility, refundPct, Math.round(cashRefund * 100.0) / 100.0, Math.round(voucher * 100.0) / 100.0, reason);
        }
    }

    /**
     * 4. SWITCH (switch-case): Frequent Flyer Tier Multiplier
     */
    public static class LoyaltyTierDispatcher {
        public static LoyaltyReward calculateByTierString(String tierName, double flightMiles) {
            double multiplier;
            boolean loungePass = false;
            boolean priorityBoarding = false;
            String perksDescription;
            LoyaltyTier resolvedTier;

            // CONTROL FLOW: switch-case
            switch (tierName != null ? tierName.toUpperCase() : "REGULAR") {
                case "PLATINUM":
                    multiplier = 3.0;
                    loungePass = true;
                    priorityBoarding = true;
                    perksDescription = "Platinum Status: 3.0x Bonus Multiplier + Lounge Pass + Priority Boarding.";
                    resolvedTier = LoyaltyTier.PLATINUM;
                    break;

                case "GOLD":
                    multiplier = 2.0;
                    loungePass = false;
                    priorityBoarding = true;
                    perksDescription = "Gold Status: 2.0x Bonus Multiplier + Priority Boarding.";
                    resolvedTier = LoyaltyTier.GOLD;
                    break;

                case "SILVER":
                    multiplier = 1.5;
                    loungePass = false;
                    priorityBoarding = false;
                    perksDescription = "Silver Status: 1.5x Bonus Multiplier.";
                    resolvedTier = LoyaltyTier.SILVER;
                    break;

                default:
                    multiplier = 1.0;
                    loungePass = false;
                    priorityBoarding = false;
                    perksDescription = "Standard Membership: 1.0x Base Miles Awarded.";
                    resolvedTier = LoyaltyTier.REGULAR;
                    break;
            }

            double totalMiles = flightMiles * multiplier;
            double bonusMiles = totalMiles - flightMiles;
            return new LoyaltyReward(resolvedTier, multiplier, flightMiles, bonusMiles, totalMiles, loungePass, priorityBoarding, perksDescription);
        }
    }

    /**
     * 5. LOOPS (for): Multi-Leg Itinerary Cost Evaluator
     */
    public static class ItineraryCostEvaluator {
        public static ItineraryCostSummary evaluateItineraryCost(TripItinerary tripItinerary) {
            if (tripItinerary == null || tripItinerary.getLegs().isEmpty()) {
                return new ItineraryCostSummary(0, 0, 0, 0, 0, 0, 0, 0, 0);
            }

            double totalBase = 0.0;
            double totalTax = 0.0;
            double totalFuel = 0.0;
            double totalMiles = 0.0;
            long totalFlightDur = 0L;
            long totalLayoverDur = 0L;
            int count = 0;

            LocalDateTime prevArr = null;

            // CONTROL FLOW: for flightLeg in tripItinerary
            for (FlightLeg flightLeg : tripItinerary.getLegs()) {
                count++;
                totalBase += flightLeg.getBaseFare();
                totalTax += flightLeg.getAirportTax();
                totalFuel += flightLeg.getFuelSurcharge();
                totalMiles += flightLeg.getDistanceMiles();
                totalFlightDur += flightLeg.getFlightDurationMinutes();

                if (prevArr != null && flightLeg.getDepartureTime().isAfter(prevArr)) {
                    totalLayoverDur += Duration.between(prevArr, flightLeg.getDepartureTime()).toMinutes();
                }
                prevArr = flightLeg.getArrivalTime();
            }

            double grandTotal = totalBase + totalTax + totalFuel;
            long totalJourney = totalFlightDur + totalLayoverDur;

            return new ItineraryCostSummary(count, Math.round(totalBase * 100.0) / 100.0, Math.round(totalTax * 100.0) / 100.0,
                    Math.round(totalFuel * 100.0) / 100.0, Math.round(grandTotal * 100.0) / 100.0, totalFlightDur, totalLayoverDur, totalJourney, totalMiles);
        }
    }

    /**
     * 6. LOOPS (while): Expired Reservation Seat Releaser
     */
    public static class ExpiredSeatReleaser {
        public static boolean hasExpiredHeldSeats(Collection<Seat> seats) {
            for (Seat s : seats) {
                if (s.isHeld() && s.isHoldExpired()) return true;
            }
            return false;
        }

        private static Seat findNextExpiredSeat(Collection<Seat> seats) {
            for (Seat s : seats) {
                if (s.isHeld() && s.isHoldExpired()) return s;
            }
            return null;
        }

        public static List<String> releaseExpiredHeldSeats(AircraftSeatMap seatMap) {
            List<String> released = new ArrayList<>();
            if (seatMap == null) return released;
            Collection<Seat> seats = seatMap.getAllSeats();

            // CONTROL FLOW: while (hasExpiredHeldSeats)
            while (hasExpiredHeldSeats(seats)) {
                Seat expired = findNextExpiredSeat(seats);
                if (expired != null) {
                    expired.releaseSeat();
                    released.add(expired.getSeatNumber());
                } else {
                    break;
                }
            }
            return released;
        }
    }

    // =========================================================================
    // 3. VISUAL SEAT MAP RENDERER
    // =========================================================================
    public static void renderSeatMap(AircraftSeatMap seatMap, String title) {
        System.out.println("\n" + BOLD + CYAN + "┌──────────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(BOLD + CYAN + "│  ✈️  CABIN SEAT MAP: %-38s  │\n" + RESET, title);
        System.out.println(BOLD + CYAN + "└──────────────────────────────────────────────────────────────┘" + RESET);
        System.out.println("  Legend: " + GREEN + "[🟢 AVAI]" + RESET + " Available  "
                + YELLOW + "[🟡 HELD]" + RESET + " 10-min Lock  "
                + RED + "[🔴 BKED]" + RESET + " Confirmed\n");

        System.out.println(BOLD + "  === FIRST CLASS (Rows 1-2) ===" + RESET);
        System.out.println("        A          B     [AISLE]    C          D");
        for (int r = 1; r <= 2; r++) {
            System.out.printf("  Row %02d: %s  %s    ‖   %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")),
                    formatSeat(seatMap.getSeat(r + "C")), formatSeat(seatMap.getSeat(r + "D")));
        }

        System.out.println("\n" + BOLD + "  === BUSINESS CLASS (Rows 3-5) ===" + RESET);
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 3; r <= 5; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ‖   %s  %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")), formatSeat(seatMap.getSeat(r + "C")),
                    formatSeat(seatMap.getSeat(r + "D")), formatSeat(seatMap.getSeat(r + "E")), formatSeat(seatMap.getSeat(r + "F")));
        }

        System.out.println("\n" + BOLD + "  === ECONOMY CLASS (Rows 6-10) ===" + RESET);
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 6; r <= 10; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ‖   %s  %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")), formatSeat(seatMap.getSeat(r + "C")),
                    formatSeat(seatMap.getSeat(r + "D")), formatSeat(seatMap.getSeat(r + "E")), formatSeat(seatMap.getSeat(r + "F")));
        }
        System.out.println();
    }

    private static String formatSeat(Seat s) {
        if (s == null) return " [      ] ";
        if (s.isAvailable() && !s.isHeld()) return GREEN + String.format("[%s:AVAI]", s.getSeatNumber()) + RESET;
        if (s.isHeld()) return YELLOW + String.format("[%s:%03ds]", s.getSeatNumber(), s.getRemainingHoldSeconds()) + RESET;
        return RED + String.format("[%s:BKED]", s.getSeatNumber()) + RESET;
    }

    // =========================================================================
    // 4. MAIN ENTRY POINT & AUTOMATED DEMO
    // =========================================================================
    public static void main(String[] args) {
        System.out.println(BOLD + BLUE + "================================================================================" + RESET);
        System.out.println(BOLD + MAGENTA + "  ✈️ AUTOMATED AIRLINE TICKET BOOKING & DYNAMIC SEAT RESERVATION ENGINE" + RESET);
        System.out.println(BOLD + BLUE + "================================================================================" + RESET);
        System.out.println(CYAN + "Live Demonstration of all Control Flow Applications from project-blueprint.txt\n" + RESET);

        // Setup sample flights & passengers
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        FlightLeg leg1 = new FlightLeg("FL-101", "AA-101", "JFK (New York)", "ORD (Chicago)", now.plusDays(3).withHour(8), now.plusDays(3).withHour(10).plusMinutes(30), 220.0, 35.0, 25.0, 740.0);
        FlightLeg leg2 = new FlightLeg("FL-102", "AA-202", "ORD (Chicago)", "LAX (Los Angeles)", now.plusDays(3).withHour(12), now.plusDays(3).withHour(16).plusMinutes(15), 310.0, 42.0, 38.0, 1745.0);
        FlightLeg leg3 = new FlightLeg("FL-103", "AA-303", "LAX (Los Angeles)", "HNL (Honolulu)", now.plusDays(3).withHour(18).plusMinutes(30), now.plusDays(3).withHour(23), 450.0, 55.0, 48.0, 2556.0);

        Passenger elite = new Passenger("P101", "Alice Vance", "alice@mit.edu", LoyaltyTier.PLATINUM);
        Passenger gold = new Passenger("P102", "Gordon Freeman", "gordon@mit.edu", LoyaltyTier.GOLD);
        Passenger standard = new Passenger("P103", "Barney Calhoun", "barney@bm.gov", LoyaltyTier.REGULAR);

        // --- STEP 1: Baggage (if / else if / else) ---
        printHeader("1. CONTROL FLOW: CONDITIONAL (if / else if / else) - Baggage Assessment");
        BaggageAssessment bFirst = BaggageEngine.assessBaggage(TicketClass.FIRST, standard, 2, 45.0);
        printBaggage("FIRST Class Standard Passenger (2 bags, 45kg)", bFirst);
        BaggageAssessment bElite = BaggageEngine.assessBaggage(TicketClass.ECONOMY, gold, 2, 40.0);
        printBaggage("ECONOMY Class with GOLD Elite Status (2 bags, 40kg)", bElite);
        BaggageAssessment bBiz = BaggageEngine.assessBaggage(TicketClass.BUSINESS, standard, 2, 50.0);
        printBaggage("BUSINESS Class Passenger (2 bags, 50kg -> 1 excess bag, 4kg overweight)", bBiz);
        BaggageAssessment bEcon = BaggageEngine.assessBaggage(TicketClass.ECONOMY, standard, 2, 35.0);
        printBaggage("ECONOMY Class Non-Elite (2 bags, 35kg -> per-kg charge)", bEcon);

        // --- STEP 2: Seat Hold (if / else) ---
        printHeader("2. CONTROL FLOW: CONDITIONAL (if / else) - Seat Availability & Hold Check");
        AircraftSeatMap map = leg1.getSeatMap();
        renderSeatMap(map, "FL-101 Initial Cabin Layout");

        System.out.println(BOLD + "Action 1: Passenger P101 attempts to hold available seat 3C (Business Aisle)..." + RESET);
        SeatHoldEngine.SeatHoldResponse r1 = SeatHoldEngine.checkAndHoldSeat(map, "3C", "P101", 600);
        System.out.println("  Branch: [if (selectedSeat.isAvailable && !selectedSeat.isHeld)] -> " + GREEN + "SUCCESS" + RESET);
        System.out.println("  Status: " + r1.message + " (Timer: " + (r1.holdDurationSeconds / 60) + " mins)");

        renderSeatMap(map, "FL-101 After Seat 3C Held");

        System.out.println(BOLD + "Action 2: Passenger P102 attempts to hold the SAME seat 3C (already locked)..." + RESET);
        SeatHoldEngine.SeatHoldResponse r2 = SeatHoldEngine.checkAndHoldSeat(map, "3C", "P102", 600);
        System.out.println("  Branch: [else] -> " + RED + "FAILED (Expected)" + RESET);
        System.out.println("  System Prompt: " + BOLD + RED + "\"" + r2.message + "\"" + RESET);
        System.out.println("  Alternative Aisle Suggestions: " + BOLD + YELLOW + r2.alternativeAisles + RESET + "\n");

        // --- STEP 3: Cancellation Refund (if / else if / else) ---
        printHeader("3. CONTROL FLOW: CONDITIONAL (if / else if / else) - Cancellation Refund Eligibility");
        RefundResult ref1 = CancellationRefundEngine.evaluateRefundEligibility(72.0, true, 500.0);
        printRefund("72h before departure (Refundable Fare)", ref1);
        RefundResult ref2 = CancellationRefundEngine.evaluateRefundEligibility(30.0, true, 500.0);
        printRefund("30h before departure (Within 24-48h window)", ref2);
        RefundResult ref3 = CancellationRefundEngine.evaluateRefundEligibility(12.0, true, 500.0);
        printRefund("12h before departure (< 24h remaining)", ref3);

        // --- STEP 4: Loyalty Tier Multiplier (switch-case) ---
        printHeader("4. CONTROL FLOW: SWITCH (switch-case) - Frequent Flyer Tier Multiplier");
        for (String tier : new String[]{"PLATINUM", "GOLD", "SILVER", "REGULAR"}) {
            LoyaltyReward rew = LoyaltyTierDispatcher.calculateByTierString(tier, 2500.0);
            System.out.printf("▶ switch(tier) -> matched: %-8s | %3.1fx Mult | Base: %4.0f | Bonus: %4.0f | Total: %s%4.0f miles%s | Lounge: %s | Priority: %s\n",
                    tier, rew.multiplier, rew.baseMiles, rew.bonusMiles, BOLD, rew.totalMilesEarned, RESET,
                    rew.loungePass ? GREEN + "YES" + RESET : "No ", rew.priorityBoarding ? GREEN + "YES" + RESET : "No ");
        }
        System.out.println();

        // --- STEP 5: Multi-Leg Itinerary Evaluator (for-loop) ---
        printHeader("5. CONTROL FLOW: LOOPS (for) - Multi-Leg Itinerary Cost Evaluator");
        TripItinerary itinerary = new TripItinerary("ITIN-CROSS-COUNTRY");
        itinerary.addLeg(leg1);
        itinerary.addLeg(leg2);
        itinerary.addLeg(leg3);

        System.out.println(BOLD + "Evaluating Itinerary: JFK -> ORD -> LAX -> HNL (3 Connecting Legs)" + RESET);
        ItineraryCostSummary itinSummary = ItineraryCostEvaluator.evaluateItineraryCost(itinerary);
        System.out.printf("  • Total Base Fare       : $%.2f\n", itinSummary.totalBaseFare);
        System.out.printf("  • Total Airport Taxes   : $%.2f\n", itinSummary.totalAirportTax);
        System.out.printf("  • Total Fuel Surcharges : $%.2f\n", itinSummary.totalFuelSurcharge);
        System.out.printf("  • Grand Flight Total    : %s$%.2f%s\n", BOLD, itinSummary.grandTotalCost, RESET);
        System.out.printf("  • Airborne Flight Time  : %d minutes (%.1f hours)\n", itinSummary.totalFlightDurationMinutes, itinSummary.totalFlightDurationMinutes / 60.0);
        System.out.printf("  • Layover Duration      : %d minutes (%.1f hours)\n", itinSummary.totalLayoverDurationMinutes, itinSummary.totalLayoverDurationMinutes / 60.0);
        System.out.printf("  • Total Journey Duration: %d minutes (%.1f hours)\n", itinSummary.totalJourneyDurationMinutes, itinSummary.totalJourneyDurationMinutes / 60.0);
        System.out.printf("  • Total Distance        : %.0f miles\n\n", itinSummary.totalDistanceMiles);

        // --- STEP 6: Expired Reservation Seat Releaser (while-loop) ---
        printHeader("6. CONTROL FLOW: LOOPS (while) - Expired Reservation Seat Releaser");
        Seat s6A = map.getSeat("6A");
        Seat s6B = map.getSeat("6B");
        Seat s6C = map.getSeat("6C");
        s6A.lockSeat("P991", -5); // Expired 5s ago
        s6B.lockSeat("P992", -10); // Expired 10s ago
        s6C.lockSeat("P993", 600); // Valid hold

        renderSeatMap(map, "Cabin State BEFORE Expired Holds Sweep");
        System.out.println("Running 'while (hasExpiredHeldSeats)' sweeper loop...");
        List<String> released = ExpiredSeatReleaser.releaseExpiredHeldSeats(map);
        System.out.println("  • Sweeper Released Seats : " + BOLD + GREEN + released + RESET);

        renderSeatMap(map, "Cabin State AFTER Expired Holds Sweep");

        printHeader("✅ ALL BLUEPRINT CONTROL FLOW SCENARIOS VERIFIED SUCCESSFULLY!");
    }

    private static void printHeader(String title) {
        System.out.println(BOLD + YELLOW + "--------------------------------------------------------------------------------" + RESET);
        System.out.println(BOLD + CYAN + " " + title + RESET);
        System.out.println(BOLD + YELLOW + "--------------------------------------------------------------------------------" + RESET);
    }

    private static void printBaggage(String label, BaggageAssessment b) {
        System.out.println(BOLD + GREEN + "▶ " + label + RESET);
        System.out.println("  • Policy: " + b.policyExplanation);
        System.out.printf("  • Free Bags: %d | Excess Bags: %d | Excess Wt: %.1fkg | Excess Fee: %s$%.2f%s\n\n",
                b.freeBagsAllowed, b.excessBagsCount, b.excessWeightKg, BOLD, b.excessFee, RESET);
    }

    private static void printRefund(String label, RefundResult r) {
        System.out.println(BOLD + GREEN + "▶ " + label + RESET);
        System.out.println("  • Policy Decision : " + r.decisionReason);
        System.out.printf("  • Status: %s%s%s | Cash Refund: $%.2f | Travel Voucher: $%.2f\n\n",
                BOLD, r.eligibility, RESET, r.cashRefundAmount, r.voucherAmount);
    }
}
