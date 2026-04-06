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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
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

public class BookingsPanel extends JPanel {

    private final Hotel hotel;
    private final List<Booking> bookings = new ArrayList<>();

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalLabel;

    private static final String[] COLUMNS = {
        "Booking ID", "Guest Name", "Phone",
        "Room Types", "Check-In", "Check-Out", "Nights", "Status"
    };

    public BookingsPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG);
        outer.setBorder(new EmptyBorder(24, 28, 8, 28));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(Theme.sectionTitle("Booking Management"));
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(Theme.sectionSub("Walk-in and reservation-based guest bookings"));
        outer.add(titleBlock, BorderLayout.WEST);

        totalLabel = new JLabel("0 Bookings") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(Theme.PINK, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Theme.alpha(Theme.PINK, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g); g2.dispose();
            }
        };
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        totalLabel.setForeground(Theme.PINK_SOFT);
        totalLabel.setOpaque(false);
        totalLabel.setBorder(new EmptyBorder(4, 12, 4, 12));
        outer.add(totalLabel, BorderLayout.EAST);
        return outer;
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
                    String status = (String) getModel().getValueAt(row, 7);
                    c.setForeground(statusColour(status));
                }
                return c;
            }
        };
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(7).setCellRenderer(centre);

        int[] widths = {160, 130, 110, 120, 100, 100, 55, 95};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        refreshTable();

        JScrollPane sp = Theme.scrollPane(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        return sp;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        bar.setBackground(Theme.BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Theme.BORDER),
            new EmptyBorder(4, 18, 4, 0)
        ));

        JButton fromResBtn = Theme.primaryButton("📋  From Reservation");
        fromResBtn.setPreferredSize(new Dimension(170, 34));
        fromResBtn.addActionListener(e -> bookFromReservation());

        JButton walkInBtn = Theme.primaryButton("🚶  Walk-in");
        walkInBtn.setPreferredSize(new Dimension(110, 34));
        walkInBtn.addActionListener(e -> showWalkInDialog());

        JButton checkInBtn  = Theme.ghostButton("✓  Check In");
        checkInBtn.addActionListener(e -> doStatusChange("CHECKIN"));

        JButton checkOutBtn = Theme.ghostButton("⬡  Check Out");
        checkOutBtn.addActionListener(e -> doStatusChange("CHECKOUT"));

        JButton cancelBtn   = Theme.dangerButton("✕  Cancel");
        cancelBtn.addActionListener(e -> doStatusChange("CANCEL"));

        JButton assignBtn   = Theme.ghostButton("🔑  Assign Room");
        assignBtn.setPreferredSize(new Dimension(130, 34));
        assignBtn.addActionListener(e -> assignRoomDialog());

        JButton viewBtn     = Theme.ghostButton("👁  Details");
        viewBtn.addActionListener(e -> viewDetails());

        bar.add(fromResBtn);
        bar.add(walkInBtn);
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28)); setForeground(Theme.BORDER);
        }});
        bar.add(checkInBtn);
        bar.add(checkOutBtn);
        bar.add(cancelBtn);
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28)); setForeground(Theme.BORDER);
        }});
        bar.add(assignBtn);
        bar.add(viewBtn);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        legend.add(legendDot(Theme.BLUE,     "Upcoming"));
        legend.add(legendDot(Theme.GREEN,    "Checked-in"));
        legend.add(legendDot(Theme.TEXT_DIM, "Checked-out"));
        legend.add(legendDot(Theme.RED,      "Cancelled"));

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

    private JLabel legendDot(Color c, String label) {
        JLabel l = new JLabel("●  " + label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 10));
        l.setForeground(c);
        return l;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            // FIX: use getTypeOfRooms() which returns Map<TypeOfRoom, Integer>
            String rt = b.getTypeOfRooms().entrySet().stream()
                .map(e -> e.getValue() + "×" + e.getKey().getDisplayName())
                .reduce((a, x) -> a + ", " + x).orElse("-");
            tableModel.addRow(new Object[]{
                b.getBookingId().substring(0, 8) + "…",
                b.getGuestName(),
                b.getPhone(),
                rt,
                b.getCheckInDate(),
                b.getCheckOutDate(),
                b.getNights(),
                b.getBookingStatus().name()
            });
        }
        totalLabel.setText(bookings.size() + " Booking" + (bookings.size() == 1 ? "" : "s"));
    }

    private Color statusColour(String s) {
        return switch (s) {
            case "UPCOMING"   -> Theme.BLUE;
            case "CHECKIN"    -> Theme.GREEN;
            case "CHECKOUT"   -> Theme.TEXT_DIM;
            case "CANCELLED"  -> Theme.RED;
            default           -> Theme.TEXT;
        };
    }

    // ── Book from Reservation ─────────────────────────────────────────────────
    private void bookFromReservation() {
        List<Reservation> confirmed = new ArrayList<>();
        Set<String> bookedResIds = new HashSet<>();
        for (Booking b : bookings) bookedResIds.add(b.getReservationId());

        for (Reservation r : hotel.getReservations()) {
            if (r.isConfirmed() && !bookedResIds.contains(r.getReservationId()))
                confirmed.add(r);
        }

        if (confirmed.isEmpty()) {
            showError("No confirmed reservations available.\n\nTo book:\n" +
                      "1. Go to Reservations panel\n" +
                      "2. Mark deposit as paid\n" +
                      "3. Click Confirm");
            return;
        }

        String[] choices = confirmed.stream()
            .map(r -> r.getGuestName() + "  |  " + r.getCheckInDate() + " → " + r.getCheckOutDate()
                      + "  |  " + formatRoomTypes(r.getTypeOfRooms()))
            .toArray(String[]::new);

        JList<String> list = new JList<>(choices);
        list.setBackground(Theme.CARD);
        list.setForeground(Theme.TEXT);
        list.setFont(Theme.FONT_BODY);
        list.setSelectionBackground(Theme.alpha(Theme.PINK, 80));
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(32);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(480, 200));
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sp.getViewport().setBackground(Theme.CARD);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Theme.SURFACE);
        JLabel prompt = new JLabel("Select a confirmed reservation to create a booking:");
        prompt.setFont(Theme.FONT_BODY);
        prompt.setForeground(Theme.TEXT_DIM);
        content.add(prompt, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);

        int ok = JOptionPane.showConfirmDialog(this, content,
            "Book from Reservation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok == JOptionPane.OK_OPTION && list.getSelectedIndex() >= 0) {
            Reservation chosen = confirmed.get(list.getSelectedIndex());
            try {
                Booking b = new Booking(chosen) {};
                bookings.add(b);
                refreshTable();
                showSuccess("Booking created for " + chosen.getGuestName() +
                            "\nBooking ID: " + b.getBookingId());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ── Walk-in booking dialog ────────────────────────────────────────────────
    private void showWalkInDialog() {
        JDialog dlg = styledDialog("Walk-in Booking", 480, 460);

        JTextField nameF     = Theme.textField();
        JTextField phoneF    = Theme.textField();
        JTextField checkInF  = Theme.textField(); checkInF.setText(LocalDate.now().toString());
        JTextField checkOutF = Theme.textField(); checkOutF.setText(LocalDate.now().plusDays(1).toString());
        JTextField specialF  = Theme.textField();

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
                         "Check-Out (YYYY-MM-DD)", "Room Quantities", "Special Requests"},
            new JComponent[]{nameF, phoneF, checkInF, checkOutF, roomPanel, specialF}
        );

        JButton save   = Theme.primaryButton("Create Booking");
        save.setPreferredSize(new Dimension(150, 34));
        JButton cancel = Theme.ghostButton("Cancel");

        save.addActionListener(e -> {
            try {
                int s = Integer.parseInt(singleQty.getText().trim());
                int d = Integer.parseInt(doubleQty.getText().trim());
                int t = Integer.parseInt(tripleQty.getText().trim());

                // FIX: use TypeOfRoom directly (no inner class)
                Map<TypeOfRoom, Integer> types = new EnumMap<>(TypeOfRoom.class);
                if (s > 0) types.put(TypeOfRoom.SINGLE, s);
                if (d > 0) types.put(TypeOfRoom.DOUBLE, d);
                if (t > 0) types.put(TypeOfRoom.TRIPLE, t);
                if (types.isEmpty()) { showError("Add at least one room type."); return; }

                String resId = "WALKIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                Booking b = new Booking(
                    resId,
                    nameF.getText().trim(),
                    phoneF.getText().trim(),
                    types,
                    LocalDate.parse(checkInF.getText().trim()),
                    LocalDate.parse(checkOutF.getText().trim()),
                    specialF.getText().trim()
                );
                bookings.add(b);
                refreshTable();
                dlg.dispose();
                showSuccess("Walk-in booking created!\nBooking ID: " + b.getBookingId());
            } catch (DateTimeParseException ex) {
                showError("Date format must be YYYY-MM-DD.");
            } catch (NumberFormatException ex) {
                showError("Room quantities must be numbers.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Status transitions ────────────────────────────────────────────────────
    private void doStatusChange(String action) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a booking first."); return; }
        Booking b = bookings.get(row);
        try {
            switch (action) {
                case "CHECKIN"  -> b.checkIn();
                case "CHECKOUT" -> b.checkOut();
                case "CANCEL"   -> {
                    int ok = JOptionPane.showConfirmDialog(this,
                        "Cancel booking for " + b.getGuestName() + "?",
                        "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ok != JOptionPane.YES_OPTION) return;
                    b.cancel();
                }
            }
            refreshTable();
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    // ── Assign room dialog ────────────────────────────────────────────────────
    private void assignRoomDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a booking first."); return; }
        Booking b = bookings.get(row);

        if (b.getBookingStatus() != Booking.BookingStatus.UPCOMING) {
            showError("Can only assign rooms to UPCOMING bookings.");
            return;
        }

        JDialog dlg = styledDialog("Assign Room — " + b.getGuestName(), 380, 280);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.SURFACE);
        form.setBorder(new EmptyBorder(16, 20, 10, 20));

        // FIX: use TypeOfRoom.name() directly
        String[] typeNames = b.getTypeOfRooms().keySet().stream()
            .map(TypeOfRoom::name).toArray(String[]::new);
        JComboBox<String> typeCb = Theme.comboBox(typeNames);

        JTextField roomNumF = Theme.textField();
        roomNumF.setToolTipText("Enter an integer room number, e.g. 101");

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4); gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx=0; gc.gridy=0; gc.weightx=0;
        form.add(Theme.label("Room Type:", Theme.FONT_BODY, Theme.TEXT_DIM), gc);
        gc.gridx=1; gc.weightx=1; form.add(typeCb, gc);

        gc.gridx=0; gc.gridy=1; gc.weightx=0;
        form.add(Theme.label("Room Number:", Theme.FONT_BODY, Theme.TEXT_DIM), gc);
        gc.gridx=1; gc.weightx=1; form.add(roomNumF, gc);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        JLabel assigned = Theme.label("Assigned: " + formatAssignments(b), Theme.FONT_SMALL, Theme.TEXT_DIM);
        form.add(assigned, gc);

        JButton assignBtn = Theme.primaryButton("Assign");
        JButton removeBtn = Theme.dangerButton("Remove");
        JButton closeBtn  = Theme.ghostButton("Close");

        assignBtn.addActionListener(e -> {
            try {
                // FIX: use TypeOfRoom.valueOf directly
                TypeOfRoom type = TypeOfRoom.valueOf((String) typeCb.getSelectedItem());
                int num = Integer.parseInt(roomNumF.getText().trim());
                b.assignRoom(type, num);
                assigned.setText("Assigned: " + formatAssignments(b));
                roomNumF.setText("");
                showSuccess("Room " + num + " assigned.");
            } catch (NumberFormatException ex) {
                showError("Room number must be an integer.");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        removeBtn.addActionListener(e -> {
            try {
                TypeOfRoom type = TypeOfRoom.valueOf((String) typeCb.getSelectedItem());
                int num = Integer.parseInt(roomNumF.getText().trim());
                b.removeAssignedRoom(type, num);
                assigned.setText("Assigned: " + formatAssignments(b));
                showSuccess("Room " + num + " removed.");
            } catch (NumberFormatException ex) {
                showError("Room number must be an integer.");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        closeBtn.addActionListener(e -> dlg.dispose());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        btns.setBackground(Theme.CARD);
        btns.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        btns.add(closeBtn); btns.add(removeBtn); btns.add(assignBtn);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── View details ──────────────────────────────────────────────────────────
    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a booking."); return; }
        Booking b = bookings.get(row);

        JDialog dlg = styledDialog("Booking Details", 460, 420);
        JPanel body = new JPanel();
        body.setBackground(Theme.SURFACE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 24, 16, 24));

        detailRow(body, "Booking ID",       b.getBookingId());
        detailRow(body, "Reservation ID",   b.getReservationId());
        detailRow(body, "Guest Name",       b.getGuestName());
        detailRow(body, "Phone",            b.getPhone());
        // FIX: use getTypeOfRooms()
        detailRow(body, "Room Types",       formatRoomTypesBooking(b.getTypeOfRooms()));
        detailRow(body, "Total Rooms",      String.valueOf(b.getTotalRooms()));
        detailRow(body, "Check-In",         b.getCheckInDate().toString());
        detailRow(body, "Check-Out",        b.getCheckOutDate().toString());
        detailRow(body, "Nights",           String.valueOf(b.getNights()));
        detailRow(body, "Status",           b.getBookingStatus().name());
        detailRow(body, "Created",          b.getBookingCreationDate().toString());
        detailRow(body, "Room Assignments", formatAssignments(b));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Theme.CARD);
        btns.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        JButton closeBtn = Theme.ghostButton("Close");
        closeBtn.addActionListener(e -> dlg.dispose());
        btns.add(closeBtn);

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.SURFACE);

        dlg.add(sp, BorderLayout.CENTER);
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

    // ── Format helpers ────────────────────────────────────────────────────────

    // FIX: both Reservation and Booking use Map<TypeOfRoom, Integer>
    private String formatRoomTypes(Map<TypeOfRoom, Integer> map) {
        return map.entrySet().stream()
            .map(e -> e.getValue() + "×" + e.getKey().getDisplayName())
            .reduce((a, b) -> a + ", " + b).orElse("-");
    }

    private String formatRoomTypesBooking(Map<TypeOfRoom, Integer> map) {
        return map.entrySet().stream()
            .map(e -> e.getValue() + "×" + e.getKey().getDisplayName())
            .reduce((a, b) -> a + ", " + b).orElse("-");
    }

    private String formatAssignments(Booking b) {
        // FIX: getActualRoomAssignments() returns Map<TypeOfRoom, List<Integer>>
        Map<TypeOfRoom, List<Integer>> map = b.getActualRoomAssignments();
        if (map.isEmpty()) return "None";
        return map.entrySet().stream()
            .map(e -> e.getKey().getDisplayName() + ": " + e.getValue())
            .reduce((a, x) -> a + "  |  " + x).orElse("None");
    }

    // ── Dialog / form helpers ─────────────────────────────────────────────────
    private JDialog styledDialog(String title, int w, int h) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Theme.SURFACE);

        JLabel hdr = new JLabel("  " + title);
        hdr.setFont(Theme.FONT_HEADER);
        hdr.setForeground(Theme.PINK_SOFT);
        hdr.setBackground(Theme.CARD);
        hdr.setOpaque(true);
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
        gc.insets = new Insets(6, 4, 6, 4);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            p.add(Theme.label(labels[i], Theme.FONT_BODY, Theme.TEXT_DIM), gc);
            gc.gridx = 1; gc.weightx = 1;
            p.add(fields[i], gc);
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

    public List<Booking> getBookings() { return Collections.unmodifiableList(bookings); }

    public void addBookingDirect(Booking booking) {
        bookings.add(booking);
        refreshTable();
    }
}