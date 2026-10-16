package com.airline.engine;

import com.airline.model.AircraftSeatMap;
import com.airline.model.Seat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Control Flow Application: Loops (while)
 * 
 * Blueprint Specification:
 * Expired Reservation Seat Releaser (while (hasExpiredHeldSeats)):
 * Periodically loops through temporarily reserved seats whose checkout timers lapsed,
 * releasing them back to the public inventory pool.
 */
public class ExpiredSeatReleaser {

    public static class ReleaserSummary {
        private final int releasedCount;
        private final List<String> releasedSeatNumbers;

        public ReleaserSummary(int releasedCount, List<String> releasedSeatNumbers) {
            this.releasedCount = releasedCount;
            this.releasedSeatNumbers = releasedSeatNumbers;
        }

        public int getReleasedCount() {
            return releasedCount;
        }

        public List<String> getReleasedSeatNumbers() {
            return releasedSeatNumbers;
        }

        @Override
        public String toString() {
            return String.format("ExpiredSeatReleaser: %d seats released back to public inventory pool %s",
                    releasedCount, releasedSeatNumbers.isEmpty() ? "[]" : releasedSeatNumbers);
        }
    }

    /**
     * Checks if there are any held seats in the inventory that have expired.
     */
    public static boolean hasExpiredHeldSeats(Collection<Seat> seats) {
        for (Seat seat : seats) {
            if (seat.isHeld() && seat.isHoldExpired()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the next expired held seat in the seat collection.
     */
    private static Seat findNextExpiredSeat(Collection<Seat> seats) {
        for (Seat seat : seats) {
            if (seat.isHeld() && seat.isHoldExpired()) {
                return seat;
            }
        }
        return null;
    }

    /**
     * Executes the seat releaser algorithm using the blueprint WHILE loop.
     *
     * @param seatMap The seat inventory map to sweep
     * @return ReleaserSummary Summary of all released seats
     */
    public static ReleaserSummary releaseExpiredHeldSeats(AircraftSeatMap seatMap) {
        if (seatMap == null) {
            return new ReleaserSummary(0, List.of());
        }

        List<String> releasedSeats = new ArrayList<>();
        Collection<Seat> seats = seatMap.getAllSeats();

        // =========================================================================
        // CONTROL FLOW: LOOP (while (hasExpiredHeldSeats))
        // =========================================================================
        while (hasExpiredHeldSeats(seats)) {
            Seat expiredSeat = findNextExpiredSeat(seats);
            if (expiredSeat != null) {
                String seatNum = expiredSeat.getSeatNumber();
                
                // Release seat back to public inventory pool
                expiredSeat.releaseSeat();
                
                releasedSeats.add(seatNum);
            } else {
                break;
            }
        }

        return new ReleaserSummary(releasedSeats.size(), releasedSeats);
    }
}
