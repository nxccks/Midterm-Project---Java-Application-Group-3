package com.airline.web;

import com.airline.engine.*;
import com.airline.model.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Embedded HTTP Web Server & REST API.
 * Demonstrates that the engine is a 100% pure software cloud backend
 * operating via web APIs and booking interfaces.
 */
public class AirlineHttpServer {

    private final int port;
    private final AirlineReservationSystem system;
    private HttpServer server;

    public AirlineHttpServer(int port, AirlineReservationSystem system) {
        this.port = port;
        this.system = system;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Web Dashboard Handler
        server.createContext("/", new DashboardHandler());

        // REST API Endpoints
        server.createContext("/api/flights", new FlightsApiHandler(system));
        server.createContext("/api/hold-seat", new HoldSeatApiHandler(system));
        server.createContext("/api/evaluate-itinerary", new ItineraryApiHandler(system));
        server.createContext("/api/assess-baggage", new BaggageApiHandler(system));
        server.createContext("/api/calculate-loyalty", new LoyaltyApiHandler(system));
        server.createContext("/api/evaluate-refund", new RefundApiHandler(system));
        server.createContext("/api/sweep-expired", new SweepExpiredApiHandler(system));

        server.setExecutor(null); // default single/multi thread executor
        server.start();
        System.out.println("Web Dashboard & REST API live at: http://localhost:" + port + "/");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                map.put(pair[0], java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            } else if (pair.length == 1) {
                map.put(pair[0], "");
            }
        }
        return map;
    }

    /**
     * Serves the visual web UI dashboard for testing the platform.
     */
    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }

            String html = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Airline Reservation & Control Flow Engine</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        .seat-available { background-color: #10B981; }
        .seat-held { background-color: #F59E0B; }
        .seat-booked { background-color: #EF4444; }
    </style>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen">
    <div class="max-w-7xl mx-auto px-4 py-8">
        <header class="mb-8 border-b border-slate-700 pb-6">
            <div class="flex items-center justify-between">
                <div>
                    <h1 class="text-3xl font-extrabold text-sky-400">
                        Automated Airline Ticket Booking & Dynamic Seat Reservation
                    </h1>
                    <p class="text-slate-400 mt-2">Interactive Demonstration of Blueprint Control Flow Applications (Conditional, Switch, Loops)</p>
                </div>
                <span class="bg-sky-500/20 text-sky-300 border border-sky-500/30 px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wider">
                    Java 24 Engine Active
                </span>
            </div>
        </header>

        <!-- Grid of Control Flow Modules -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
            
            <!-- 1. Baggage Allowance & Fee Assessment (if / else if / else) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-emerald-400">
                        Baggage Assessment
                    </h2>
                    <span class="text-xs bg-emerald-900/50 text-emerald-300 border border-emerald-700 px-2 py-0.5 rounded font-mono">
                        if / else if / else
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: FIRST or Elite: 2 free bags | BUSINESS: 1 free bag | ECONOMY: standard excess per-kg rate.</p>
                
                <div class="space-y-3">
                    <div>
                        <label class="block text-xs font-medium text-slate-300 mb-1">Ticket Class</label>
                        <select id="bagClass" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                            <option value="FIRST">FIRST CLASS</option>
                            <option value="BUSINESS">BUSINESS CLASS</option>
                            <option value="ECONOMY" selected>ECONOMY CLASS</option>
                        </select>
                    </div>
                    <div class="flex items-center gap-2">
                        <input type="checkbox" id="bagElite" class="rounded bg-slate-900 border-slate-700 text-emerald-500">
                        <label for="bagElite" class="text-sm text-slate-300">Passenger has Elite Frequent Flyer Status</label>
                    </div>
                    <div class="grid grid-cols-2 gap-3">
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Checked Bags</label>
                            <input type="number" id="bagCount" value="2" min="0" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                        </div>
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Total Weight (kg)</label>
                            <input type="number" id="bagWeight" value="38" min="0" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                        </div>
                    </div>
                    <button onclick="assessBaggage()" class="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-medium py-2 rounded text-sm transition">
                        Evaluate Baggage Allowance
                    </button>
                    <div id="bagResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

            <!-- 2. Seat Availability & Dynamic Hold Check (if / else) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-amber-400">
                        Dynamic Seat Hold
                    </h2>
                    <span class="text-xs bg-amber-900/50 text-amber-300 border border-amber-700 px-2 py-0.5 rounded font-mono">
                        if / else
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: If seat is available & unheld, locks for 10 minutes; else prompts unavailable and suggests alternative aisles.</p>
                
                <div class="space-y-3">
                    <div class="grid grid-cols-2 gap-3">
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Flight Leg</label>
                            <select id="seatLegId" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                                <option value="FL-101">FL-101 (JFK -> ORD)</option>
                                <option value="FL-102">FL-102 (ORD -> LAX)</option>
                                <option value="FL-103">FL-103 (LAX -> HNL)</option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Seat Number</label>
                            <input type="text" id="seatNumber" value="3C" placeholder="e.g. 1A, 3C, 6D" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white uppercase">
                        </div>
                    </div>
                    <div>
                        <label class="block text-xs font-medium text-slate-300 mb-1">Passenger ID</label>
                        <input type="text" id="seatPassengerId" value="P101" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                    </div>
                    <button onclick="holdSeat()" class="w-full bg-amber-600 hover:bg-amber-500 text-white font-medium py-2 rounded text-sm transition">
                        Lock Seat (10-Minute Hold)
                    </button>
                    <div id="seatResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

            <!-- 3. Cancellation Refund Eligibility (if / else if / else) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-rose-400">
                        Refund Eligibility
                    </h2>
                    <span class="text-xs bg-rose-900/50 text-rose-300 border border-rose-700 px-2 py-0.5 rounded font-mono">
                        if / else if / else
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: >=48h & refundable -> 100% refund | >=24h -> 50% voucher | &lt;24h -> Non-refundable.</p>
                
                <div class="space-y-3">
                    <div class="grid grid-cols-2 gap-3">
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Hours Before Flight</label>
                            <input type="number" id="refundHours" value="52" min="0" step="0.5" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                        </div>
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Ticket Paid ($)</label>
                            <input type="number" id="refundPrice" value="450" min="0" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                        </div>
                    </div>
                    <div class="flex items-center gap-2">
                        <input type="checkbox" id="refundFareType" checked class="rounded bg-slate-900 border-slate-700 text-rose-500">
                        <label for="refundFareType" class="text-sm text-slate-300">Ticket was booked on a Refundable Fare Basis</label>
                    </div>
                    <button onclick="evaluateRefund()" class="w-full bg-rose-600 hover:bg-rose-500 text-white font-medium py-2 rounded text-sm transition">
                        Evaluate Refund Policy
                    </button>
                    <div id="refundResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

            <!-- 4. Loyalty Tier Multiplier (switch-case) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-purple-400">
                        Loyalty Dispatcher
                    </h2>
                    <span class="text-xs bg-purple-900/50 text-purple-300 border border-purple-700 px-2 py-0.5 rounded font-mono">
                        switch-case
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: PLATINUM: 3.0x + Lounge | GOLD: 2.0x + Priority Boarding | SILVER: 1.5x | default: 1.0x.</p>
                
                <div class="space-y-3">
                    <div class="grid grid-cols-2 gap-3">
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Frequent Flyer Tier</label>
                            <select id="loyaltyTier" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                                <option value="PLATINUM">PLATINUM (3.0x)</option>
                                <option value="GOLD">GOLD (2.0x)</option>
                                <option value="SILVER">SILVER (1.5x)</option>
                                <option value="REGULAR">REGULAR (1.0x)</option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-xs font-medium text-slate-300 mb-1">Flight Miles</label>
                            <input type="number" id="loyaltyMiles" value="2500" min="0" class="w-full bg-slate-900 border border-slate-700 rounded px-3 py-2 text-sm text-white">
                        </div>
                    </div>
                    <button onclick="calculateLoyalty()" class="w-full bg-purple-600 hover:bg-purple-500 text-white font-medium py-2 rounded text-sm transition">
                        Dispatch Loyalty Perks
                    </button>
                    <div id="loyaltyResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

            <!-- 5. Multi-Leg Itinerary Cost Evaluator (for-loop) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-cyan-400">
                        Multi-Leg Evaluator
                    </h2>
                    <span class="text-xs bg-cyan-900/50 text-cyan-300 border border-cyan-700 px-2 py-0.5 rounded font-mono">
                        for (leg in itinerary)
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: Iterates connecting flight legs to sum base fares, taxes, surcharges, and flight duration.</p>
                
                <div class="space-y-3">
                    <div>
                        <label class="block text-xs font-medium text-slate-300 mb-1">Selected Route Connecting Legs</label>
                        <div class="space-y-1 text-xs">
                            <label class="flex items-center gap-2">
                                <input type="checkbox" id="leg1" checked class="rounded bg-slate-900 border-slate-700 text-cyan-500">
                                <span>FL-101: JFK -> ORD ($220 + $35 tax + $25 fuel, 150m)</span>
                            </label>
                            <label class="flex items-center gap-2">
                                <input type="checkbox" id="leg2" checked class="rounded bg-slate-900 border-slate-700 text-cyan-500">
                                <span>FL-102: ORD -> LAX ($310 + $42 tax + $38 fuel, 255m)</span>
                            </label>
                            <label class="flex items-center gap-2">
                                <input type="checkbox" id="leg3" checked class="rounded bg-slate-900 border-slate-700 text-cyan-500">
                                <span>FL-103: LAX -> HNL ($450 + $55 tax + $48 fuel, 270m)</span>
                            </label>
                        </div>
                    </div>
                    <button onclick="evaluateItinerary()" class="w-full bg-cyan-600 hover:bg-cyan-500 text-white font-medium py-2 rounded text-sm transition">
                        Iterate & Evaluate Aggregate Cost
                    </button>
                    <div id="itineraryResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

            <!-- 6. Expired Reservation Seat Releaser (while-loop) -->
            <div class="bg-slate-800 border border-slate-700 rounded-xl p-6 shadow-lg">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-bold text-pink-400">
                        Expired Seat Sweeper
                    </h2>
                    <span class="text-xs bg-pink-900/50 text-pink-300 border border-pink-700 px-2 py-0.5 rounded font-mono">
                        while (hasExpiredHeldSeats)
                    </span>
                </div>
                <p class="text-xs text-slate-400 mb-4">Rule: Loops continuously through seats with lapsed checkout timers, returning them to the inventory pool.</p>
                
                <div class="space-y-3">
                    <p class="text-xs text-slate-300">Trigger background sweep on all aircraft seat maps across flight inventory.</p>
                    <button onclick="sweepExpired()" class="w-full bg-pink-600 hover:bg-pink-500 text-white font-medium py-2 rounded text-sm transition">
                        Execute Sweeper (while-loop)
                    </button>
                    <div id="sweeperResult" class="hidden p-3 bg-slate-900 rounded border border-slate-700 text-xs text-slate-300 space-y-1"></div>
                </div>
            </div>

        </div>
    </div>

    <script>
        async function assessBaggage() {
            const ticketClass = document.getElementById('bagClass').value;
            const isElite = document.getElementById('bagElite').checked;
            const bags = document.getElementById('bagCount').value;
            const weight = document.getElementById('bagWeight').value;

            const res = await fetch(`/api/assess-baggage?class=${ticketClass}&elite=${isElite}&bags=${bags}&weight=${weight}`);
            const data = await res.json();
            
            const div = document.getElementById('bagResult');
            div.classList.remove('hidden');
            div.innerHTML = `
                <div class="font-semibold text-emerald-400">${data.policy}</div>
                <div>• Free Bags Allowed: <strong>${data.freeBags}</strong></div>
                <div>• Excess Bags Count: <strong>${data.excessBags}</strong></div>
                <div>• Excess Weight: <strong>${data.excessWeight} kg</strong></div>
                <div class="text-sm font-bold text-white mt-1">• Excess Fee Due: $${data.excessFee.toFixed(2)}</div>
            `;
        }

        async function holdSeat() {
            const legId = document.getElementById('seatLegId').value;
            const seatNumber = document.getElementById('seatNumber').value;
            const passengerId = document.getElementById('seatPassengerId').value;

            const res = await fetch(`/api/hold-seat?legId=${legId}&seatNumber=${seatNumber}&passengerId=${passengerId}`);
            const data = await res.json();

            const div = document.getElementById('seatResult');
            div.classList.remove('hidden');
            if (data.success) {
                div.innerHTML = `
                    <div class="font-semibold text-amber-400">Seat Locked Successfully</div>
                    <div>• Seat: <strong>${data.seatNumber}</strong></div>
                    <div>• Hold Duration: <strong>10 minutes (600 seconds)</strong></div>
                    <div>• Status: ${data.message}</div>
                `;
            } else {
                div.innerHTML = `
                    <div class="font-semibold text-rose-400">${data.message}</div>
                    <div>• Suggested Alternative Aisle Seats: <strong>${data.alternativeAisles.join(', ') || 'None available'}</strong></div>
                `;
            }
        }

        async function evaluateRefund() {
            const hours = document.getElementById('refundHours').value;
            const price = document.getElementById('refundPrice').value;
            const refundable = document.getElementById('refundFareType').checked;

            const res = await fetch(`/api/evaluate-refund?hours=${hours}&price=${price}&refundable=${refundable}`);
            const data = await res.json();

            const div = document.getElementById('refundResult');
            div.classList.remove('hidden');
            div.innerHTML = `
                <div class="font-semibold text-rose-400">Eligibility: ${data.eligibility}</div>
                <div>• Policy Reason: ${data.decisionReason}</div>
                <div>• Cash Refund: <strong>$${data.cashRefund.toFixed(2)}</strong></div>
                <div>• Travel Voucher: <strong>$${data.voucherAmount.toFixed(2)}</strong></div>
            `;
        }

        async function calculateLoyalty() {
            const tier = document.getElementById('loyaltyTier').value;
            const miles = document.getElementById('loyaltyMiles').value;

            const res = await fetch(`/api/calculate-loyalty?tier=${tier}&miles=${miles}`);
            const data = await res.json();

            const div = document.getElementById('loyaltyResult');
            div.classList.remove('hidden');
            div.innerHTML = `
                <div class="font-semibold text-purple-400">Tier: ${data.tier} (${data.multiplier}x Multiplier)</div>
                <div>• Base Miles: ${data.baseMiles} | Bonus Miles: ${data.bonusMiles}</div>
                <div class="text-sm font-bold text-white mt-1">• Total Miles Credited: ${data.totalMilesEarned}</div>
                <div>• Lounge Access: <strong>${data.loungePass ? 'YES' : 'NO'}</strong></div>
                <div>• Priority Boarding: <strong>${data.priorityBoarding ? 'YES' : 'NO'}</strong></div>
            `;
        }

        async function evaluateItinerary() {
            const legs = [];
            if (document.getElementById('leg1').checked) legs.push('FL-101');
            if (document.getElementById('leg2').checked) legs.push('FL-102');
            if (document.getElementById('leg3').checked) legs.push('FL-103');

            const res = await fetch(`/api/evaluate-itinerary?legs=${legs.join(',')}`);
            const data = await res.json();

            const div = document.getElementById('itineraryResult');
            div.classList.remove('hidden');
            div.innerHTML = `
                <div class="font-semibold text-cyan-400">Multi-Leg Aggregate Results (${data.totalLegs} Flight Legs):</div>
                <div>• Base Fares: $${data.totalBaseFare.toFixed(2)}</div>
                <div>• Airport Taxes: $${data.totalAirportTax.toFixed(2)}</div>
                <div>• Fuel Surcharges: $${data.totalFuelSurcharge.toFixed(2)}</div>
                <div class="text-sm font-bold text-white mt-1">• Grand Total: $${data.grandTotalCost.toFixed(2)}</div>
                <div>• Flight Airborne Time: ${data.totalFlightDurationMinutes} min | Layover: ${data.totalLayoverDurationMinutes} min</div>
            `;
        }

        async function sweepExpired() {
            const res = await fetch(`/api/sweep-expired`);
            const data = await res.json();

            const div = document.getElementById('sweeperResult');
            div.classList.remove('hidden');
            div.innerHTML = `
                <div class="font-semibold text-pink-400">Sweeper Finished (while-loop):</div>
                <div>• Total Seats Released: <strong>${data.releasedCount}</strong></div>
                <div>• Released Details: ${data.releasedSeats.length ? data.releasedSeats.join(', ') : 'No expired holds detected.'}</div>
            `;
        }
    </script>
</body>
</html>
""";
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class FlightsApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public FlightsApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (FlightLeg leg : system.getAllFlightLegs()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(String.format("{\"legId\":\"%s\",\"flightNumber\":\"%s\",\"origin\":\"%s\",\"destination\":\"%s\",\"baseFare\":%.2f,\"durationMinutes\":%d}",
                        leg.getLegId(), leg.getFlightNumber(), leg.getOriginAirport(), leg.getDestinationAirport(), leg.getBaseFare(), leg.getFlightDurationMinutes()));
            }
            sb.append("]");
            sendJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class HoldSeatApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public HoldSeatApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String legId = q.getOrDefault("legId", "FL-101");
            String seatNumber = q.getOrDefault("seatNumber", "3C");
            String passengerId = q.getOrDefault("passengerId", "P101");

            FlightLeg leg = system.getFlightLeg(legId);
            if (leg == null) {
                sendJsonResponse(exchange, 404, "{\"error\":\"Flight leg not found\"}");
                return;
            }

            SeatHoldEngine.SeatHoldResponse res = SeatHoldEngine.checkAndHoldSeat(leg.getSeatMap(), seatNumber, passengerId);
            StringBuilder aisles = new StringBuilder("[");
            for (int i = 0; i < res.getAlternativeAisles().size(); i++) {
                if (i > 0) aisles.append(",");
                aisles.append("\"").append(res.getAlternativeAisles().get(i)).append("\"");
            }
            aisles.append("]");

            String json = String.format("{\"success\":%b,\"message\":\"%s\",\"seatNumber\":\"%s\",\"holdDuration\":%d,\"alternativeAisles\":%s}",
                    res.isSuccess(), res.getMessage(), res.getSeatNumber(), res.getHoldDurationSeconds(), aisles);
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class BaggageApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public BaggageApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            TicketClass tc = TicketClass.valueOf(q.getOrDefault("class", "ECONOMY").toUpperCase());
            boolean isElite = Boolean.parseBoolean(q.getOrDefault("elite", "false"));
            int bags = Integer.parseInt(q.getOrDefault("bags", "2"));
            double weight = Double.parseDouble(q.getOrDefault("weight", "35.0"));

            Passenger p = new Passenger("TEMP", "User", "user@test.com", isElite ? LoyaltyTier.GOLD : LoyaltyTier.REGULAR);
            BaggageAssessment b = BaggageEngine.assessBaggage(tc, p, bags, weight);

            String json = String.format("{\"freeBags\":%d,\"excessBags\":%d,\"excessWeight\":%.1f,\"excessFee\":%.2f,\"policy\":\"%s\"}",
                    b.getFreeBagsAllowed(), b.getExcessBagsCount(), b.getExcessWeightKg(), b.getExcessFee(), b.getPolicyExplanation());
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class LoyaltyApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public LoyaltyApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String tier = q.getOrDefault("tier", "PLATINUM");
            double miles = Double.parseDouble(q.getOrDefault("miles", "2500"));

            LoyaltyReward r = LoyaltyTierDispatcher.calculateByTierString(tier, miles);
            String json = String.format("{\"tier\":\"%s\",\"multiplier\":%.1f,\"baseMiles\":%.1f,\"bonusMiles\":%.1f,\"totalMilesEarned\":%.1f,\"loungePass\":%b,\"priorityBoarding\":%b,\"perks\":\"%s\"}",
                    r.getTier(), r.getMultiplier(), r.getBaseMiles(), r.getBonusMiles(), r.getTotalMilesEarned(), r.hasLoungePass(), r.hasPriorityBoarding(), r.getPerksDescription());
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class RefundApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public RefundApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            double hours = Double.parseDouble(q.getOrDefault("hours", "50"));
            double price = Double.parseDouble(q.getOrDefault("price", "400"));
            boolean refundable = Boolean.parseBoolean(q.getOrDefault("refundable", "true"));

            RefundResult r = CancellationRefundEngine.evaluateRefundEligibility(hours, refundable, price);
            String json = String.format("{\"eligibility\":\"%s\",\"refundPercentage\":%.1f,\"cashRefund\":%.2f,\"voucherAmount\":%.2f,\"decisionReason\":\"%s\"}",
                    r.getEligibility(), r.getRefundPercentage(), r.getCashRefundAmount(), r.getVoucherAmount(), r.getDecisionReason());
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class ItineraryApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public ItineraryApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String legsStr = q.getOrDefault("legs", "FL-101,FL-102");
            List<String> legs = Arrays.stream(legsStr.split(",")).map(String::trim).toList();

            TripItinerary itinerary = system.buildItinerary("WEB-ITIN", legs);
            ItineraryCostSummary s = system.evaluateItinerary(itinerary);

            String json = String.format("{\"totalLegs\":%d,\"totalBaseFare\":%.2f,\"totalAirportTax\":%.2f,\"totalFuelSurcharge\":%.2f,\"grandTotalCost\":%.2f,\"totalFlightDurationMinutes\":%d,\"totalLayoverDurationMinutes\":%d,\"totalJourneyDurationMinutes\":%d,\"totalDistanceMiles\":%.1f}",
                    s.getTotalLegs(), s.getTotalBaseFare(), s.getTotalAirportTax(), s.getTotalFuelSurcharge(), s.getGrandTotalCost(), s.getTotalFlightDurationMinutes(), s.getTotalLayoverDurationMinutes(), s.getTotalJourneyDurationMinutes(), s.getTotalDistanceMiles());
            sendJsonResponse(exchange, 200, json);
        }
    }

    static class SweepExpiredApiHandler implements HttpHandler {
        private final AirlineReservationSystem system;
        public SweepExpiredApiHandler(AirlineReservationSystem system) { this.system = system; }
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ExpiredSeatReleaser.ReleaserSummary summary = system.sweepAllExpiredSeats();
            StringBuilder seats = new StringBuilder("[");
            for (int i = 0; i < summary.getReleasedSeatNumbers().size(); i++) {
                if (i > 0) seats.append(",");
                seats.append("\"").append(summary.getReleasedSeatNumbers().get(i)).append("\"");
            }
            seats.append("]");

            String json = String.format("{\"releasedCount\":%d,\"releasedSeats\":%s}",
                    summary.getReleasedCount(), seats);
            sendJsonResponse(exchange, 200, json);
        }
    }
}
