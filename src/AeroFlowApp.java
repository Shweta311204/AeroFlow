import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AeroFlowApp.java – AeroFlow  | Airline Flight Schedule Manager
 *
 * Changes in this version:
 *   - Search bar is now inline above the flight table (always visible, no dialog)
 *   - Clicking "Search" in sidebar focuses the inline search bar directly
 *   - Vibrant modern azure blue palette (rgb 24,110,220) replacing dull slate
 *   - Crisper card, button, hover states
 *   - All features preserved: edit, delete, sort, filter, add, dashboard
 */
public class AeroFlowApp extends JFrame {

    // ── Modern colour palette ─────────────────────────────────────────
    private static final Color C_PRIMARY      = new Color(24,  110, 220);
    private static final Color C_PRIMARY_DARK = new Color(14,   82, 174);
    private static final Color C_PRIMARY_LITE = new Color(219, 234, 254);
    private static final Color C_SURFACE      = new Color(245, 247, 252);
    private static final Color C_SURFACE_LOW  = new Color(236, 241, 250);
    private static final Color C_CARD         = Color.WHITE;
    private static final Color C_ON_SURFACE   = new Color(15,  23,  42);
    private static final Color C_MUTED        = new Color(100, 116, 139);
    private static final Color C_BORDER       = new Color(203, 213, 225);
    private static final Color C_ROW_HOVER    = new Color(239, 246, 255);
    private static final Color C_ERROR        = new Color(220,  38,  38);
    private static final Color C_SUCCESS      = new Color(22,  163,  74);
    private static final Color C_ACTIVE_NAV   = new Color(239, 246, 255);

    // ── Data ───────────────────────────────────────────────────────────
    private final ArrayList<Flight> masterList  = new ArrayList<>();
    private final ArrayList<Flight> displayList = new ArrayList<>();
    private boolean isFiltered = false;

    // ── Table ──────────────────────────────────────────────────────────
    private FlightTableModel tableModel;
    private JTable           flightTable;
    private int              hoveredRow = -1;

    // ── Inline search (always visible above table) ─────────────────────
    private JTextField searchField;

    // ── Status labels ──────────────────────────────────────────────────
    private final JLabel lblStatus = new JLabel("Showing all flights");
    private final JLabel lblCount  = new JLabel();

    // ── Stat card value labels (updated live) ──────────────────────────
    private JLabel statTotal, statAvgPrice;

    // ── Card layout center ─────────────────────────────────────────────
    private JPanel contentCard;
    private JPanel dashboardPanel;

    // ── Sidebar active button ─────────────────────────────────────────
    private JButton activeSideBtn = null;

    // =================================================================
    // CONSTRUCTOR
    // =================================================================
    public AeroFlowApp() {
        super("AeroFlow | Flight Schedule Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 600));
        setSize(1100, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_SURFACE);

        seedData();

        tableModel  = new FlightTableModel(displayList);
        flightTable = buildTable(tableModel);
        refreshDisplay(masterList);

        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // =================================================================
    // SEED DATA
    // =================================================================
    private void seedData() {
        masterList.add(new Flight("AF-102", "London (LHR)",   "08:45", 45000.00));
        masterList.add(new Flight("AF-345", "Tokyo (NRT)",    "11:20", 92000.00));
        masterList.add(new Flight("AF-098", "New York (JFK)", "14:15", 28000.00));
        masterList.add(new Flight("AF-771", "Paris (CDG)",    "17:30", 31500.00));
        masterList.add(new Flight("IN-204", "Mumbai (BOM)",   "06:00",  3800.00));
        masterList.add(new Flight("IN-317", "Delhi (DEL)",    "09:10",  2950.00));
        masterList.add(new Flight("IN-510", "Chennai (MAA)",  "13:25",  4200.00));
        masterList.add(new Flight("IN-622", "Kolkata (CCU)",  "16:45",  4750.00));
    }

    // =================================================================
    // TOP BAR
    // =================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
                BorderFactory.createEmptyBorder(11, 24, 11, 24)));

