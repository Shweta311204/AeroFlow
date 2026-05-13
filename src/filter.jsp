<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AeroFlow | Filter Flights</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', system-ui, sans-serif; background: #f5f7fc; color: #0f172a; min-height: 100vh; }

        .topbar { background: #fff; border-bottom: 1px solid #cbd5e1; padding: 12px 28px; display: flex; align-items: center; justify-content: space-between; }
        .brand  { font-size: 22px; font-weight: 700; color: #186edc; }

        .container { max-width: 420px; margin: 64px auto; padding: 0 16px; }

        .card { background: #fff; border: 1px solid #cbd5e1; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
        .card-header { background: #186edc; padding: 18px 24px; }
        .card-header h1 { color: #fff; font-size: 17px; font-weight: 700; }
        .card-header p  { color: #cce5ff; font-size: 12px; margin-top: 3px; }

        .card-body { padding: 24px; }

        .form-group { display: flex; flex-direction: column; gap: 5px; margin-bottom: 18px; }
        label { font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.8px; color: #64748b; }

        input[type="number"] {
            padding: 12px 14px; border: 1px solid #cbd5e1; border-radius: 7px;
            font-size: 22px; font-weight: 700; color: #186edc;
            background: #d9e4ec; text-align: center;
        }
        input:focus { outline: 2px solid #186edc; border-color: transparent; background: #fff; }

        .radio-group { display: flex; flex-direction: column; gap: 10px; margin-top: 4px; }
        .radio-label { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #334155; cursor: pointer; }
        .radio-label input[type="radio"] { accent-color: #186edc; width: 16px; height: 16px; }

        .btn { padding: 10px 22px; border-radius: 7px; font-size: 13px; font-weight: 600; cursor: pointer; border: none; text-decoration: none; display: inline-block; }
        .btn-primary { background: #186edc; color: #fff; }
        .btn-primary:hover { background: #0e52ae; }
        .btn-secondary { background: #ecf1fa; color: #334155; border: 1px solid #cbd5e1; }
        .btn-secondary:hover { background: #cbd5e1; }

        .card-footer { background: #f0f4f8; border-top: 1px solid #e2e8f0; padding: 14px 24px; display: flex; justify-content: flex-end; gap: 10px; }
    </style>
</head>
<body>

<div class="topbar">
    <div class="brand">AeroFlow</div>
    <a href="flights?action=list" style="font-size:12px; color:#186edc; text-decoration:none;">&#8592; Back</a>
</div>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h1>&#8377; Price Filter</h1>
            <p>Set threshold to filter flights</p>
        </div>

        <div class="card-body">
            <!--
                POST to FlightServlet with action=doFilter
                Servlet queries DB with WHERE price <= / >= threshold → index.jsp
            -->
            <form method="post" action="flights">
                <input type="hidden" name="action" value="doFilter">

                <div class="form-group">
                    <label for="threshold">Price threshold (&#8377;)</label>
                    <input type="number" id="threshold" name="threshold"
                           value="5000" min="0" step="1" required>
                </div>

                <div class="form-group">
                    <label>Filter Mode</label>
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" name="mode" value="BELOW" checked>
                            Below &le; threshold
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="mode" value="ABOVE">
                            Above &ge; threshold
                        </label>
                    </div>
                </div>

                <div class="card-footer" style="margin: 0 -24px -24px; padding: 14px 24px;">
                    <a class="btn btn-secondary" href="flights?action=list">Cancel</a>
                    <button class="btn btn-primary" type="submit">Apply Filter</button>
                </div>

            </form>
        </div>

    </div>
</div>

</body>
</html>
