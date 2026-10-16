package com.airline.engine;

import com.airline.model.AircraftSeatMap;
import com.airline.model.Seat;
import com.airline.model.TicketClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Control Flow Application: Conditional (if / else)
 * 
 * Blueprint Specification:
 * Seat Availability & Hold Check:
 * if (selectedSeat.isAvailable && !selectedSeat.isHeld)
 *     locks seat for 10 minutes;
 * else
 *     prompts "Seat no longer available" and suggests alternative aisles.
 */
public class SeatHoldEngine {

    public static final long DEFAULT_HOLD_DURATION_SECONDS = 600L; // 10 minutes (600 seconds)

    public static class SeatHoldResponse {
        private final boolean success;
        private final String message;
        private final String seatNumber;
        private final long holdDurationSeconds;
        private final List<String> alternativeAisles;

        public SeatHoldResponse(boolean success, String message, String seatNumber,
                                long holdDurationSeconds, List<String> alternativeAisles) {
            this.success = success;
            this.message = message;
            this.seatNumber = seatNumber;
            this.holdDurationSeconds = holdDurationSeconds;
            this.alternativeAisles = alternativeAisles;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public long getHoldDurationSeconds() {
            return holdDurationSeconds;
        }

        public List<String> getAlternativeAisles() {
            return alternativeAisles;
        }

        @Override
        public String toString() {
            if (success) {
                return String.format("SUCCESS: Seat %s locked for %d minutes (%d seconds).",
                        seatNumber, holdDurationSeconds / 60, holdDurationSeconds);
            } else {
                return String.format("PROMPT: %s. Suggested aisle alternatives: %s",
                        message, alternativeAisles.isEmpty() ? "None available" : String.join(", ", alternativeAisles));
            }
        }
    }

    /**
     * Attempts to reserve and lock a seat using conditional control flow.
     */
    public static SeatHoldResponse checkAndHoldSeat(AircraftSeatMap seatMap,
                                                   String seatNumber,
                                                   String passengerId) {
        return checkAndHoldSeat(seatMap, seatNumber, passengerId, DEFAULT_HOLD_DURATION_SECONDS);
    }

    /**
     * Attempts to reserve and lock a seat with custom duration.
     */
    public static SeatHoldResponse checkAndHoldSeat(AircraftSeatMap seatMap,
                                                   String seatNumber,
                                                   String passengerId,
                                                   long holdDurationSeconds) {
        if (seatMap == null || seatNumber == null) {
            return new SeatHoldResponse(false, "Seat no longer available", "", 0, List.of());
        }

        Seat selectedSeat = seatMap.getSeat(seatNumber);

        // Check if seat exists on aircraft
        if (selectedSeat == null) {
            List<String> suggestedAisles = extractSeatNumbers(seatMap.getAvailableAisleSeats(null));
            return new SeatHoldResponse(false, "Seat no longer available (Invalid Seat ID)", seatNumber, 0, suggestedAisles);
        }

        // =========================================================================
        // CONTROL FLOW: CONDITIONAL (if / else)
        // =========================================================================
        if (selectedSeat.isAvailable() && !selectedSeat.isHeld()) {
            // Locks seat for 10 minutes (holdDurationSeconds)
            selectedSeat.lockSeat(passengerId, holdDurationSeconds);
            return new SeatHoldResponse(
                    true,
                    "Seat locked successfully for 10 minutes.",
                    selectedSeat.getSeatNumber(),
                    holdDurationSeconds,
                    List.of()
            );
        } else {
            // Prompts "Seat no longer available" and suggests alternative aisles
            List<Seat> aisleCandidates = seatMap.getAvailableAisleSeats(selectedSeat.getSeatClass());
            List<String> suggestedAisles = extractSeatNumbers(aisleCandidates);

            return new SeatHoldResponse(
                    false,
                    "Seat no longer available",
                    selectedSeat.getSeatNumber(),
                    0,
                    suggestedAisles
            );
        }
    }

    private static List<String> extractSeatNumbers(List<Seat> seats) {
        List<String> list = new ArrayList<>();
        for (Seat s : seats) {
            list.add(s.getSeatNumber());
        }
        return list;
    }
}
