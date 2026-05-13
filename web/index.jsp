<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Flight" %>
<%
    // Retrieve data set by servlet
    List<Flight> flights = (List<Flight>) request.getAttribute("flights");
    String statusMsg   = (String) request.getAttribute("statusMsg");
    String searchQuery = (String) request.getAttribute("searchQuery");
    String urlMsg      = request.getParameter("msg");

    if (statusMsg == null) statusMsg = "Showing all flights.";
    if (searchQuery == null) searchQuery = "";

    // Toast message
    String toast = "";
    if ("added".equals(urlMsg))   toast = "Flight added successfully!";
    if ("deleted".equals(urlMsg)) toast = "Flight deleted.";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AeroFlow | Flight Schedule Manager</title>
    <style>
        /* ── Reset & Base ── */
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', system-ui, sans-serif; background: #f5f7fc; color: #0f172a; min-height: 100vh; }

        /* ── Top Bar ── */
        .topbar {
            background: #fff;
            border-bottom: 1px solid #cbd5e1;
            padding: 12px 28px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .topbar .brand { font-size: 22px; font-weight: 700; color: #186edc; letter-spacing: -0.5px; }
        .live-pill {
            background: #dcfce7; color: #16a34a; border: 1px solid #86efac;
            border-radius: 20px; padding: 4px 14px; font-size: 11px; font-weight: 700;
        }

        /* ── Layout ── */
        .layout { display: flex; min-height: calc(100vh - 56px); }

        /* ── Sidebar ── */
        .sidebar {
            width: 210px; background: #ecf1fa;
            border-right: 1px solid #cbd5e1;
            padding: 24px 0 20px;
            display: flex; flex-direction: column;
        }
        .ws-chip {
            margin: 0 16px 16px;
            background: #fff; border: 1px solid #cbd5e1;
            border-radius: 8px; padding: 10px 14px;
        }
        .ws-chip .ws-name { font-weight: 700; font-size: 13px; }
        .ws-chip .ws-term { font-size: 11px; color: #64748b; margin-top: 2px; }

        .nav-section { padding: 0 10px; }
        .nav-section label {
            display: block; font-size: 9px; font-weight: 700;
            color: #94a3b8; letter-spacing: 1px; padding: 6px 10px 4px;
            text-transform: uppercase;
        }
        .nav-btn {
            display: block; width: 100%; text-align: left;
            background: none; border: none; cursor: pointer;
            padding: 9px 12px; border-radius: 7px;
            font-size: 13px; font-weight: 500; color: #334155;
            text-decoration: none; transition: background 0.15s;
        }
        .nav-btn:hover { background: #dbeafe; color: #186edc; }
        .nav-btn.active { background: #eff6ff; color: #186edc; font-weight: 700; }

        /* ── Main Content ── */
        .main { flex: 1; padding: 28px; overflow-y: auto; }
        .page-title { font-size: 20px; font-weight: 700; margin-bottom: 4px; }
        .page-sub   { font-size: 13px; color: #64748b; margin-bottom: 20px; }

        /* ── Stat Cards ── */
        .stat-row { display: flex; gap: 16px; margin-bottom: 24px; flex-wrap: wrap; }
        .stat-card {
            background: #fff; border: 1px solid #cbd5e1;
            border-radius: 10px; padding: 16px 22px; min-width: 140px;
        }
        .stat-card .label { font-size: 10px; font-weight: 700; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.8px; }
        .stat-card .value { font-size: 28px; font-weight: 700; color: #186edc; margin-top: 4px; }
        .stat-card .sub   { font-size: 11px; color: #64748b; margin-top: 2px; }

        /* ── Card ── */
        .card {
            background: #fff; border: 1px solid #cbd5e1;
            border-radius: 10px; overflow: hidden;
        }
        .card-header {
            padding: 14px 20px;
            background: #fff;
            border-bottom: 1px solid #e2e8f0;
            display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
        }
        .card-header h2 { font-size: 15px; font-weight: 700; flex: 1; }

        /* ── Search bar ── */
        .search-form { display: flex; gap: 6px; }
        .search-form input {
            padding: 7px 13px; border: 1px solid #cbd5e1;
            border-radius: 7px; font-size: 13px;
            background: #f0f4f8; width: 220px;
        }
        .search-form input:focus { outline: 2px solid #186edc; border-color: transparent; }

        /* ── Buttons ── */
        .btn {
            padding: 7px 16px; border-radius: 7px;
            font-size: 12px; font-weight: 600; cursor: pointer;
            border: none; text-decoration: none; display: inline-block;
            transition: background 0.15s;
        }
        .btn-primary { background: #186edc; color: #fff; }
        .btn-primary:hover { background: #0e52ae; }
        .btn-outline { background: #fff; color: #186edc; border: 1px solid #cbd5e1; }
        .btn-outline:hover { background: #eff6ff; }
        .btn-secondary { background: #ecf1fa; color: #334155; border: 1px solid #cbd5e1; }
        .btn-secondary:hover { background: #cbd5e1; }
        .btn-sm { padding: 5px 12px; font-size: 11px; }
        .btn-danger { background: #dc2626; color: #fff; }
        .btn-danger:hover { background: #b91c1c; }

        /* ── Table ── */
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        thead tr { background: #f8faff; }
        thead th {
            padding: 11px 16px; text-align: left;
            font-size: 10px; font-weight: 700; text-transform: uppercase;
            color: #64748b; letter-spacing: 0.7px;
            border-bottom: 1px solid #e2e8f0;
        }
        tbody tr { border-bottom: 1px solid #f1f5f9; transition: background 0.1s; }
        tbody tr:hover { background: #eff6ff; }
        tbody td { padding: 11px 16px; color: #334155; }
        .price-cell { font-weight: 700; color: #186edc; }
        .no-data { text-align: center; padding: 40px; color: #94a3b8; }

        /* ── Status bar ── */
        .status-bar {
            background: #fff; border-top: 1px solid #cbd5e1;
            padding: 8px 24px; font-size: 11px; color: #64748b;
            display: flex; justify-content: space-between;
        }

        /* ── Toast ── */
        .toast {
            background: #16a34a; color: #fff; padding: 10px 20px;
            border-radius: 8px; font-size: 13px; font-weight: 600;
            position: fixed; bottom: 24px; right: 24px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            animation: fadeIn 0.3s ease;
        }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; } }
    </style>
</head>
<body>

<!-- ── Top Bar ── -->
<div class="topbar">
    <div class="brand">AeroFlow</div>
    <span class="live-pill">&#9679; Live</span>
</div>

<div class="layout">

    <!-- ── Sidebar ── -->
    <nav class="sidebar">
        <div class="ws-chip">
            <div class="ws-name">Global Operations</div>
            <div class="ws-term">Terminal A-12</div>
        </div>

        <div class="nav-section">
            <label>Main Menu</label>
            <a class="nav-btn" href="dashboard.jsp">&#9632; Dashboard</a>
            <a class="nav-btn active" href="flights?action=list">&#9632; Flight Log</a>
            <a class="nav-btn" href="flights?action=add">&#43; Add Flight</a>
        </div>

        <div class="nav-section" style="margin-top:10px;">
            <label>Tools</label>
            <a class="nav-btn" href="flights?action=filter">&#9660; Filter by Price</a>
            <a class="nav-btn" href="flights?action=sort&by=price">&#8593; Sort by Price</a>
            <a class="nav-btn" href="flights?action=sort&by=time">&#8709; Sort by Time</a>
            <a class="nav-btn" href="flights?action=reset">&#8635; Reset View</a>
        </div>
    </nav>

    <!-- ── Main ── -->
    <main class="main">
        <div class="page-title">Flight Log</div>
        <div class="page-sub">Manage and monitor all scheduled flights.</div>

        <!-- Stat cards -->
        <div class="stat-row">
            <div class="stat-card">
                <div class="label">Total Flights</div>
                <div class="value"><%= flights != null ? flights.size() : 0 %></div>
                <div class="sub">in schedule</div>
            </div>
            <%
                double total = 0, minP = Double.MAX_VALUE, maxP = 0;
                if (flights != null) {
                    for (Flight f : flights) {
                        total += f.getPrice();
                        if (f.getPrice() < minP) minP = f.getPrice();
                        if (f.getPrice() > maxP) maxP = f.getPrice();
                    }
                }
                double avg = (flights != null && !flights.isEmpty()) ? total / flights.size() : 0;
                if (minP == Double.MAX_VALUE) minP = 0;
            %>
            <div class="stat-card">
                <div class="label">Avg. Price</div>
                <div class="value">&#8377;<%= String.format("%.0f", avg) %></div>
                <div class="sub">across current view</div>
            </div>
            <div class="stat-card">
                <div class="label">Cheapest</div>
                <div class="value">&#8377;<%= String.format("%.0f", minP) %></div>
                <div class="sub">lowest ticket</div>
            </div>
            <div class="stat-card">
                <div class="label">Costliest</div>
                <div class="value">&#8377;<%= String.format("%.0f", maxP) %></div>
                <div class="sub">highest ticket</div>
            </div>
        </div>

        <!-- Flight table card -->
        <div class="card">
            <div class="card-header">
                <h2>Scheduled Flights</h2>

                <!-- Inline search -->
                <form class="search-form" method="get" action="flights">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="q" placeholder="Search flight or destination..."
                           value="<%= searchQuery %>">
                    <button class="btn btn-outline btn-sm" type="submit">Search</button>
                </form>

                <a class="btn btn-primary btn-sm" href="flights?action=add">+ Add Flight</a>
                <a class="btn btn-secondary btn-sm" href="flights?action=filter">Filter</a>
                <a class="btn btn-secondary btn-sm" href="flights?action=reset">Reset</a>
            </div>

            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Flight No.</th>
                            <th>Destination</th>
                            <th>Departure</th>
                            <th>Price (&#8377;)</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (flights == null || flights.isEmpty()) { %>
                        <tr><td colspan="6" class="no-data">No flights found.</td></tr>
                        <% } else {
                            int rowNum = 1;
                            for (Flight f : flights) { %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td><strong><%= f.getFlightNumber() %></strong></td>
                            <td><%= f.getDestination() %></td>
                            <td><%= f.getDepartureTime() %></td>
                            <td class="price-cell">&#8377;<%= String.format("%.2f", f.getPrice()) %></td>
                            <td>
                                <a class="btn btn-outline btn-sm"
                                   href="edit-flight.jsp?id=<%= f.getId() %>">Edit</a>
                                &nbsp;
                                <a class="btn btn-danger btn-sm"
                                   href="flights?action=delete&id=<%= f.getId() %>"
                                   onclick="return confirm('Delete flight <%= f.getFlightNumber() %>?')">Delete</a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

    </main>
</div>

<!-- Status bar -->
<div class="status-bar">
    <span><%= statusMsg %></span>
    <span>AeroFlow &copy; 2025</span>
</div>

<!-- Toast -->
<% if (!toast.isEmpty()) { %>
<div class="toast" id="toast"><%= toast %></div>
<script>setTimeout(() => document.getElementById('toast').remove(), 3000);</script>
<% } %>

</body>
</html>
