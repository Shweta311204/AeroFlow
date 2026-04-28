import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightTableModel.java
 * Adapts an ArrayList<Flight> for display in a JTable.
 */
public class FlightTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Flight No.", "Destination", "Departure", "Price (\u20B9)"};

    private final ArrayList<Flight> flights;

    public FlightTableModel(ArrayList<Flight> flights) {
        this.flights = flights;
    }

    @Override public int getRowCount()    { return flights.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Flight f = flights.get(row);
        return switch (col) {
            case 0 -> f.getFlightNumber();
            case 1 -> f.getDestination();
            case 2 -> f.getDepartureTime();
            case 3 -> String.format("%.2f", f.getPrice());
            default -> "";
        };
    }

    public void addFlight(Flight f) {
        flights.add(f);
        fireTableDataChanged();
    }

    public void removeFlight(int row) {
        flights.remove(row);
        fireTableDataChanged();
    }

    public Flight getFlight(int row) { return flights.get(row); }

    public void showFlights(List<Flight> subset) {
        flights.clear();
        flights.addAll(subset);
        fireTableDataChanged();
    }

    public ArrayList<Flight> getFlights() { return flights; }
}