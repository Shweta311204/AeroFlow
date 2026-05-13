<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
    // Preserve form values on error
    String pNum  = request.getParameter("flightNumber")  != null ? request.getParameter("flightNumber")  : "";
    String pDest = request.getParameter("destination")   != null ? request.getParameter("destination")   : "";
    String pDep  = request.getParameter("departureTime") != null ? request.getParameter("departureTime") : "";
    String pPri  = request.getParameter("price")         != null ? request.getParameter("price")         : "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AeroFlow | Add Flight</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', system-ui, sans-serif; background: #f5f7fc; color: #0f172a; min-height: 100vh; }

        .topbar { background: #fff; border-bottom: 1px solid #cbd5e1; padding: 12px 28px; display: flex; align-items: center; justify-content: space-between; }
        .brand  { font-size: 22px; font-weight: 700; color: #186edc; }

        .container { max-width: 620px; margin: 48px auto; padding: 0 16px; }

        .card { background: #fff; border: 1px solid #cbd5e1; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }

        .card-header { background: #186edc; padding: 22px 28px; }
        .card-header h1 { color: #fff; font-size: 18px; font-weight: 700; }
        .card-header p  { color: #cce5ff; font-size: 12px; margin-top: 4px; }

        .card-body { padding: 28px; }

        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .form-group { display: flex; flex-direction: column; gap: 5px; }
        .form-group.full { grid-column: span 2; }

        label { font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.8px; color: #64748b; }

        input[type="text"], input[type="number"] {
            padding: 10px 13px; border: 1px solid #cbd5e1; border-radius: 7px;
            font-size: 13px; background: #d9e4ec; color: #29343a;
            transition: border-color 0.15s, background 0.15s;
        }
        input:focus { outline: 2px solid #186edc; border-color: transparent; background: #fff; }
        input::placeholder { color: #94a3b8; font-style: italic; }

        .error-banner {
            background: #fef2f2; border: 1px solid #fca5a5;
            color: #b91c1c; border-radius: 7px;
            padding: 10px 14px; font-size: 13px; margin-bottom: 20px;
        }

        .tip { font-size: 11px; color: #94a3b8; font-style: italic; margin-top: 14px; }

        .card-footer { background: #f0f4f8; border-top: 1px solid #e2e8f0; padding: 14px 28px; display: flex; justify-content: flex-end; gap: 10px; align-items: center; }
        .hint { font-size: 10px; color: #94a3b8; flex: 1; }

        .btn { padding: 9px 20px; border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer; border: none; text-decoration: none; display: inline-block; }
        .btn-primary { background: #186edc; color: #fff; }
        .btn-primary:hover { background: #0e52ae; }
        .btn-secondary { background: #fff; color: #64748b; border: 1px solid #cbd5e1; }
        .btn-secondary:hover { background: #f0f4f8; }

        a.back { font-size: 12px; color: #186edc; text-decoration: none; display: inline-flex; align-items: center; gap: 4px; margin-bottom: 18px; }
        a.back:hover { text-decoration: underline; }
    </style>
</head>
<body>

<div class="topbar">
    <div class="brand">AeroFlow</div>
    <a href="flights?action=list" style="font-size:12px; color:#186edc; text-decoration:none;">&#8592; Back to Flight Log</a>
</div>

<div class="container">
    <a class="back" href="flights?action=list">&#8592; Back to Flight Log</a>

    <div class="card">

        <!-- Header -->
        <div class="card-header">
            <h1>&#9992; Create New Flight Assignment</h1>
            <p>Input flight parameters for global operations schedule.</p>
        </div>

        <!-- Body -->
        <div class="card-body">
            <% if (error != null && !error.isEmpty()) { %>
            <div class="error-banner">&#9888; <%= error %></div>
            <% } %>

            <!--
                POST to FlightServlet with action=doAdd
                Servlet validates → inserts via JDBC → redirects to flight list
            -->
            <form method="post" action="flights">
                <input type="hidden" name="action" value="doAdd">

                <div class="form-grid">

                    <!-- Flight Number -->
                    <div class="form-group">
                        <label for="flightNumber">Flight Number</label>
                        <input type="text" id="flightNumber" name="flightNumber"
                               placeholder="e.g. AF-102" required
                               value="<%= pNum %>">
                    </div>

                    <!-- Destination -->
                    <div class="form-group">
                        <label for="destination">Destination</label>
                        <input type="text" id="destination" name="destination"
                               placeholder="e.g. London (LHR)" required
                               value="<%= pDest %>">
                    </div>

                    <!-- Departure Time -->
                    <div class="form-group">
                        <label for="departureTime">Departure Time (HH:MM)</label>
                        <input type="text" id="departureTime" name="departureTime"
                               placeholder="e.g. 08:45" pattern="^([01]?\d|2[0-3]):[0-5]\d$"
                               required value="<%= pDep %>">
                    </div>

                    <!-- Price -->
                    <div class="form-group">
                        <label for="price">Base Price (&#8377;)</label>
                        <input type="number" id="price" name="price"
                               placeholder="e.g. 45000" min="0" step="0.01"
                               required value="<%= pPri %>">
                    </div>

                </div>

                <p class="tip">&#9432; Departure must be in 24-hour HH:MM format. Price must be a positive number.</p>

                <!-- Footer -->
                <div class="card-footer" style="margin: 24px -28px -28px; padding: 14px 28px;">
                    <span class="hint">Press Esc or click Discard to cancel.</span>
                    <a class="btn btn-secondary" href="flights?action=list">Discard</a>
                    <button class="btn btn-primary" type="submit">&#10003; Authorize Flight</button>
                </div>

            </form>
        </div>

    </div>
</div>

<script>
    // Close on Escape
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') window.location.href = 'flights?action=list';
    });
</script>
</body>
</html>
