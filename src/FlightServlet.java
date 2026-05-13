import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * FlightServlet.java
 * Central controller servlet for AeroFlow web application.
 *
 * URL mapping:  /flights   (configured via @WebServlet and web.xml)
 *
 * Actions dispatched via request parameter 'action':
 *   (none / "list")  → list all flights          → index.jsp
 *   "add"            → show add-flight form       → add-flight.jsp
 *   "doAdd"          → POST: insert into DB       → redirect to list
 *   "delete"         → DELETE flight by id        → redirect to list
 *   "filter"         → show filter form           → filter.jsp
 *   "doFilter"       → POST: filter by price      → index.jsp (filtered)
 *   "sort"           → sort by column             → index.jsp (sorted)
 *   "search"         → search by keyword          → index.jsp (searched)
 *   "reset"          → clear filters/search       → redirect to list
 */
@WebServlet("/flights")
public class FlightServlet extends HttpServlet {

    private final FlightDAO dao = new FlightDAO();

    // =========================================================
    // GET  – navigation, list, sort, search, delete, reset
    // =========================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {

                /* ----- Show all flights ----- */
                case "list":
                    req.setAttribute("flights", dao.getAllFlights());
                    req.setAttribute("statusMsg", "Showing all flights.");
                    forward(req, resp, "/index.jsp");
                    break;

                /* ----- Show Add-Flight form ----- */
                case "add":
                    forward(req, resp, "/add-flight.jsp");
                    break;

                /* ----- Delete a flight ----- */
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.deleteFlight(id);
                    resp.sendRedirect(req.getContextPath() + "/flights?action=list&msg=deleted");
                    break;
                }

                /* ----- Sort ----- */
                case "sort": {
                    String by = req.getParameter("by");   // "price" or "time"
                    List<Flight> sorted = dao.sortFlights(by);
                    req.setAttribute("flights", sorted);
                    req.setAttribute("statusMsg",
                            "Sorted by " + ("price".equals(by) ? "price." : "departure time."));
                    forward(req, resp, "/index.jsp");
                    break;
                }

                /* ----- Search ----- */
                case "search": {
                    String kw = req.getParameter("q");
                    if (kw == null) kw = "";
                    List<Flight> results = dao.searchFlights(kw.trim());
                    req.setAttribute("flights", results);
                    req.setAttribute("statusMsg",
                            results.size() + " result(s) for \"" + kw + "\".");
                    req.setAttribute("searchQuery", kw);
                    forward(req, resp, "/index.jsp");
                    break;
                }

                /* ----- Show filter form ----- */
                case "filter":
                    forward(req, resp, "/filter.jsp");
                    break;

                /* ----- Reset / clear ----- */
                case "reset":
                default:
                    resp.sendRedirect(req.getContextPath() + "/flights?action=list");
                    break;
            }

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/flights?action=list&msg=badid");
        }
    }

    // =========================================================
    // POST – form submissions (add flight, apply filter)
    // =========================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        try {
            switch (action) {

                /* ----- Insert new flight into DB ----- */
                case "doAdd": {
                    String num  = req.getParameter("flightNumber").trim();
                    String dest = req.getParameter("destination").trim();
                    String dep  = req.getParameter("departureTime").trim();
                    String priceStr = req.getParameter("price").trim();

                    // Server-side validation
                    if (num.isEmpty() || dest.isEmpty() || dep.isEmpty() || priceStr.isEmpty()) {
                        req.setAttribute("error", "All fields are required.");
                        forward(req, resp, "/add-flight.jsp");
                        return;
                    }
                    if (!dep.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
                        req.setAttribute("error", "Departure time must be HH:MM (e.g. 08:45).");
                        forward(req, resp, "/add-flight.jsp");
                        return;
                    }
                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                        if (price < 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        req.setAttribute("error", "Price must be a positive number.");
                        forward(req, resp, "/add-flight.jsp");
                        return;
                    }

                    Flight f = new Flight(num, dest, dep, price);
                    dao.addFlight(f);
                    resp.sendRedirect(req.getContextPath() + "/flights?action=list&msg=added");
                    break;
                }

                /* ----- Apply price filter ----- */
                case "doFilter": {
                    double threshold = Double.parseDouble(req.getParameter("threshold").trim());
                    String mode      = req.getParameter("mode");   // "BELOW" or "ABOVE"
                    List<Flight> filtered = dao.filterByPrice(threshold, mode);
                    req.setAttribute("flights", filtered);
                    req.setAttribute("statusMsg",
                            "Filtered: price " + ("BELOW".equals(mode) ? "\u2264" : "\u2265") +
                            " \u20B9" + String.format("%.0f", threshold) +
                            " (" + filtered.size() + " results)");
                    forward(req, resp, "/index.jsp");
                    break;
                }

                default:
                    resp.sendRedirect(req.getContextPath() + "/flights?action=list");
                    break;
            }

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // Helper
    // =========================================================
    private void forward(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }
}
