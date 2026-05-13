import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightDAO.java
 * Data Access Object – all JDBC interactions with the 'flights' table.
 *
 * Operations:
 *   getAllFlights()           – SELECT *
 *   addFlight(Flight)        – INSERT
 *   deleteFlight(int id)     – DELETE by PK
 *   filterByPrice(double, String) – SELECT with WHERE clause
 *   sortFlights(String)      – SELECT with ORDER BY
 */
public class FlightDAO {

    /* ------------------------------------------------------------------ */
    /*  READ – get all flights                                              */
    /* ------------------------------------------------------------------ */
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Flight f = mapRow(rs);
                list.add(f);
            }
        }
        return list;
    }

    /* ------------------------------------------------------------------ */
    /*  CREATE – insert a new flight                                        */
    /* ------------------------------------------------------------------ */
    public boolean addFlight(Flight f) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, destination, departure_time, price) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, f.getFlightNumber());
            ps.setString(2, f.getDestination());
            ps.setString(3, f.getDepartureTime());
            ps.setDouble(4, f.getPrice());

            return ps.executeUpdate() > 0;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  DELETE – remove flight by PK                                        */
    /* ------------------------------------------------------------------ */
    public boolean deleteFlight(int id) throws SQLException {
        String sql = "DELETE FROM flights WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  FILTER – price below or above threshold                             */
    /* ------------------------------------------------------------------ */
    public List<Flight> filterByPrice(double threshold, String mode) throws SQLException {
        // mode: "BELOW" or "ABOVE"
        String operator = "ABOVE".equalsIgnoreCase(mode) ? ">=" : "<=";
        String sql = "SELECT * FROM flights WHERE price " + operator + " ? ORDER BY price";

        List<Flight> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /* ------------------------------------------------------------------ */
    /*  SORT – by departure_time or price                                   */
    /* ------------------------------------------------------------------ */
    public List<Flight> sortFlights(String sortBy) throws SQLException {
        String orderCol = "price".equalsIgnoreCase(sortBy) ? "price" : "departure_time";
        String sql = "SELECT * FROM flights ORDER BY " + orderCol;

        List<Flight> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /* ------------------------------------------------------------------ */
    /*  SEARCH – by flight number or destination (LIKE)                    */
    /* ------------------------------------------------------------------ */
    public List<Flight> searchFlights(String keyword) throws SQLException {
        String sql = "SELECT * FROM flights WHERE "
                   + "LOWER(flight_number) LIKE ? OR LOWER(destination) LIKE ?";

        List<Flight> list = new ArrayList<>();
        String pattern = "%" + keyword.toLowerCase() + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /* ------------------------------------------------------------------ */
    /*  Helper – map ResultSet row → Flight                                 */
    /* ------------------------------------------------------------------ */
    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight f = new Flight(
            rs.getString("flight_number"),
            rs.getString("destination"),
            rs.getString("departure_time"),
            rs.getDouble("price")
        );
        // Store DB id in flight number field temporarily OR use a subclass;
        // here we just tag the id into an attribute so JSP can use it.
        // We extend Flight with a transient 'id' field via setAttribute approach:
        f.setId(rs.getInt("id")); // Flight.java needs getId/setId — see note below
        return f;
    }
}
