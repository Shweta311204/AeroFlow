/**
 * Flight.java
 * Represents a single airline flight with its core attributes.
 * Used by both the Swing desktop app (ArrayList<Flight>) and the
 * web layer (Servlet + JSP + JDBC).
 *
 * Added: int id  – primary key from the 'flights' DB table.
 */
public class Flight {

    // --- Fields ---
    private int    id;             // DB primary key (0 if not yet persisted)
    private String flightNumber;
    private String destination;
    private String departureTime;  // format: "HH:MM"
    private double price;          // in INR (₹)

    // --- Constructors ---
    public Flight(String flightNumber, String destination, String departureTime, double price) {
        this.flightNumber  = flightNumber;
        this.destination   = destination;
        this.departureTime = departureTime;
        this.price         = price;
    }

    // --- Getters ---
    public int    getId()            { return id; }
    public String getFlightNumber()  { return flightNumber; }
    public String getDestination()   { return destination; }
    public String getDepartureTime() { return departureTime; }
    public double getPrice()         { return price; }

    // --- Setters ---
    public void setId(int id)                              { this.id            = id; }
    public void setFlightNumber(String flightNumber)       { this.flightNumber  = flightNumber; }
    public void setDestination(String destination)         { this.destination   = destination; }
    public void setDepartureTime(String departureTime)     { this.departureTime = departureTime; }
    public void setPrice(double price)                     { this.price         = price; }

    /**
     * toString() – used in Swing JList and clipboard copy.
     * Format: "AF-102 | London (LHR) | 08:45 | ₹45000.00"
     */
    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %-8s | \u20B9%.2f",
                flightNumber, destination, departureTime, price);
    }
}
