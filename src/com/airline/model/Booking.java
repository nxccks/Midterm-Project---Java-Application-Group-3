package com.airline.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main Booking aggregate record.
 */
public class Booking {
    public enum Status {
        HELD,
        CONFIRMED,
        CANCELLED,
        EXPIRED
    }

    private final String bookingId;
    private final Passenger passenger;
    private final TripItinerary itinerary;
    private final TicketClass ticketClass;
    private final Map<String, String> selectedSeats; // legId -> seatNumber
    private boolean refundableFare;
    private Status status;
    private LocalDateTime bookingTime;
    
    private BaggageAssessment baggageAssessment;
    private LoyaltyReward loyaltyReward;
    private ItineraryCostSummary costSummary;
    private RefundResult refundResult;
    private double totalAmountPaid;

    public Booking(String bookingId, Passenger passenger, TripItinerary itinerary,
                   TicketClass ticketClass, boolean refundableFare) {
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.itinerary = itinerary;
        this.ticketClass = ticketClass;
        this.refundableFare = refundableFare;
        this.status = Status.HELD;
        this.bookingTime = LocalDateTime.now();
        this.selectedSeats = new LinkedHashMap<>();
        this.totalAmountPaid = 0.0;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public TripItinerary getItinerary() {
        return itinerary;
    }

    public TicketClass getTicketClass() {
        return ticketClass;
    }

    public boolean isRefundableFare() {
        return refundableFare;
    }

    public void setRefundableFare(boolean refundableFare) {
        this.refundableFare = refundableFare;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void assignSeat(String legId, String seatNumber) {
        selectedSeats.put(legId, seatNumber);
    }

    public Map<String, String> getSelectedSeats() {
        return Collections.unmodifiableMap(selectedSeats);
    }

    public BaggageAssessment getBaggageAssessment() {
        return baggageAssessment;
    }

    public void setBaggageAssessment(BaggageAssessment baggageAssessment) {
        this.baggageAssessment = baggageAssessment;
    }

    public LoyaltyReward getLoyaltyReward() {
        return loyaltyReward;
    }

    public void setLoyaltyReward(LoyaltyReward loyaltyReward) {
        this.loyaltyReward = loyaltyReward;
    }

    public ItineraryCostSummary getCostSummary() {
        return costSummary;
    }

    public void setCostSummary(ItineraryCostSummary costSummary) {
        this.costSummary = costSummary;
    }

    public RefundResult getRefundResult() {
        return refundResult;
    }

    public void setRefundResult(RefundResult refundResult) {
        this.refundResult = refundResult;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }
}
