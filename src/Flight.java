/**
 * Flight.java
 * Represents a single airline flight with its core attributes.
 * Used as the element type stored in the ArrayList<Flight> data structure.
 */
public class Flight {
    // --- Fields ---
    private String flightNumber;
    private String destination;
    private String departureTime; // format: "HH:MM"
    private double price;         // in INR (₹)

    // --- Constructor ---
    public Flight(String flightNumber, String destination, String departureTime, double price) {
        this.flightNumber  = flightNumber;
        this.destination   = destination;
        this.departureTime = departureTime;
        this.price         = price;
    }

    // --- Getters ---
    public String getFlightNumber()  { return flightNumber; }
    public String getDestination()   { return destination; }
    public String getDepartureTime() { return departureTime; }
    public double getPrice()         { return price; }

    // --- Setters (needed by the Edit-Flight dialog) ---
    public void setFlightNumber(String flightNumber)   { this.flightNumber  = flightNumber; }
    public void setDestination(String destination)     { this.destination   = destination; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setPrice(double price)                 { this.price         = price; }

    /**
     * toString() override – shown in the JList and copied to clipboard.
     * Format: "AF-102 | London (LHR) | 08:45 | ₹45000.00"
     */
    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %-8s | \u20B9%.2f",
                flightNumber, destination, departureTime, price);
    }
}