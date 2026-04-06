package hotel.javabeans.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Booking;
import hotel.javabeans.Hotel;
import hotel.javabeans.Reservation;
import hotel.javabeans.TypeOfRoom;

public class ReservationsPanel extends JPanel {

    private final Hotel hotel;
    private BookingsPanel bookingsPanel;

    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
        "Reservation ID", "Guest Name", "Phone",
        "Check-In", "Check-Out", "Est. Price",
        "Deposit", "Dep. Paid", "Confirmed"
    };

    public ReservationsPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    public void setBookingsPanel(BookingsPanel bp) {
        this.bookingsPanel = bp;
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Theme.BG);
        h.setBorder(new EmptyBorder(24, 28, 14, 28));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(Theme.sectionTitle("Reservations"));
        text.add(Box.createVerticalStrut(3));
        text.add(Theme.sectionSub("Create and manage guest reservations — confirm to enable booking"));
        h.add(text, BorderLayout.WEST);

        JLabel hint = new JLabel("Flow:  New → Deposit → Confirm → Book") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(Theme.YELLOW, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.alpha(Theme.YELLOW, 70));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g); g2.dispose();
            }
        };
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Theme.YELLOW);
        hint.setOpaque(false);
        hint.setBorder(new EmptyBorder(4, 12, 4, 12));
        h.add(hint, BorderLayout.EAST);
        return h;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Theme.SURFACE : Theme.CARD);
                    boolean confirmed = "Yes".equals(getModel().getValueAt(row, 8));
                    boolean depPaid   = "Yes".equals(getModel().getValueAt(row, 7));
                    c.setForeground(
                        confirmed ? Theme.GREEN :
                        depPaid   ? Theme.YELLOW :
                        Theme.TEXT
                    );
                }
                return c;
            }
        };
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(7).setCellRenderer(centre);
        table.getColumnModel().getColumn(8).setCellRenderer(centre);

        refreshTable();
        JScrollPane sp = Theme.scrollPane(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        return sp;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        bar.setBackground(Theme.BG);

        JButton addBtn     = Theme.primaryButton("+ New");
        JButton depositBtn = Theme.ghostButton("💳  Mark Deposit Paid");
        depositBtn.setPreferredSize(new Dimension(170, 34));
        JButton confirmBtn = Theme.ghostButton("✓  Confirm");
        JButton bookBtn    = Theme.primaryButton("📋  Book Now");
        bookBtn.setPreferredSize(new Dimension(120, 34));
        JButton removeBtn  = Theme.dangerButton("✕  Remove");
        JButton viewBtn    = Theme.ghostButton("👁  Details");

        addBtn.addActionListener(e -> showAddDialog());
        depositBtn.addActionListener(e -> markDeposit());
        confirmBtn.addActionListener(e -> confirmReservation());
        bookBtn.addActionListener(e -> bookNow());
        removeBtn.addActionListener(e -> removeSelected());
        viewBtn.addActionListener(e -> viewDetails());

        bar.add(addBtn);
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28)); setForeground(Theme.BORDER);
        }});
        bar.add(depositBtn);
        bar.add(confirmBtn);
        bar.add(bookBtn);
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28)); setForeground(Theme.BORDER);
        }});
        bar.add(removeBtn);
        bar.add(viewBtn);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        legend.add(legendDot(Theme.GREEN,  "Confirmed"));
        legend.add(legendDot(Theme.YELLOW, "Deposit Paid"));
        legend.add(legendDot(Theme.TEXT,   "Pending"));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.BG);
        wrapper.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Theme.BORDER),
            new EmptyBorder(4, 18, 4, 18)
        ));
        wrapper.add(bar,    BorderLayout.WEST);
        wrapper.add(legend, BorderLayout.EAST);
        return wrapper;
    }

    private JLabel legendDot(Color c, String text) {
        JLabel l = new JLabel("●  " + text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 10));
        l.setForeground(c);
        return l;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Reservation r : hotel.getReservations()) {
            tableModel.addRow(new Object[]{
                r.getReservationId().substring(0, 8) + "…",
                r.getGuestName(),
                r.getPhone(),
                r.getCheckInDate(),
                r.getCheckOutDate(),
                String.format("$%.2f", r.getEstimatedPrice()),
                String.format("$%.2f", r.getDepositAmount()),
                r.isDepositPaid() ? "Yes" : "No",
                r.isConfirmed()   ? "Yes" : "No"
            });
        }
    }

    // ── Add reservation dialog ────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = styledDialog("New Reservation", 480, 450);

        JTextField guestF    = Theme.textField();
        JTextField phoneF    = Theme.textField();
        JTextField checkInF  = Theme.textField(); checkInF.setText(LocalDate.now().plusDays(1).toString());
        JTextField checkOutF = Theme.textField(); checkOutF.setText(LocalDate.now().plusDays(3).toString());
        JTextField priceF    = Theme.textField();

        JTextField singleQty = Theme.textField(); singleQty.setText("0");
        JTextField doubleQty = Theme.textField(); doubleQty.setText("0");
        JTextField tripleQty = Theme.textField(); tripleQty.setText("0");

        JPanel roomPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        roomPanel.setOpaque(false);
        roomPanel.add(Theme.label("SINGLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(singleQty);
        roomPanel.add(Theme.label("DOUBLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(doubleQty);
        roomPanel.add(Theme.label("TRIPLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(tripleQty);

        JPanel form = formPanel(
            new String[]{"Guest Name", "Phone", "Check-In (YYYY-MM-DD)",
                         "Check-Out (YYYY-MM-DD)", "Est. Price ($)", "Room Quantities"},
            new JComponent[]{guestF, phoneF, checkInF, checkOutF, priceF, roomPanel}
        );

        JLabel depositPreview = Theme.label("Deposit (40%): $0.00", Theme.FONT_SMALL, Theme.PINK_SOFT);
        priceF.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                try {
                    double p = Double.parseDouble(priceF.getText().trim());
                    depositPreview.setText(String.format("Deposit (40%%): $%.2f", p * 0.4));
                } catch (NumberFormatException ignored) {
                    depositPreview.setText("Deposit (40%): $—");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Theme.SURFACE);
        bottom.setBorder(new EmptyBorder(0, 24, 0, 16));
        bottom.add(depositPreview, BorderLayout.WEST);

        JButton save   = Theme.primaryButton("Create");
        JButton cancel = Theme.ghostButton("Cancel");
        JPanel btns    = btnRow(save, cancel);

        save.addActionListener(e -> {
            try {
                int s = Integer.parseInt(singleQty.getText().trim());
                int d = Integer.parseInt(doubleQty.getText().trim());
                int t = Integer.parseInt(tripleQty.getText().trim());

                // FIX: use TypeOfRoom directly
                Map<TypeOfRoom, Integer> types = new EnumMap<>(TypeOfRoom.class);
                if (s > 0) types.put(TypeOfRoom.SINGLE, s);
                if (d > 0) types.put(TypeOfRoom.DOUBLE, d);
                if (t > 0) types.put(TypeOfRoom.TRIPLE, t);
                if (types.isEmpty()) { showError("Add at least one room type."); return; }

                Reservation res = new Reservation(
                    guestF.getText().trim(),
                    phoneF.getText().trim(),
                    types,
                    LocalDate.parse(checkInF.getText().trim()),
                    LocalDate.parse(checkOutF.getText().trim()),
                    Double.parseDouble(priceF.getText().trim())
                );
                hotel.addReservation(res);
                refreshTable();
                dlg.dispose();
                showSuccess("Reservation created!\nDeposit required: $" + res.getDepositAmount() +
                            "\n\nNext step: click 'Mark Deposit Paid' then 'Confirm'.");
            } catch (DateTimeParseException ex) {
                showError("Date format must be YYYY-MM-DD.");
            } catch (NumberFormatException ex) {
                showError("Price and room quantities must be numbers.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(Theme.CARD);
        south.add(bottom, BorderLayout.WEST);
        south.add(btns,   BorderLayout.EAST);

        dlg.add(form,  BorderLayout.CENTER);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Mark deposit paid ─────────────────────────────────────────────────────
    private void markDeposit() {
        Reservation r = getSelected(); if (r == null) return;
        if (r.isDepositPaid()) { showError("Deposit already marked as paid."); return; }

        String payId = JOptionPane.showInputDialog(this,
            "Enter payment ID for deposit ($" + String.format("%.2f", r.getDepositAmount()) + "):",
            "Mark Deposit Paid", JOptionPane.PLAIN_MESSAGE);
        if (payId != null && !payId.isBlank()) {
            try {
                r.markDepositPaid(payId.trim());
                refreshTable();
                showSuccess("Deposit marked as paid. You can now confirm the reservation.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ── Confirm ───────────────────────────────────────────────────────────────
    private void confirmReservation() {
        Reservation r = getSelected(); if (r == null) return;
        if (r.isConfirmed()) { showError("Reservation is already confirmed."); return; }
        try {
            r.confirm();
            refreshTable();
            showSuccess("Reservation confirmed!\nYou can now click 'Book Now' to create a booking.");
        } catch (IllegalStateException ex) {
            showError(ex.getMessage() + "\n\nMake sure you mark the deposit as paid first.");
        }
    }

    // ── Book Now ──────────────────────────────────────────────────────────────
    private void bookNow() {
        Reservation r = getSelected(); if (r == null) return;

        if (!r.isConfirmed()) {
            showError("Reservation must be confirmed before booking.\n\n" +
                      "Steps:\n1. Mark deposit as paid\n2. Click Confirm\n3. Then Book Now");
            return;
        }

        if (bookingsPanel == null) {
            showError("BookingsPanel reference not set.\nPlease navigate to Bookings tab manually.");
            return;
        }

        for (Booking b : bookingsPanel.getBookings()) {
            if (b.getReservationId().equals(r.getReservationId())) {
                showError("This reservation already has a booking.\nCheck the Bookings panel.");
                return;
            }
        }

        int ok = JOptionPane.showConfirmDialog(this,
            "Create a booking for " + r.getGuestName() + "?\n" +
            "Check-in: " + r.getCheckInDate() + "  →  Check-out: " + r.getCheckOutDate(),
            "Confirm Booking", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (ok == JOptionPane.YES_OPTION) {
            try {
                Booking b = new Booking(r) {};
                bookingsPanel.addBookingDirect(b);
                showSuccess("Booking created successfully!\n" +
                            "Booking ID: " + b.getBookingId() +
                            "\n\nSwitch to the Bookings panel to manage check-in.");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ── Remove ────────────────────────────────────────────────────────────────
    private void removeSelected() {
        Reservation r = getSelected(); if (r == null) return;
        int ok = JOptionPane.showConfirmDialog(this,
            "Remove reservation for " + r.getGuestName() + "?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            hotel.removeReservation(r);
            refreshTable();
            showSuccess("Reservation removed.");
        }
    }

    // ── View details ──────────────────────────────────────────────────────────
    private void viewDetails() {
        Reservation r = getSelected(); if (r == null) return;

        JDialog dlg = styledDialog("Reservation Details — " + r.getGuestName(), 440, 400);
        JPanel body = new JPanel();
        body.setBackground(Theme.SURFACE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 24, 16, 24));

        detailRow(body, "Reservation ID",  r.getReservationId());
        detailRow(body, "Guest Name",      r.getGuestName());
        detailRow(body, "Phone",           r.getPhone());
        // FIX: use getTypeOfRooms()
        detailRow(body, "Room Types",      formatRoomTypes(r.getTypeOfRooms()));
        detailRow(body, "Total Rooms",     String.valueOf(r.getTotalRooms()));
        detailRow(body, "Check-In",        r.getCheckInDate().toString());
        detailRow(body, "Check-Out",       r.getCheckOutDate().toString());
        detailRow(body, "Est. Price",      String.format("$%.2f", r.getEstimatedPrice()));
        detailRow(body, "Deposit (40%)",   String.format("$%.2f", r.getDepositAmount()));
        detailRow(body, "Remaining Bal.",  String.format("$%.2f", r.getRemainingBalance()));
        detailRow(body, "Deposit Paid",    r.isDepositPaid() ? "Yes — ID: " + r.getDepositPaymentId() : "No");
        detailRow(body, "Confirmed",       r.isConfirmed() ? "Yes ✓" : "No");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Theme.CARD);
        btns.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        JButton closeBtn = Theme.ghostButton("Close");
        closeBtn.addActionListener(e -> dlg.dispose());
        btns.add(closeBtn);

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null); sp.getViewport().setBackground(Theme.SURFACE);
        dlg.add(sp,   BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void detailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.PINK_DIM);
        lbl.setPreferredSize(new Dimension(140, 20));
        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BODY);
        val.setForeground(Theme.TEXT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(5));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Reservation getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a reservation first."); return null; }
        return hotel.getReservations().get(row);
    }

    // FIX: Map<TypeOfRoom, Integer> — no inner class
    private String formatRoomTypes(Map<TypeOfRoom, Integer> map) {
        return map.entrySet().stream()
            .map(e -> e.getValue() + "×" + e.getKey().getDisplayName())
            .reduce((a, b) -> a + ", " + b).orElse("-");
    }

    private JDialog styledDialog(String title, int w, int h) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, title, true);
        dlg.setSize(w, h); dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Theme.SURFACE);
        JLabel hdr = new JLabel("  " + title);
        hdr.setFont(Theme.FONT_HEADER); hdr.setForeground(Theme.PINK_SOFT);
        hdr.setBackground(Theme.CARD); hdr.setOpaque(true);
        hdr.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, Theme.PINK_DIM),
            new EmptyBorder(10, 16, 10, 16)
        ));
        dlg.add(hdr, BorderLayout.NORTH);
        return dlg;
    }

    private JPanel formPanel(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.SURFACE);
        p.setBorder(new EmptyBorder(16, 20, 10, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new java.awt.Insets(6, 4, 6, 4); gc.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            p.add(Theme.label(labels[i], Theme.FONT_BODY, Theme.TEXT_DIM), gc);
            gc.gridx = 1; gc.weightx = 1; p.add(fields[i], gc);
        }
        return p;
    }

    private JPanel btnRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        row.setBackground(Theme.CARD);
        row.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        for (JButton b : buttons) row.add(b);
        return row;
    }

    private void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Error",   JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
}