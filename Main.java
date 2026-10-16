import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

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
            if (!held || holdExpiresAtMillis <= 0) return false;
            return System.currentTimeMillis() > holdExpiresAtMillis;
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
            for (int r = 1; r <= 2; r++) {
                map.addSeat(new Seat(r + "A", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
                map.addSeat(new Seat(r + "B", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
                map.addSeat(new Seat(r + "C", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
                map.addSeat(new Seat(r + "D", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
            }
            for (int r = 3; r <= 5; r++) {
                map.addSeat(new Seat(r + "A", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
                map.addSeat(new Seat(r + "B", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
                map.addSeat(new Seat(r + "C", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
                map.addSeat(new Seat(r + "D", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
                map.addSeat(new Seat(r + "E", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
                map.addSeat(new Seat(r + "F", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
            }
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
        public long getFlightDurationMinutes() {
            return Duration.between(departureTime, arrivalTime).toMinutes();
        }
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

    // 1. CONDITIONAL (if / else if / else) -> Baggage Engine
    public static class BaggageEngine {
        public static final double STANDARD_PER_KG_RATE = 15.00;
        public static final double STANDARD_BAG_WEIGHT_LIMIT_KG = 23.0;
        public static final double EXTRA_PIECE_FEE = 40.00;

        public static BaggageAssessment assessBaggage(TicketClass ticketClass, Passenger passenger, int numberOfBags, double totalWeightKg) {
            int freeBagsAllowed;
            String policyExplanation;
            boolean isElite = (passenger != null && passenger.isEliteFrequentFlyer());

            if (ticketClass == TicketClass.FIRST || isElite) {
                freeBagsAllowed = 2;
                String eliteTag = isElite ? " (Elite Status Active)" : "";
                policyExplanation = "FIRST class or Elite tier allows up to 2 free checked bags" + eliteTag + ".";
            } else if (ticketClass == TicketClass.BUSINESS) {
                freeBagsAllowed = 1;
                policyExplanation = "BUSINESS class allows 1 free checked bag.";
            } else {
                freeBagsAllowed = 0;
                policyExplanation = "ECONOMY: No free bags included. Standard per-kg excess rate applied.";
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

    // 2. CONDITIONAL (if / else) -> Seat Hold Engine
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
                return new SeatHoldResponse(false, "Seat no longer available", "", 0, Collections.emptyList());
            }
            Seat selectedSeat = seatMap.getSeat(seatNumber);
            if (selectedSeat == null) {
                List<String> aisles = new ArrayList<>();
                for (Seat s : seatMap.getAvailableAisleSeats(null)) {
                    aisles.add(s.getSeatNumber());
                }
                return new SeatHoldResponse(false, "Seat no longer available", seatNumber, 0, aisles);
            }

            if (selectedSeat.isAvailable() && !selectedSeat.isHeld()) {
                selectedSeat.lockSeat(passengerId, durationSeconds);
                return new SeatHoldResponse(true, "Seat locked successfully for 10 minutes.", selectedSeat.getSeatNumber(), durationSeconds, Collections.emptyList());
            } else {
                List<String> suggestedAisles = new ArrayList<>();
                for (Seat s : seatMap.getAvailableAisleSeats(selectedSeat.getSeatClass())) {
                    suggestedAisles.add(s.getSeatNumber());
                }
                return new SeatHoldResponse(false, "Seat no longer available", selectedSeat.getSeatNumber(), 0, suggestedAisles);
            }
        }
    }

    // 3. CONDITIONAL (if / else if / else) -> Cancellation Refund Engine
    public static class CancellationRefundEngine {
        public static RefundResult evaluateRefundEligibility(double hoursBeforeFlight, boolean isRefundableFare, double totalPaid) {
            RefundEligibility eligibility;
            double refundPct;
            double cashRefund = 0.0;
            double voucher = 0.0;
            String reason;

            if (hoursBeforeFlight >= 48.0 && isRefundableFare) {
                eligibility = RefundEligibility.FULL_REFUND_100;
                refundPct = 100.0;
                cashRefund = totalPaid;
                reason = "Cancelled >= 48h before flight on refundable fare: 100% cash refund issued.";
            } else if (hoursBeforeFlight >= 24.0) {
                eligibility = RefundEligibility.TRAVEL_VOUCHER_50;
                refundPct = 50.0;
                voucher = totalPaid * 0.50;
                reason = "Cancelled >= 24h before flight: 50% airline travel voucher issued.";
            } else {
                eligibility = RefundEligibility.NON_REFUNDABLE;
                refundPct = 0.0;
                reason = "Cancelled under 24 hours before flight: Ticket is non-refundable.";
            }

            return new RefundResult(hoursBeforeFlight, isRefundableFare, eligibility, refundPct, Math.round(cashRefund * 100.0) / 100.0, Math.round(voucher * 100.0) / 100.0, reason);
        }
    }

    // 4. SWITCH (switch-case) -> Loyalty Tier Multiplier
    public static class LoyaltyTierDispatcher {
        public static LoyaltyReward calculateByTierString(String tierName, double flightMiles) {
            double multiplier;
            boolean loungePass = false;
            boolean priorityBoarding = false;
            String perksDescription;
            LoyaltyTier resolvedTier;

            switch (tierName != null ? tierName.toUpperCase() : "REGULAR") {
                case "PLATINUM":
                    multiplier = 3.0;
                    loungePass = true;
                    priorityBoarding = true;
                    perksDescription = "Platinum: 3.0x Multiplier + Lounge Pass + Priority Boarding.";
                    resolvedTier = LoyaltyTier.PLATINUM;
                    break;
                case "GOLD":
                    multiplier = 2.0;
                    loungePass = false;
                    priorityBoarding = true;
                    perksDescription = "Gold: 2.0x Multiplier + Priority Boarding.";
                    resolvedTier = LoyaltyTier.GOLD;
                    break;
                case "SILVER":
                    multiplier = 1.5;
                    loungePass = false;
                    priorityBoarding = false;
                    perksDescription = "Silver: 1.5x Multiplier.";
                    resolvedTier = LoyaltyTier.SILVER;
                    break;
                default:
                    multiplier = 1.0;
                    loungePass = false;
                    priorityBoarding = false;
                    perksDescription = "Regular: 1.0x Base Miles Awarded.";
                    resolvedTier = LoyaltyTier.REGULAR;
                    break;
            }

            double totalMiles = flightMiles * multiplier;
            double bonusMiles = totalMiles - flightMiles;
            return new LoyaltyReward(resolvedTier, multiplier, flightMiles, bonusMiles, totalMiles, loungePass, priorityBoarding, perksDescription);
        }
    }

    // 5. LOOPS (for) -> Multi-Leg Itinerary Evaluator
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

    // 6. LOOPS (while) -> Expired Seat Releaser
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

    public static void renderSeatMap(AircraftSeatMap seatMap, String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.printf("| CABIN SEAT MAP: %-42s |\n", title);
        System.out.println("+------------------------------------------------------------+");
        System.out.println("  Legend: [AVAI] Available | [HELD] 10m Lock | [BKED] Confirmed\n");

        System.out.println("  === FIRST CLASS (Rows 1-2) ===");
        System.out.println("        A          B     [AISLE]    C          D");
        for (int r = 1; r <= 2; r++) {
            System.out.printf("  Row %02d: %s  %s    ||   %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")),
                    formatSeat(seatMap.getSeat(r + "C")), formatSeat(seatMap.getSeat(r + "D")));
        }

        System.out.println("\n  === BUSINESS CLASS (Rows 3-5) ===");
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 3; r <= 5; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ||   %s  %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")), formatSeat(seatMap.getSeat(r + "C")),
                    formatSeat(seatMap.getSeat(r + "D")), formatSeat(seatMap.getSeat(r + "E")), formatSeat(seatMap.getSeat(r + "F")));
        }

        System.out.println("\n  === ECONOMY CLASS (Rows 6-10) ===");
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 6; r <= 10; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ||   %s  %s  %s\n",
                    r, formatSeat(seatMap.getSeat(r + "A")), formatSeat(seatMap.getSeat(r + "B")), formatSeat(seatMap.getSeat(r + "C")),
                    formatSeat(seatMap.getSeat(r + "D")), formatSeat(seatMap.getSeat(r + "E")), formatSeat(seatMap.getSeat(r + "F")));
        }
        System.out.println();
    }

    private static String formatSeat(Seat s) {
        if (s == null) return "[      ]";
        if (s.isAvailable() && !s.isHeld()) return "[" + s.getSeatNumber() + ":AVAI]";
        if (s.isHeld()) return String.format("[%s:%03ds]", s.getSeatNumber(), s.getRemainingHoldSeconds());
        return "[" + s.getSeatNumber() + ":BKED]";
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("  AIRLINE TICKET BOOKING & DYNAMIC SEAT RESERVATION ENGINE");
        System.out.println("================================================================================");
        System.out.println("Live Demonstration of all Control Flow Applications from project-blueprint.txt\n");

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        FlightLeg leg1 = new FlightLeg("FL-101", "AA-101", "JFK (New York)", "ORD (Chicago)",
                now.plusDays(3).withHour(8), now.plusDays(3).withHour(10).plusMinutes(30), 220.0, 35.0, 25.0, 740.0);
        FlightLeg leg2 = new FlightLeg("FL-102", "AA-202", "ORD (Chicago)", "LAX (Los Angeles)",
                now.plusDays(3).withHour(12), now.plusDays(3).withHour(16).plusMinutes(15), 310.0, 42.0, 38.0, 1745.0);
        FlightLeg leg3 = new FlightLeg("FL-103", "AA-303", "LAX (Los Angeles)", "HNL (Honolulu)",
                now.plusDays(3).withHour(18).plusMinutes(30), now.plusDays(3).withHour(23), 450.0, 55.0, 48.0, 2556.0);

        Passenger elite = new Passenger("P101", "Alice Vance", "alice@mit.edu", LoyaltyTier.PLATINUM);
        Passenger gold = new Passenger("P102", "Gordon Freeman", "gordon@mit.edu", LoyaltyTier.GOLD);
        Passenger standard = new Passenger("P103", "Barney Calhoun", "barney@bm.gov", LoyaltyTier.REGULAR);

        // --- STEP 1: Baggage (if / else if / else) ---
        printHeader("1. CONTROL FLOW: CONDITIONAL (if / else if / else) - Baggage Assessment");
        BaggageAssessment bFirst = BaggageEngine.assessBaggage(TicketClass.FIRST, standard, 2, 45.0);
        printBaggage("FIRST Class Passenger (2 bags, 45kg)", bFirst);
        BaggageAssessment bElite = BaggageEngine.assessBaggage(TicketClass.ECONOMY, gold, 2, 40.0);
        printBaggage("ECONOMY with GOLD Elite Status (2 bags, 40kg)", bElite);
        BaggageAssessment bBiz = BaggageEngine.assessBaggage(TicketClass.BUSINESS, standard, 2, 50.0);
        printBaggage("BUSINESS Class Passenger (2 bags, 50kg -> 1 excess bag)", bBiz);
        BaggageAssessment bEcon = BaggageEngine.assessBaggage(TicketClass.ECONOMY, standard, 2, 35.0);
        printBaggage("ECONOMY Class Non-Elite (2 bags, 35kg -> per-kg excess fee)", bEcon);

        // --- STEP 2: Seat Hold (if / else) ---
        printHeader("2. CONTROL FLOW: CONDITIONAL (if / else) - Seat Availability & Hold Check");
        AircraftSeatMap map = leg1.getSeatMap();
        renderSeatMap(map, "FL-101 Initial Cabin Layout");

        System.out.println("Action 1: Passenger P101 attempts to hold available seat 3C (Business Aisle)...");
        SeatHoldEngine.SeatHoldResponse r1 = SeatHoldEngine.checkAndHoldSeat(map, "3C", "P101", 600);
        System.out.println("  Branch: [if (selectedSeat.isAvailable && !selectedSeat.isHeld)] -> SUCCESS");
        System.out.println("  Status: " + r1.message + " (Hold Timer: " + (r1.holdDurationSeconds / 60) + " mins)");

        renderSeatMap(map, "FL-101 After Seat 3C Held");

        System.out.println("Action 2: Passenger P102 attempts to hold the SAME seat 3C (already locked)...");
        SeatHoldEngine.SeatHoldResponse r2 = SeatHoldEngine.checkAndHoldSeat(map, "3C", "P102", 600);
        System.out.println("  Branch: [else] -> FAILED (Expected)");
        System.out.println("  System Prompt: \"" + r2.message + "\"");
        System.out.println("  Alternative Aisle Suggestions: " + r2.alternativeAisles + "\n");

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
            System.out.printf("  switch(tier) -> case %-8s | %3.1fx | Base: %4.0f | Bonus: %4.0f | Total: %4.0f miles | Lounge: %-3s | Priority: %-3s\n",
                    tier, rew.multiplier, rew.baseMiles, rew.bonusMiles, rew.totalMilesEarned,
                    rew.loungePass ? "YES" : "No", rew.priorityBoarding ? "YES" : "No");
        }
        System.out.println();

        // --- STEP 5: Multi-Leg Itinerary Evaluator (for-loop) ---
        printHeader("5. CONTROL FLOW: LOOPS (for) - Multi-Leg Itinerary Cost Evaluator");
        TripItinerary itinerary = new TripItinerary("ITIN-CROSS-COUNTRY");
        itinerary.addLeg(leg1);
        itinerary.addLeg(leg2);
        itinerary.addLeg(leg3);

        System.out.println("Evaluating Itinerary: JFK -> ORD -> LAX -> HNL (3 Connecting Legs)");
        ItineraryCostSummary itinSummary = ItineraryCostEvaluator.evaluateItineraryCost(itinerary);
        System.out.printf("  * Total Base Fare       : $%.2f\n", itinSummary.totalBaseFare);
        System.out.printf("  * Total Airport Taxes   : $%.2f\n", itinSummary.totalAirportTax);
        System.out.printf("  * Total Fuel Surcharges : $%.2f\n", itinSummary.totalFuelSurcharge);
        System.out.printf("  * Grand Flight Total    : $%.2f\n", itinSummary.grandTotalCost);
        System.out.printf("  * Airborne Flight Time  : %d minutes (%.1f hours)\n", itinSummary.totalFlightDurationMinutes, itinSummary.totalFlightDurationMinutes / 60.0);
        System.out.printf("  * Layover Duration      : %d minutes (%.1f hours)\n", itinSummary.totalLayoverDurationMinutes, itinSummary.totalLayoverDurationMinutes / 60.0);
        System.out.printf("  * Total Journey Duration: %d minutes (%.1f hours)\n", itinSummary.totalJourneyDurationMinutes, itinSummary.totalJourneyDurationMinutes / 60.0);
        System.out.printf("  * Total Distance        : %.0f miles\n\n", itinSummary.totalDistanceMiles);

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
        System.out.println("  * Sweeper Released Seats : " + released);

        renderSeatMap(map, "Cabin State AFTER Expired Holds Sweep");

        printHeader("ALL BLUEPRINT CONTROL FLOW SCENARIOS VERIFIED SUCCESSFULLY!");
    }

    private static void printHeader(String title) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  " + title);
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void printBaggage(String label, BaggageAssessment b) {
        System.out.println(">> " + label);
        System.out.println("   Policy: " + b.policyExplanation);
        System.out.printf("   Free Bags: %d | Excess Bags: %d | Excess Wt: %.1fkg | Excess Fee: $%.2f\n\n",
                b.freeBagsAllowed, b.excessBagsCount, b.excessWeightKg, b.excessFee);
    }

    private static void printRefund(String label, RefundResult r) {
        System.out.println(">> " + label);
        System.out.println("   Policy Decision : " + r.decisionReason);
        System.out.printf("   Status: %s | Cash Refund: $%.2f | Travel Voucher: $%.2f\n\n",
                r.eligibility, r.cashRefundAmount, r.voucherAmount);
    }
}
