import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AddFlightDialog.java
 * Modal dialog for adding or editing a Flight record.
 * Redesigned to match AeroFlow Pro design system.
 * Pass null as 'existing' to create a new flight; pass an existing Flight to edit it.
 */
public class AddFlightDialog extends JDialog {

    // ── Design tokens ──────────────────────────────────────────────────
    private static final Color C_PRIMARY      = new Color(24,  110, 220);
    private static final Color C_PRIMARY_LITE = new Color(204, 229, 255);
    private static final Color C_PRIMARY_DARK = new Color(14,   82, 174);
    private static final Color C_SURFACE      = new Color(247, 249, 252);
    private static final Color C_SURFACE_LOW  = new Color(240, 244, 248);
    private static final Color C_FIELD_BG     = new Color(217, 228, 236);
    private static final Color C_ON_SURFACE   = new Color(15,  23,  42);
    private static final Color C_OUTLINE      = new Color(113, 124, 132);
    private static final Color C_BORDER       = new Color(210, 220, 228);
    private static final Color C_ERROR        = new Color(159, 64, 61);
    private static final Color C_ACTIVE_NAV   = new Color(239, 246, 255);

    private final JTextField tfNumber      = styledField("e.g. AF-102");
    private final JTextField tfDestination = styledField("e.g. London (LHR)");
    private final JTextField tfDeparture   = styledField("HH:MM  (e.g. 08:45)");
    private final JTextField tfPrice       = styledField("e.g. 45000");

    private Flight result = null;

    public AddFlightDialog(Frame owner, Flight existing) {
        super(owner, existing == null ? "Add New Flight" : "Edit Flight", true);
        setUndecorated(false);
        getContentPane().setBackground(C_SURFACE);

        // Pre-fill if editing
        if (existing != null) {
            tfNumber.setText(existing.getFlightNumber());
            tfDestination.setText(existing.getDestination());
            tfDeparture.setText(existing.getDepartureTime());
            tfPrice.setText(String.valueOf(existing.getPrice()));
        }

        setLayout(new BorderLayout(0, 0));
        add(buildHeader(existing), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(existing), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(500, 420));
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // ── Header strip (mirrors AeroFlow top-bar style) ──────────────────
    private JPanel buildHeader(Flight existing) {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(C_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Icon + Title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);

        // Plane icon badge
        JLabel iconBadge = new JLabel("");
        iconBadge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconBadge.setForeground(new Color(204, 229, 255));
        titleRow.add(iconBadge);

        JLabel title = new JLabel(existing == null ? "Create New Flight Assignment" : "Edit Flight Assignment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleRow.add(title);
        header.add(titleRow, BorderLayout.WEST);

        JLabel sub = new JLabel("Input flight parameters for global operations schedule.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(180, 210, 240));
        header.add(sub, BorderLayout.SOUTH);

        return header;
    }

    // ── Body: two-column form ──────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 28));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.NORTHWEST;
        lc.insets = new Insets(0, 0, 4, 16);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = new Insets(0, 0, 20, 0);

        // Row 0 – divider label
        GridBagConstraints div = new GridBagConstraints();
        div.gridx = 0; div.gridy = 0; div.gridwidth = 4;
        div.fill  = GridBagConstraints.HORIZONTAL;
        div.insets = new Insets(0, 0, 18, 0);
        JLabel section1 = sectionLabel("FLIGHT NUMBER & DESTINATION ");
        
        body.add(section1,div);

        // Row 1 – Flight Number | Destination (side by side)
        addField(body, lc, fc, 0, 1, "Flight Number", tfNumber);
        addField(body, lc, fc, 2, 1, "Destination", tfDestination);

        // Divider label
        div.gridy = 2; div.insets = new Insets(4, 0, 18, 0);
        body.add(sectionLabel("SCHEDULE & PRICING"), div);

        // Row 3 – Departure | Price
        addField(body, lc, fc, 0, 3, "Departure Time (HH:MM)", tfDeparture);
        addField(body, lc, fc, 2, 3, "Base Price", tfPrice);

        // Tip
        GridBagConstraints tc = new GridBagConstraints();
        tc.gridx = 0; tc.gridy = 4; tc.gridwidth = 4;
        tc.fill = GridBagConstraints.HORIZONTAL;
        tc.insets = new Insets(8, 0, 0, 0);
        JLabel tip = new JLabel("Departure must be in 24-hour HH:MM format. Price must be a positive number.");
        tip.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tip.setForeground(C_OUTLINE);
        body.add(tip, tc);

        return body;
    }

    private void addField(JPanel p, GridBagConstraints lc, GridBagConstraints fc,
                          int col, int row, String label, JTextField field) {
        // Label
        lc.gridx = col; lc.gridy = row;
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(C_OUTLINE);
        p.add(lbl, lc);

        // Field
        fc.gridx = col; fc.gridy = row;
        if (col == 0) fc.insets = new Insets(0, 0, 20, 12);
        else          fc.insets = new Insets(0, 0, 20, 0);
        p.add(field, fc);
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(160, 180, 200));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 233, 240)),
                BorderFactory.createEmptyBorder(0, 0, 6, 0)));
        return lbl;
    }

    // ── Footer: action buttons ─────────────────────────────────────────
    private JPanel buildFooter(Flight existing) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(C_SURFACE_LOW);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JLabel hint = new JLabel("Press Esc to cancel");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(C_OUTLINE);
        footer.add(hint, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);

        JButton btnCancel = new JButton("Discard");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setForeground(C_OUTLINE);
        btnCancel.setBackground(C_SURFACE_LOW);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        btnCancel.setOpaque(true);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(90, 36));
        btnCancel.addActionListener(e -> dispose());

        JButton btnOK = new JButton(existing == null ? "  Authorize Flight" : " Save Changes");
        btnOK.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOK.setForeground(Color.WHITE);
        btnOK.setBackground(C_PRIMARY);
        btnOK.setFocusPainted(false);
        btnOK.setBorderPainted(false);
        btnOK.setOpaque(true);
        btnOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOK.setPreferredSize(new Dimension(160, 36));
        btnOK.addActionListener(e -> { if (validateAndBuild()) dispose(); });

        btnOK.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnOK.setBackground(C_PRIMARY_DARK); }
            public void mouseExited(MouseEvent e)  { btnOK.setBackground(C_PRIMARY); }
        });

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        btns.add(btnCancel);
        btns.add(btnOK);
        footer.add(btns, BorderLayout.EAST);

        return footer;
    }

    private static JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(16) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(160, 180, 200));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets i = getInsets();
                    g2.drawString(placeholder, i.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(new Color(41, 52, 58));
        tf.setBackground(C_FIELD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 228), 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        tf.setCaretColor(C_PRIMARY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(8, 11, 8, 11)));
                tf.repaint();
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 215, 228), 1, true),
                        BorderFactory.createEmptyBorder(9, 12, 9, 12)));
                tf.repaint();
            }
        });
        return tf;
    }

    private boolean validateAndBuild() {
        String num      = tfNumber.getText().trim();
        String dest     = tfDestination.getText().trim();
        String dep      = tfDeparture.getText().trim();
        String priceStr = tfPrice.getText().trim();

        if (num.isEmpty() || dest.isEmpty() || dep.isEmpty() || priceStr.isEmpty()) {
            showError("All fields are required.", "Validation Error");
            return false;
        }
        if (!dep.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            showError("Departure time must be HH:MM (e.g. 08:45).", "Validation Error");
            return false;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Price must be a positive number.", "Validation Error");
            return false;
        }
        result = new Flight(num, dest, dep, price);
        return true;
    }

    private void showError(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public Flight getResult() { return result; }
}