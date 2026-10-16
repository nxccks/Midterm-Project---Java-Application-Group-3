package com.airline.model;

import java.util.*;

/**
 * Manages aircraft seat configurations and queries.
 */
public class AircraftSeatMap {
    private final String flightId;
    private final Map<String, Seat> seats;

    public AircraftSeatMap(String flightId) {
        this.flightId = flightId;
        this.seats = new LinkedHashMap<>();
    }

    public String getFlightId() {
        return flightId;
    }

    public void addSeat(Seat seat) {
        seats.put(seat.getSeatNumber().toUpperCase(), seat);
    }

    public Seat getSeat(String seatNumber) {
        if (seatNumber == null) return null;
        return seats.get(seatNumber.toUpperCase());
    }

    public Collection<Seat> getAllSeats() {
        return Collections.unmodifiableCollection(seats.values());
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableList = new ArrayList<>();
        for (Seat seat : seats.values()) {
            if (seat.isAvailable() && !seat.isHeld()) {
                availableList.add(seat);
            }
        }
        return availableList;
    }

    /**
     * Finds available unheld aisle seats (optionally matching class).
     */
    public List<Seat> getAvailableAisleSeats(TicketClass ticketClass) {
        List<Seat> aisleSeats = new ArrayList<>();
        for (Seat seat : seats.values()) {
            if (seat.isAvailable() && !seat.isHeld() && seat.getLocation() == SeatLocation.AISLE) {
                if (ticketClass == null || seat.getSeatClass() == ticketClass) {
                    aisleSeats.add(seat);
                }
            }
        }
        // Fallback: If none in specified class, return all available aisle seats
        if (aisleSeats.isEmpty() && ticketClass != null) {
            for (Seat seat : seats.values()) {
                if (seat.isAvailable() && !seat.isHeld() && seat.getLocation() == SeatLocation.AISLE) {
                    aisleSeats.add(seat);
                }
            }
        }
        return aisleSeats;
    }

    /**
     * Builds a standard commercial airliner cabin layout (Boeing 777 / Airbus A350):
     * Rows 1-2: FIRST CLASS (A:Window, B:Aisle, C:Aisle, D:Window)
     * Rows 3-5: BUSINESS CLASS (A:Window, B:Middle, C:Aisle, D:Aisle, E:Middle, F:Window)
     * Rows 6-15: ECONOMY CLASS (A:Window, B:Middle, C:Aisle, D:Aisle, E:Middle, F:Window)
     */
    public static AircraftSeatMap createDefaultLayout(String flightId) {
        AircraftSeatMap map = new AircraftSeatMap(flightId);

        // First Class: Rows 1 to 2
        for (int row = 1; row <= 2; row++) {
            map.addSeat(new Seat(row + "A", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
            map.addSeat(new Seat(row + "B", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
            map.addSeat(new Seat(row + "C", TicketClass.FIRST, SeatLocation.AISLE, 140.0));
            map.addSeat(new Seat(row + "D", TicketClass.FIRST, SeatLocation.WINDOW, 150.0));
        }

        // Business Class: Rows 3 to 5
        for (int row = 3; row <= 5; row++) {
            map.addSeat(new Seat(row + "A", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
            map.addSeat(new Seat(row + "B", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
            map.addSeat(new Seat(row + "C", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
            map.addSeat(new Seat(row + "D", TicketClass.BUSINESS, SeatLocation.AISLE, 65.0));
            map.addSeat(new Seat(row + "E", TicketClass.BUSINESS, SeatLocation.MIDDLE, 50.0));
            map.addSeat(new Seat(row + "F", TicketClass.BUSINESS, SeatLocation.WINDOW, 75.0));
        }

        // Economy Class: Rows 6 to 12
        for (int row = 6; row <= 12; row++) {
            map.addSeat(new Seat(row + "A", TicketClass.ECONOMY, SeatLocation.WINDOW, 20.0));
            map.addSeat(new Seat(row + "B", TicketClass.ECONOMY, SeatLocation.MIDDLE, 0.0));
            map.addSeat(new Seat(row + "C", TicketClass.ECONOMY, SeatLocation.AISLE, 15.0));
            map.addSeat(new Seat(row + "D", TicketClass.ECONOMY, SeatLocation.AISLE, 15.0));
            map.addSeat(new Seat(row + "E", TicketClass.ECONOMY, SeatLocation.MIDDLE, 0.0));
            map.addSeat(new Seat(row + "F", TicketClass.ECONOMY, SeatLocation.WINDOW, 20.0));
        }

        return map;
    }
}