        // Brand


       
        JLabel brand = new JLabel("AeroFlow");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(C_PRIMARY);
        bar.add(brand, BorderLayout.WEST);


        // Right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel livePill = new JLabel("  \u25CF  Live  ");
        livePill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        livePill.setForeground(C_SUCCESS);
        livePill.setBackground(new Color(220, 252, 231));
        livePill.setOpaque(true);
        livePill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(134, 239, 172), 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        

        right.add(livePill);
       
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // =================================================================
    // SIDEBAR
    // =================================================================
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(C_SURFACE_LOW);
        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, C_BORDER),
                BorderFactory.createEmptyBorder(24, 0, 20, 0)));
        side.setPreferredSize(new Dimension(210, 0));

        // Workspace chip
        JPanel chip = new JPanel(new GridLayout(2, 1, 0, 2));
        chip.setBackground(C_CARD);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        chip.setMaximumSize(new Dimension(178, 56));

        JLabel wsName = new JLabel("Global Operations");
        wsName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        wsName.setForeground(C_ON_SURFACE);
        JLabel wsTerm = new JLabel("Terminal A-12");
        wsTerm.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        wsTerm.setForeground(C_MUTED);
        chip.add(wsName); chip.add(wsTerm);

        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        chipRow.setOpaque(false);
        chipRow.setMaximumSize(new Dimension(210, 62));
        chipRow.add(chip);
        side.add(chipRow);
        side.add(Box.createVerticalStrut(14));

       
        side.add(Box.createVerticalStrut(8));

        // Nav items
        String[][] nav = {
            {"Dashboard",  "dashboard"},
            {"Flight Log",       "flightlog"},
            {"Add Flight",       "addflight"},
            {"Search",     "search"},
        };

        for (String[] item : nav) {
            JButton btn = new JButton("   " + item[0]);
            String key = item[1];
            btn.setMaximumSize(new Dimension(210, 40));
            btn.setPreferredSize(new Dimension(210, 40));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setBackground(C_SURFACE_LOW);
            btn.setForeground(C_ON_SURFACE);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (btn != activeSideBtn) btn.setBackground(C_ACTIVE_NAV);
                }
                public void mouseExited(MouseEvent e) {
                    if (btn != activeSideBtn) btn.setBackground(C_SURFACE_LOW);
                }
            });
            btn.addActionListener(e -> handleNav(key, btn));

            if (key.equals("flightlog")) {
                activeSideBtn = btn;
                btn.setBackground(C_ACTIVE_NAV);
                btn.setForeground(C_PRIMARY);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
            side.add(btn);
        }

        side.add(Box.createVerticalGlue());
        side.add(makeDivider());

        // Help
        JButton help = new JButton("Help Center");
        help.setMaximumSize(new Dimension(210, 40));
        help.setHorizontalAlignment(SwingConstants.LEFT);
        help.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        help.setForeground(C_MUTED);
        help.setBackground(C_SURFACE_LOW);
        help.setFocusPainted(false);
        help.setBorderPainted(false);
        help.setOpaque(true);
        help.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        help.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "AeroFlow Pro v2.0\nContact your system administrator for support.",
                "Help Center", JOptionPane.INFORMATION_MESSAGE));
        side.add(help);

        return side;
    }

    private JSeparator makeDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(C_BORDER);
        s.setMaximumSize(new Dimension(210, 1));
        return s;
    }

    private void handleNav(String key, JButton btn) {
        if (activeSideBtn != null) {
            activeSideBtn.setBackground(C_SURFACE_LOW);
            activeSideBtn.setForeground(C_ON_SURFACE);
            activeSideBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        activeSideBtn = btn;
        btn.setBackground(C_ACTIVE_NAV);
        btn.setForeground(C_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        CardLayout cl = (CardLayout) contentCard.getLayout();
        switch (key) {
            case "dashboard" -> { cl.show(contentCard, "dashboard"); rebuildDashboard(); }
            case "flightlog" -> cl.show(contentCard, "table");
            case "addflight" -> { cl.show(contentCard, "table"); openAddDialog(); }
            case "search"    -> {
                cl.show(contentCard, "table");      // keep table visible
                searchField.requestFocusInWindow(); // jump focus to inline search bar
                searchField.selectAll();
            }
        }
    }

    // =================================================================
    // CENTER (table card + dashboard card via CardLayout)
    // =================================================================
    private JPanel buildCenter() {
        contentCard = new JPanel(new CardLayout());
        contentCard.setBackground(C_SURFACE);
        contentCard.add(buildTableCard(), "table");
        dashboardPanel = buildDashboardCard();
        contentCard.add(dashboardPanel,   "dashboard");
        return contentCard;
    }

    // =================================================================
    // TABLE CARD  — page header + stats + INLINE SEARCH + table
    // =================================================================
    private JPanel buildTableCard() {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(C_SURFACE);
        outer.setBorder(BorderFactory.createEmptyBorder(22, 22, 14, 22));

        // ── Page header + toolbar ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel tg = new JPanel(new GridLayout(2, 1, 0, 4));
        tg.setOpaque(false);
        JLabel titleLbl = new JLabel("Flight Schedule");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(C_PRIMARY);
        JLabel subLbl = new JLabel("Terminal A-12  •  " + masterList.size() + " flights managed");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(C_MUTED);
        tg.add(titleLbl); tg.add(subLbl);
        header.add(tg, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toolbar.setOpaque(false);
        JButton btnTime   = toolbarBtn("Time",   false);
        JButton btnPrice  = toolbarBtn("Price",  false);
        JButton btnFilter = toolbarBtn("Filter", false);
        JButton btnReset  = toolbarBtn(" Reset",        true);
        btnTime  .addActionListener(e -> sortFlights(false));
        btnPrice .addActionListener(e -> sortFlights(true));
        btnFilter.addActionListener(e -> openFilterDialog());
        btnReset .addActionListener(e -> resetView());
        for (JButton b : new JButton[]{btnTime, btnPrice, btnFilter, btnReset}) toolbar.add(b);
        header.add(toolbar, BorderLayout.EAST);
        outer.add(header, BorderLayout.NORTH);

        // ── Stats + search stacked ──
        JPanel northStack = new JPanel();
        northStack.setLayout(new BoxLayout(northStack, BoxLayout.Y_AXIS));
        northStack.setOpaque(false);
        northStack.add(buildStatsRow());
        northStack.add(Box.createVerticalStrut(12));
        northStack.add(buildInlineSearch());
        northStack.add(Box.createVerticalStrut(10));
        outer.add(northStack, BorderLayout.CENTER);

        // ── Table ──
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(C_CARD);
        tableWrap.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        JScrollPane scroll = new JScrollPane(flightTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(C_CARD);
        tableWrap.add(scroll, BorderLayout.CENTER);
        outer.add(tableWrap, BorderLayout.SOUTH);

        return outer;
    }

    // =================================================================
    // INLINE SEARCH BAR  — always visible above the flight table
    // =================================================================
    private JPanel buildInlineSearch() {
        JPanel wrap = new JPanel(new BorderLayout(10, 0));
        wrap.setBackground(C_CARD);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 12)));
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Icon
        JLabel icon = new JLabel(" ");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        icon.setForeground(C_MUTED);
        wrap.add(icon, BorderLayout.WEST);

        // Text field — no inner border; the card is the border
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(C_ON_SURFACE);
        searchField.setBackground(C_CARD);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setCaretColor(C_PRIMARY);

        // Placeholder text via renderer
        searchField.putClientProperty("placeholder", "Search by destination or flight number...");
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                wrap.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(9, 15, 9, 11)));
            }
            public void focusLost(FocusEvent e) {
                wrap.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(10, 16, 10, 12)));
            }
        });

        // Live search on every keystroke
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applySearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applySearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
        });

        wrap.add(searchField, BorderLayout.CENTER);

        // Right: match badge + Clear button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        JLabel matchBadge = new JLabel();
        matchBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        matchBadge.setForeground(C_PRIMARY);
        matchBadge.setBackground(C_PRIMARY_LITE);
        matchBadge.setOpaque(false);
        matchBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        searchField.putClientProperty("matchBadge", matchBadge);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearBtn.setForeground(C_PRIMARY);
        clearBtn.setBackground(C_PRIMARY_LITE);
        clearBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { clearBtn.setBackground(new Color(196, 218, 255)); }
            public void mouseExited(MouseEvent e)  { clearBtn.setBackground(C_PRIMARY_LITE); }
        });
        clearBtn.addActionListener(e -> { searchField.setText(""); searchField.requestFocusInWindow(); });

        right.add(matchBadge);
        right.add(clearBtn);
        wrap.add(right, BorderLayout.EAST);

        return wrap;
    }

    /** Live-filter master list as user types */
    private void applySearch() {
        String q = searchField.getText().trim().toLowerCase();
        JLabel badge = (JLabel) searchField.getClientProperty("matchBadge");

        if (q.isEmpty()) {
            isFiltered = false;
            refreshDisplay(masterList);
            if (badge != null) { badge.setText(""); badge.setOpaque(false); badge.repaint(); }
            lblStatus.setText("Showing all flights.");
        } else {
            List<Flight> results = masterList.stream()
                    .filter(f -> f.getDestination().toLowerCase().contains(q)
                              || f.getFlightNumber().toLowerCase().contains(q))
                    .collect(Collectors.toList());
            isFiltered = true;
            displayList.clear();
            displayList.addAll(results);
            tableModel.fireTableDataChanged();
            updateCount();
            if (badge != null) {
                badge.setText(results.size() + " match" + (results.size() == 1 ? "" : "es"));
                badge.setOpaque(true);
                badge.repaint();
            }
            lblStatus.setText("Search: \"" + searchField.getText().trim() + "\" \u2014 " + results.size() + " result(s)");
        }
    }

    // =================================================================
    // STATS ROW
    // =================================================================
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        double avg = masterList.stream().mapToDouble(Flight::getPrice).average().orElse(0);
        statTotal    = new JLabel(String.valueOf(masterList.size()));
        statAvgPrice = new JLabel(String.format("\u20B9%.0f", avg));

        row.add(statCard("Total Flights",    statTotal,           false));
        row.add(statCard("Departures Today", new JLabel("12"),    false));
        row.add(statCard("Avg Price",        statAvgPrice,        false));
        row.add(statCard("System Status",    new JLabel("Optimal"), true));
        return row;
    }

    private JPanel statCard(String label, JLabel val, boolean filled) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 4));
        p.setBackground(filled ? C_PRIMARY : C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(filled ? C_PRIMARY : C_BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(filled ? new Color(186, 216, 255) : C_MUTED);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(filled ? Color.WHITE : C_ON_SURFACE);
        p.add(lbl); p.add(val);
        return p;
    }

    // =================================================================
    // DASHBOARD CARD
    // =================================================================
    private JPanel buildDashboardCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(C_SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel tg = new JPanel(new GridLayout(2, 1, 0, 4));
        tg.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(C_ON_SURFACE);
        JLabel sub = new JLabel("Live operational summary");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_MUTED);
        tg.add(title); tg.add(sub);
        card.add(tg, BorderLayout.NORTH);

        
        long intl = masterList.stream()
        .filter(f -> !f.getFlightNumber().startsWith("IN"))
        .count();
        long   dom    = masterList.stream().filter(f -> f.getFlightNumber().startsWith("IN")).count();
        double minP   = masterList.stream().mapToDouble(Flight::getPrice).min().orElse(0);
        double maxP   = masterList.stream().mapToDouble(Flight::getPrice).max().orElse(0);
        long   budget = masterList.stream().filter(f -> f.getPrice() < 5000).count();

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        grid.add(dashTile("Total Flights",          String.valueOf(masterList.size()), C_PRIMARY));
        grid.add(dashTile("International",          String.valueOf(intl),             new Color(99, 102, 241)));
        grid.add(dashTile("Domestic",               String.valueOf(dom),              new Color(20, 184, 166)));
        grid.add(dashTile("Min Price",              String.format("\u20B9%.0f", minP), new Color(37, 99, 235)));
        grid.add(dashTile("Max Price",              String.format("\u20B9%.0f", maxP), C_ERROR));
    

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel dashTile(String label, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1),
                        BorderFactory.createEmptyBorder(16, 18, 16, 18))));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(C_MUTED);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(accent);
        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private void rebuildDashboard() {
        dashboardPanel.removeAll();
        JPanel fresh = buildDashboardCard();
        dashboardPanel.setLayout(fresh.getLayout());
        dashboardPanel.setBackground(fresh.getBackground());
        dashboardPanel.setBorder(((JPanel) fresh).getBorder());
        for (Component c : fresh.getComponents())
            dashboardPanel.add(c, ((BorderLayout) fresh.getLayout()).getConstraints(c));
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    // =================================================================
    // TABLE — with hover + action column (click to Edit/Delete)
    // =================================================================
    private JTable buildTable(FlightTableModel model) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(C_PRIMARY_LITE);
        table.setSelectionForeground(C_ON_SURFACE);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setBackground(C_CARD);
        table.setForeground(C_ON_SURFACE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader th = table.getTableHeader();
        th.setBackground(C_SURFACE_LOW);
        th.setForeground(C_MUTED);
        th.setFont(new Font("Segoe UI", Font.BOLD, 10));
        th.setPreferredSize(new Dimension(0, 44));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        int[] widths = {110, 220, 110, 130};
        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) cm.getColumn(i).setPreferredWidth(widths[i]);

        // Striped row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                lbl.setFont(col == 0
                        ? new Font("Segoe UI", Font.BOLD, 13)
                        : new Font("Segoe UI", Font.PLAIN, 13));
                if (sel) {
                    lbl.setBackground(C_PRIMARY_LITE);
                    lbl.setForeground(C_PRIMARY_DARK);
                } else if (row == hoveredRow) {
                    lbl.setBackground(C_ROW_HOVER);
                    lbl.setForeground(C_ON_SURFACE);
                } else {
                    lbl.setBackground(row % 2 == 0 ? C_CARD : new Color(248, 250, 254));
                    lbl.setForeground(C_ON_SURFACE);
                }
                return lbl;
            }
        });

        addActionsColumn(table, model);

        // Hover tracking
        table.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r != hoveredRow) { hoveredRow = r; table.repaint(); }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) { hoveredRow = -1; table.repaint(); }
        });

        // Click → Edit or Delete on Actions column
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col != table.getColumnCount() - 1 || row < 0) return;
                Rectangle rect = table.getCellRect(row, col, false);
                if (e.getX() - rect.x < rect.width / 2) openEditDialog(row);
                else                                     deleteFlight(row);
            }
        });

        return table;
    }

    private void addActionsColumn(JTable table, FlightTableModel model) {
        TableColumn actCol = new TableColumn(model.getColumnCount());
        actCol.setHeaderValue("Actions");
        actCol.setPreferredWidth(150);
        actCol.setMinWidth(140);
        actCol.setCellRenderer((t, v, sel, foc, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 9));
            if (sel)                p.setBackground(C_PRIMARY_LITE);
            else if (row == hoveredRow) p.setBackground(C_ROW_HOVER);
            else p.setBackground(row % 2 == 0 ? C_CARD : new Color(248, 250, 254));
            p.add(actionBtn("Edit",   C_PRIMARY, Color.WHITE));
            p.add(actionBtn("Delete", C_ERROR,   Color.WHITE));
            return p;
        });
        table.addColumn(actCol);
    }

    private JButton actionBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(64, 30));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // =================================================================
    // STATUS BAR
    // =================================================================
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
                BorderFactory.createEmptyBorder(7, 22, 7, 22)));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(C_MUTED);
        lblCount .setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCount .setForeground(C_PRIMARY);
        updateCount();
        bar.add(lblStatus, BorderLayout.WEST);
        bar.add(lblCount,  BorderLayout.EAST);
        return bar;
    }

    // =================================================================
    // TOOLBAR BUTTON FACTORY
    // =================================================================
    private JButton toolbarBtn(String text, boolean secondary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (secondary) {
            btn.setBackground(C_SURFACE_LOW);
            btn.setForeground(C_ON_SURFACE);
            btn.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        } else {
            btn.setBackground(C_CARD);
            btn.setForeground(C_PRIMARY);
            btn.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        }
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(secondary ? C_BORDER : C_PRIMARY_LITE); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(secondary ? C_SURFACE_LOW : C_CARD); }
        });
        return btn;
    }

    // =================================================================
    // BUSINESS LOGIC
    // =================================================================
    private void refreshDisplay(List<Flight> source) {
        displayList.clear();
        displayList.addAll(source);
        tableModel.fireTableDataChanged();
        updateCount();
        updateStatCards();
    }

    private void updateCount() {
        lblCount.setText("Showing " + displayList.size() + " of " + masterList.size() + " flights");
    }

    private void updateStatCards() {
        if (statTotal != null) statTotal.setText(String.valueOf(masterList.size()));
        if (statAvgPrice != null) {
            double avg = masterList.stream().mapToDouble(Flight::getPrice).average().orElse(0);
            statAvgPrice.setText(String.format("\u20B9%.0f", avg));
        }
    }

    private void openAddDialog() {
        AddFlightDialog dlg = new AddFlightDialog(this, null);
        dlg.setVisible(true);
        Flight f = dlg.getResult();
        if (f != null) { masterList.add(f); resetView(); lblStatus.setText("Flight " + f.getFlightNumber() + " added."); }
    }

    private void openEditDialog(int row) {
        if (row < 0 || row >= displayList.size()) return;
        Flight orig = displayList.get(row);
        AddFlightDialog dlg = new AddFlightDialog(this, orig);
        dlg.setVisible(true);
        Flight updated = dlg.getResult();
        if (updated != null) {
            int idx = masterList.indexOf(orig);
            if (idx >= 0) masterList.set(idx, updated);
            refreshDisplay(isFiltered ? displayList : masterList);
            lblStatus.setText("Flight " + updated.getFlightNumber() + " updated.");
        }
    }

    private void deleteFlight(int row) {
        if (row < 0 || row >= displayList.size()) return;
        Flight f = displayList.get(row);
        int ch = JOptionPane.showConfirmDialog(this,
                "Delete flight " + f.getFlightNumber() + " to " + f.getDestination() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ch == JOptionPane.YES_OPTION) {
            masterList.remove(f);
            displayList.remove(row);
            tableModel.fireTableDataChanged();
            updateCount(); updateStatCards();
            lblStatus.setText("Flight " + f.getFlightNumber() + " deleted.");
        }
    }

    private void sortFlights(boolean byPrice) {
        Collections.sort(displayList, byPrice ? FlightComparators.BY_PRICE : FlightComparators.BY_DEPARTURE_TIME);
        tableModel.fireTableDataChanged();
        lblStatus.setText("Sorted by " + (byPrice ? "price." : "departure time."));
    }

    private void openFilterDialog() {
        FilterDialog dlg = new FilterDialog(this);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        double threshold = dlg.getThreshold();
        FilterDialog.Mode mode = dlg.getMode();
        List<Flight> filtered = masterList.stream()
                .filter(f -> mode == FilterDialog.Mode.BELOW ? f.getPrice() <= threshold : f.getPrice() >= threshold)
                .collect(Collectors.toList());
        isFiltered = true;
        refreshDisplay(filtered);
        lblStatus.setText("Filtered: price " + (mode == FilterDialog.Mode.BELOW ? "\u2264" : "\u2265") +
                " \u20B9" + String.format("%.0f", threshold) + " (" + filtered.size() + " results)");
    }

    private void resetView() {
        isFiltered = false;
        if (searchField != null) searchField.setText("");
        refreshDisplay(masterList);
        lblStatus.setText("Showing all flights.");
    }

    // =================================================================
    // MAIN
    // =================================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(AeroFlowApp::new);
    }
}