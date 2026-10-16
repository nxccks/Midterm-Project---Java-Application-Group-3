package com.airline.model;

import java.time.Instant;

/**
 * Seat representation with availability, lock status, and holding timers.
 */
public class Seat {
    private final String seatNumber;
    private final TicketClass seatClass;
    private final SeatLocation location;
    private final double extraPrice;
    
    private boolean available;
    private boolean held;
    private String heldByPassengerId;
    private long holdExpiresAtMillis; // Epoch timestamp in milliseconds

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

    public String getSeatNumber() {
        return seatNumber;
    }

    public TicketClass getSeatClass() {
        return seatClass;
    }

    public SeatLocation getLocation() {
        return location;
    }

    public double getExtraPrice() {
        return extraPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isHeld() {
        return held;
    }

    public String getHeldByPassengerId() {
        return heldByPassengerId;
    }

    public long getHoldExpiresAtMillis() {
        return holdExpiresAtMillis;
    }

    /**
     * Checks whether this seat currently has an expired hold timer.
     */
    public boolean isHoldExpired() {
        if (!held || holdExpiresAtMillis <= 0) {
            return false;
        }
        return System.currentTimeMillis() > holdExpiresAtMillis;
    }

    public long getRemainingHoldSeconds() {
        if (!held || holdExpiresAtMillis <= 0) {
            return 0L;
        }
        long diff = holdExpiresAtMillis - System.currentTimeMillis();
        return diff > 0 ? diff / 1000L : 0L;
    }

    /**
     * Locks the seat for a specified duration in seconds (default 10 minutes = 600 seconds).
     */
    public void lockSeat(String passengerId, long durationSeconds) {
        this.held = true;
        this.available = false;
        this.heldByPassengerId = passengerId;
        this.holdExpiresAtMillis = System.currentTimeMillis() + (durationSeconds * 1000L);
    }

    /**
     * Releases the seat back to the open inventory pool.
     */
    public void releaseSeat() {
        this.held = false;
        this.available = true;
        this.heldByPassengerId = null;
        this.holdExpiresAtMillis = 0L;
    }

    /**
     * Confirms the reservation permanently.
     */
    public void confirmBooking(String passengerId) {
        this.held = false;
        this.available = false;
        this.heldByPassengerId = passengerId;
        this.holdExpiresAtMillis = 0L;
    }

    @Override
    public String toString() {
        return String.format("Seat[%s | %s | %s | Available:%s | Held:%s | Rem:%ds]",
                seatNumber, seatClass, location, available, held, getRemainingHoldSeconds());
    }
}
