package com.airline.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Multi-leg travel itinerary containing sequential flight legs.
 */
public class TripItinerary {
    private final String itineraryId;
    private final List<FlightLeg> legs;

    public TripItinerary(String itineraryId) {
        this.itineraryId = itineraryId;
        this.legs = new ArrayList<>();
    }

    public TripItinerary(String itineraryId, List<FlightLeg> legs) {
        this.itineraryId = itineraryId;
        this.legs = new ArrayList<>(legs);
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void addLeg(FlightLeg leg) {
        legs.add(leg);
    }

    public List<FlightLeg> getLegs() {
        return Collections.unmodifiableList(legs);
    }

    public boolean isEmpty() {
        return legs.isEmpty();
    }

    public int getLegCount() {
        return legs.size();
    }

    public String getOriginAirport() {
        return legs.isEmpty() ? "" : legs.get(0).getOriginAirport();
    }

    public String getFinalDestinationAirport() {
        return legs.isEmpty() ? "" : legs.get(legs.size() - 1).getDestinationAirport();
    }

    public LocalDateTime getFirstDepartureTime() {
        return legs.isEmpty() ? null : legs.get(0).getDepartureTime();
    }

    public LocalDateTime getFinalArrivalTime() {
        return legs.isEmpty() ? null : legs.get(legs.size() - 1).getArrivalTime();
    }
}
