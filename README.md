# ✈️ Automated Airline Ticket Booking & Dynamic Seat Reservation Engine

A cloud-based airline reservation and inventory management platform that automates multi-passenger bookings, dynamic seat mapping, loyalty reward calculations, and dynamic fare rules.

Built in **Java** to demonstrate core **Control Flow Applications** (Conditionals, Switch Statements, and Loops).

---

## 📋 Control Flow Applications Mapping

| Control Flow Structure | Blueprint Feature | Implementation Location | Logic Summary |
| :--- | :--- | :--- | :--- |
| **Conditional (`if / else if / else`)** | **Baggage Allowance & Fee Assessment** | [`BaggageEngine.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/BaggageEngine.java) | `if (ticketClass == "FIRST" \|\| isEliteFrequentFlyer)` permits up to 2 checked bags free;<br>`else if (ticketClass == "BUSINESS")` permits 1 free bag;<br>`else` charges standard per-kilogram excess luggage rate. |
| **Conditional (`if / else`)** | **Seat Availability & Hold Check** | [`SeatHoldEngine.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/SeatHoldEngine.java) | `if (selectedSeat.isAvailable && !selectedSeat.isHeld)` locks seat for 10 minutes (600s);<br>`else` prompts *"Seat no longer available"* and suggests alternative aisles. |
| **Conditional (`if / else if / else`)** | **Cancellation Refund Eligibility** | [`CancellationRefundEngine.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/CancellationRefundEngine.java) | `if (hoursBeforeFlight >= 48 && isRefundableFare)` issues 100% refund;<br>`else if (hoursBeforeFlight >= 24)` issues 50% travel voucher;<br>`else` flags ticket as non-refundable. |
| **Switch (`switch-case`)** | **Frequent Flyer Tier Multiplier** | [`LoyaltyTierDispatcher.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/LoyaltyTierDispatcher.java) | `case "PLATINUM":` 3.0× bonus mile multiplier + lounge pass.<br>`case "GOLD":` 2.0× bonus mile multiplier + priority boarding.<br>`case "SILVER":` 1.5× bonus mile multiplier.<br>`default:` Awards standard 1.0× base miles. |
| **Loop (`for`)** | **Multi-Leg Itinerary Cost Evaluator** | [`ItineraryCostEvaluator.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/ItineraryCostEvaluator.java) | `for (flightLeg in tripItinerary)` iterates across connecting flights to calculate base fares, airport taxes, fuel surcharges, and total flight/layover duration. |
| **Loop (`while`)** | **Expired Reservation Seat Releaser** | [`ExpiredSeatReleaser.java`](file:///home/br0kenb4ck/Documents/proglang_group_project/src/com/airline/engine/ExpiredSeatReleaser.java) | `while (hasExpiredHeldSeats)` periodically loops through temporarily reserved seats whose checkout timers lapsed, releasing them back to the public inventory pool. |

---

## 📂 Project Architecture

```
proglang_group_project/
├── project-blueprint.txt
├── README.md
├── build.sh
├── run.sh
└── src/
    └── com/
        └── airline/
            ├── Main.java                          # Application entry point
            ├── model/
            │   ├── TicketClass.java               # FIRST, BUSINESS, ECONOMY
            │   ├── LoyaltyTier.java               # PLATINUM, GOLD, SILVER, REGULAR
            │   ├── SeatLocation.java              # AISLE, WINDOW, MIDDLE
            │   ├── Seat.java                      # Seat with 10-minute hold timers
            │   ├── AircraftSeatMap.java           # Cabin map & aisle search
            │   ├── Passenger.java                 # Passenger & elite status logic
            │   ├── FlightLeg.java                 # Leg info, base fare, tax, fuel
            │   ├── TripItinerary.java             # Multi-leg sequence
            │   ├── BaggageAssessment.java         # Baggage breakdown & fee
            │   ├── LoyaltyReward.java             # Tier multipliers & perks
            │   ├── ItineraryCostSummary.java      # Aggregated fare & duration
            │   ├── RefundResult.java              # Refund & voucher calculation
            │   └── Booking.java                   # Aggregate reservation record
            ├── engine/
            │   ├── BaggageEngine.java             # Conditional baggage assessment
            │   ├── SeatHoldEngine.java            # Conditional 10-min seat lock & aisle suggester
            │   ├── CancellationRefundEngine.java  # Conditional 48h/24h refund engine
            │   ├── LoyaltyTierDispatcher.java     # Switch-case tier dispatcher
            │   ├── ItineraryCostEvaluator.java    # For-loop multi-leg aggregator
            │   ├── ExpiredSeatReleaser.java       # While-loop expired hold sweeper
            │   └── AirlineReservationSystem.java  # Platform coordinator
            ├── cli/
            │   ├── InteractiveConsole.java        # Rich interactive terminal UI
            │   └── ScenarioDemoRunner.java        # Blueprint automated test runner
            ├── web/
            │   └── AirlineHttpServer.java         # Embedded Web UI & REST API
            └── test/
                └── TestRunner.java                # 50 Automated unit tests
```

---

## 🚀 How to Compile & Run

### 1. Build the Project
```bash
./build.sh
```

### 2. Run the Automated Blueprint Scenario Demo
Executes all real-life scenarios specified in `project-blueprint.txt`:
```bash
./run.sh --demo
```

### 3. Run the Automated Unit Test Suite (50 Tests)
```bash
./run.sh --test
```

### 4. Run the Interactive Terminal Console
```bash
./run.sh
```

### 5. Launch the Web Dashboard & REST API
```bash
./run.sh --server 8080
```
Open **http://localhost:8080** in your browser to access the visual web interface.

---

## 🌐 REST API Endpoints

- `GET /api/flights` - List all scheduled flight legs and pricing
- `GET /api/hold-seat?legId=FL-101&seatNumber=3C&passengerId=P101` - Lock seat for 10 minutes
- `GET /api/assess-baggage?class=FIRST&elite=true&bags=2&weight=40` - Baggage calculation
- `GET /api/calculate-loyalty?tier=PLATINUM&miles=2500` - Loyalty tier dispatcher
- `GET /api/evaluate-refund?hours=50&price=400&refundable=true` - Refund policy evaluation
- `GET /api/evaluate-itinerary?legs=FL-101,FL-102,FL-103` - Multi-leg cost & layover aggregator
- `GET /api/sweep-expired` - Trigger while-loop expired seat sweeper
