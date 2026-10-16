package com.airline.cli;

import com.airline.model.AircraftSeatMap;
import com.airline.model.Seat;
import com.airline.model.SeatLocation;
import com.airline.model.TicketClass;

/**
 * Visual ASCII & Unicode Cabin Renderer for Terminal Interfaces.
 */
public class SeatMapVisualizer {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    public static void renderSeatMap(AircraftSeatMap seatMap, String title) {
        System.out.println("\n" + BOLD + CYAN + "┌──────────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf(BOLD + CYAN + "│  ✈️  CABIN SEAT MAP: %-38s  │\n" + RESET, title);
        System.out.println(BOLD + CYAN + "└──────────────────────────────────────────────────────────────┘" + RESET);
        System.out.println("  Legend: " + GREEN + "[🟢 AVAI]" + RESET + " Available  "
                + YELLOW + "[🟡 HELD]" + RESET + " 10-min Lock  "
                + RED + "[🔴 BKED]" + RESET + " Confirmed\n");

        // First Class Section (Rows 1-2, 2x2: A B || C D)
        System.out.println(BOLD + "  === FIRST CLASS (Rows 1-2) ===" + RESET);
        System.out.println("        A          B     [AISLE]    C          D");
        for (int r = 1; r <= 2; r++) {
            System.out.printf("  Row %02d: %s  %s    ‖   %s  %s\n",
                    r,
                    formatSeatBox(seatMap.getSeat(r + "A")),
                    formatSeatBox(seatMap.getSeat(r + "B")),
                    formatSeatBox(seatMap.getSeat(r + "C")),
                    formatSeatBox(seatMap.getSeat(r + "D")));
        }

        // Business Class Section (Rows 3-5, 3x3: A B C || D E F)
        System.out.println("\n" + BOLD + "  === BUSINESS CLASS (Rows 3-5) ===" + RESET);
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 3; r <= 5; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ‖   %s  %s  %s\n",
                    r,
                    formatSeatBox(seatMap.getSeat(r + "A")),
                    formatSeatBox(seatMap.getSeat(r + "B")),
                    formatSeatBox(seatMap.getSeat(r + "C")),
                    formatSeatBox(seatMap.getSeat(r + "D")),
                    formatSeatBox(seatMap.getSeat(r + "E")),
                    formatSeatBox(seatMap.getSeat(r + "F")));
        }

        // Economy Class Section (Rows 6-10 preview)
        System.out.println("\n" + BOLD + "  === ECONOMY CLASS (Rows 6-12) ===" + RESET);
        System.out.println("        A          B          C     [AISLE]    D          E          F");
        for (int r = 6; r <= 10; r++) {
            System.out.printf("  Row %02d: %s  %s  %s    ‖   %s  %s  %s\n",
                    r,
                    formatSeatBox(seatMap.getSeat(r + "A")),
                    formatSeatBox(seatMap.getSeat(r + "B")),
                    formatSeatBox(seatMap.getSeat(r + "C")),
                    formatSeatBox(seatMap.getSeat(r + "D")),
                    formatSeatBox(seatMap.getSeat(r + "E")),
                    formatSeatBox(seatMap.getSeat(r + "F")));
        }
        System.out.println("  ... (Rows 11-12 available)\n");
    }

    private static String formatSeatBox(Seat seat) {
        if (seat == null) return " [      ] ";
        if (seat.isAvailable() && !seat.isHeld()) {
            return GREEN + String.format("[%s:AVAI]", seat.getSeatNumber()) + RESET;
        } else if (seat.isHeld()) {
            return YELLOW + String.format("[%s:%03ds]", seat.getSeatNumber(), seat.getRemainingHoldSeconds()) + RESET;
        } else {
            return RED + String.format("[%s:BKED]", seat.getSeatNumber()) + RESET;
        }
    }
}
