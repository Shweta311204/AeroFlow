import java.util.Comparator;

/**
 * FlightComparators.java
 * Provides two named Comparator<Flight> implementations used for sorting.
 */
public class FlightComparators {
    /**
     * Sort by departure time lexicographically.
     * Works correctly because times are stored as "HH:MM" (zero-padded 24-h).
     */
    public static final Comparator<Flight> BY_DEPARTURE_TIME =
            Comparator.comparing(Flight::getDepartureTime);

    /**
     * Sort by ticket price ascending (cheapest first).
     */
    public static final Comparator<Flight> BY_PRICE =
            Comparator.comparingDouble(Flight::getPrice);
}