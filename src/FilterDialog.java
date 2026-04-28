import javax.swing.*;
import java.awt.*;

/**
 * FilterDialog.java
 * Lets the user enter a price threshold and choose whether to show
 * flights BELOW or ABOVE that value. Enhanced UI matching AeroFlow design system.
 */
public class FilterDialog extends JDialog {
    public enum Mode { BELOW, ABOVE }

    private static final Color C_PRIMARY      = new Color(24,  110, 220);
    private static final Color C_SURFACE    = new Color(247, 249, 252);
    private static final Color C_ON_SURFACE = new Color(41, 52, 58);
    private static final Color C_OUTLINE    = new Color(113, 124, 132);
    private static final Color C_BORDER     = new Color(210, 220, 228);
    private static final Color C_FIELD_BG   = new Color(217, 228, 236);

    private final JTextField   tfThreshold = new JTextField("5000", 12);
    private final JRadioButton rbBelow     = new JRadioButton("Below \u2264 threshold", true);
    private final JRadioButton rbAbove     = new JRadioButton("Above \u2265 threshold", false);
    private boolean confirmed = false;

    public FilterDialog(Frame owner) {
        super(owner, "Filter Flights by Price", true);
        getContentPane().setBackground(C_SURFACE);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbBelow);
        bg.add(rbAbove);

        // Style radio buttons
        for (JRadioButton rb : new JRadioButton[]{rbBelow, rbAbove}) {
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            rb.setForeground(C_ON_SURFACE);
            rb.setBackground(Color.WHITE);
            rb.setOpaque(false);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // Style text field
        tfThreshold.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tfThreshold.setForeground(C_PRIMARY);
        tfThreshold.setBackground(C_FIELD_BG);
        tfThreshold.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tfThreshold.setHorizontalAlignment(JTextField.CENTER);

        // ---- Main panel ----
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(Color.WHITE);

        // Header strip
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel("\u20B9  Price Filter");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Set threshold to filter flights");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(204, 229, 255));
        JPanel hText = new JPanel(new GridLayout(2,1,0,2));
        hText.setOpaque(false);
        hText.add(title); hText.add(sub);
        header.add(hText, BorderLayout.CENTER);
        main.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 16, 28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.gridx  = 0; gc.gridy = 0; gc.gridwidth = 2;

        JLabel threshLabel = new JLabel("Price threshold (\u20B9)");
        threshLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        threshLabel.setForeground(C_OUTLINE);
        body.add(threshLabel, gc);

        gc.gridy = 1;
        body.add(tfThreshold, gc);

        gc.gridy = 2; gc.insets = new Insets(14, 4, 2, 4);
        JLabel modeLabel = new JLabel("Filter mode");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        modeLabel.setForeground(C_OUTLINE);
        body.add(modeLabel, gc);

        gc.gridy = 3; gc.insets = new Insets(4, 4, 2, 4);
        body.add(rbBelow, gc);
        gc.gridy = 4; gc.insets = new Insets(2, 4, 6, 4);
        body.add(rbAbove, gc);

        main.add(body, BorderLayout.CENTER);

        // Buttons
        JButton btnApply  = styleBtn("Apply Filter", C_PRIMARY, Color.WHITE, true);
        JButton btnCancel = styleBtn("Cancel", new Color(240, 244, 248), C_ON_SURFACE, false);

        btnApply.addActionListener(e -> {
            if (validateThreshold()) { confirmed = true; dispose(); }
        });
        btnCancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        buttons.add(btnCancel);
        buttons.add(btnApply);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
        pack();
        setMinimumSize(new Dimension(340, 310));
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private JButton styleBtn(String text, Color bg, Color fg, boolean shadow) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        if (shadow) btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    private boolean validateThreshold() {
        try {
            double v = Double.parseDouble(tfThreshold.getText().trim());
            if (v < 0) throw new NumberFormatException();
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public double  getThreshold() { return Double.parseDouble(tfThreshold.getText().trim()); }
    public Mode    getMode()      { return rbBelow.isSelected() ? Mode.BELOW : Mode.ABOVE; }
}