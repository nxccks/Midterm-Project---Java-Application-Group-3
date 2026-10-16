package com.airline;

import com.airline.cli.InteractiveConsole;
import com.airline.cli.ScenarioDemoRunner;
import com.airline.engine.AirlineReservationSystem;
import com.airline.web.AirlineHttpServer;

/**
 * Main Application Entry Point
 * ✈️ Automated Airline Ticket Booking & Dynamic Seat Reservation Engine
 */
public class Main {

    public static void main(String[] args) {
        AirlineReservationSystem system = new AirlineReservationSystem();

        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("--demo") || command.equals("-d") || command.equals("demo")) {
                ScenarioDemoRunner.runAllScenarios();
                return;
            } else if (command.equals("--walkthrough") || command.equals("-w") || command.equals("walkthrough")) {
                ScenarioDemoRunner.runInteractiveWalkthrough();
                return;
            } else if (command.equals("--server") || command.equals("-s") || command.equals("server")) {
                int port = 8080;
                if (args.length > 1) {
                    try {
                        port = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                startServer(port, system, true);
                return;
            }
        }

        // By default, start background web server on 8080 and launch interactive terminal
        int serverPort = 8080;
        startServer(serverPort, system, false);

        InteractiveConsole console = new InteractiveConsole(system);
        console.start();
    }

    private static void startServer(int port, AirlineReservationSystem system, boolean block) {
        try {
            AirlineHttpServer server = new AirlineHttpServer(port, system);
            server.start();
            if (block) {
                System.out.println("Press Ctrl+C to stop the server.");
                Thread.currentThread().join();
            }
        } catch (Exception e) {
            System.err.println("Could not start HTTP server on port " + port + ": " + e.getMessage());
        }
    }
}
