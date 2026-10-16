package com.airline.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a single flight leg within a trip itinerary.
 */
public class FlightLeg {
    private final String legId;
    private final String flightNumber;
    private final String originAirport;
    private final String destinationAirport;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final double baseFare;
    private final double localAirportTax;
    private final double fuelSurcharge;
    private final double distanceMiles;
    private final AircraftSeatMap seatMap;

    public FlightLeg(String legId, String flightNumber, String originAirport, String destinationAirport,
                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                     double baseFare, double localAirportTax, double fuelSurcharge,
                     double distanceMiles) {
        this.legId = legId;
        this.flightNumber = flightNumber;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.baseFare = baseFare;
        this.localAirportTax = localAirportTax;
        this.fuelSurcharge = fuelSurcharge;
        this.distanceMiles = distanceMiles;
        this.seatMap = AircraftSeatMap.createDefaultLayout(legId);
    }

    public String getLegId() {
        return legId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getLocalAirportTax() {
        return localAirportTax;
    }

    public double getFuelSurcharge() {
        return fuelSurcharge;
    }

    public double getDistanceMiles() {
        return distanceMiles;
    }

    public AircraftSeatMap getSeatMap() {
        return seatMap;
    }

    public long getFlightDurationMinutes() {
        return Duration.between(departureTime, arrivalTime).toMinutes();
    }

    public double getTotalLegCost() {
        return baseFare + localAirportTax + fuelSurcharge;
    }

    @Override
    public String toString() {
        return String.format("%s (%s -> %s) | Dep: %s | Base: $%.2f | Tax: $%.2f | Fuel: $%.2f | Dur: %d min",
                flightNumber, originAirport, destinationAirport, departureTime,
                baseFare, localAirportTax, fuelSurcharge, getFlightDurationMinutes());
    }
}
