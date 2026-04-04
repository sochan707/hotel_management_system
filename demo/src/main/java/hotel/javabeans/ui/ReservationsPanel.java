package hotel.javabeans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Hotel;
import hotel.javabeans.Reservation;

public class ReservationsPanel extends JPanel {

    private final Hotel hotel;
    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
        "Reservation ID", "Guest Name", "Phone",
        "Check-In", "Check-Out", "Est. Price",
        "Deposit", "Paid", "Confirmed"
    };

    public ReservationsPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Theme.BG);
        h.setBorder(new EmptyBorder(24, 28, 14, 28));
        JLabel title = Theme.label("Reservations", Theme.FONT_TITLE, Theme.TEXT);
        JLabel sub   = Theme.label("View and manage guest reservations",
                                    Theme.FONT_SMALL, Theme.TEXT_DIM);
        JPanel text = new JPanel(); text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title); text.add(Box.createVerticalStrut(3)); text.add(sub);
        h.add(text, BorderLayout.WEST);
        return h;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(Theme.SURFACE);
                    boolean confirmed = "Yes".equals(getModel().getValueAt(row, 8));
                    c.setForeground(confirmed ? Theme.GREEN : Theme.TEXT);
                }
                return c;
            }
        };
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshTable();
        JScrollPane sp = Theme.scrollPane(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 14));
        bar.setBackground(Theme.BG);
        bar.setBorder(new EmptyBorder(0, 18, 0, 0));

        JButton addBtn      = Theme.primaryButton("+ New");
        JButton depositBtn  = Theme.ghostButton("💳  Mark Deposit Paid");
        depositBtn.setPreferredSize(new Dimension(170, 34));
        JButton confirmBtn  = Theme.ghostButton("✓  Confirm");
        JButton removeBtn   = Theme.dangerButton("✕  Remove");
        JButton viewBtn     = Theme.ghostButton("👁  Details");

        addBtn.addActionListener(e -> showAddDialog());
        depositBtn.addActionListener(e -> markDeposit());
        confirmBtn.addActionListener(e -> confirmReservation());
        removeBtn.addActionListener(e -> removeSelected());
        viewBtn.addActionListener(e -> viewDetails());

        bar.add(addBtn); bar.add(depositBtn); bar.add(confirmBtn);
        bar.add(removeBtn); bar.add(viewBtn);
        return bar;
    }

    private void refreshTable() {
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
                r.isDepositPaid()  ? "Yes" : "No",
                r.isConfirmed()    ? "Yes" : "No"
            });
        }
    }

    // ── Add dialog ───────────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = styledDialog("New Reservation", 460, 430);

        JTextField guestField  = Theme.textField();
        JTextField phoneField  = Theme.textField();
        JTextField checkInFld  = Theme.textField(); checkInFld.setText("YYYY-MM-DD");
        JTextField checkOutFld = Theme.textField(); checkOutFld.setText("YYYY-MM-DD");
        JTextField priceFld    = Theme.textField();

        // Room type quantities
        JTextField singleQty = Theme.textField(); singleQty.setText("0");
        JTextField doubleQty = Theme.textField(); doubleQty.setText("0");
        JTextField tripleQty = Theme.textField(); tripleQty.setText("0");

        JPanel roomPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        roomPanel.setOpaque(false);
        roomPanel.add(Theme.label("SINGLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(singleQty);
        roomPanel.add(Theme.label("DOUBLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(doubleQty);
        roomPanel.add(Theme.label("TRIPLE rooms:", Theme.FONT_SMALL, Theme.TEXT_DIM)); roomPanel.add(tripleQty);

        JPanel form = formPanel(
            new String[]{"Guest Name", "Phone", "Check-In Date", "Check-Out Date",
                         "Estimated Price ($)", "Room Quantities"},
            new JComponent[]{guestField, phoneField, checkInFld, checkOutFld, priceFld, roomPanel}
        );

        JButton save   = Theme.primaryButton("Save");
        JButton cancel = Theme.ghostButton("Cancel");

        save.addActionListener(e -> {
            try {
                LocalDate checkIn  = LocalDate.parse(checkInFld.getText().trim());
                LocalDate checkOut = LocalDate.parse(checkOutFld.getText().trim());
                double price = Double.parseDouble(priceFld.getText().trim());

                Map<Reservation.RoomType, Integer> types = new EnumMap<>(Reservation.RoomType.class);
                int s = Integer.parseInt(singleQty.getText().trim());
                int d = Integer.parseInt(doubleQty.getText().trim());
                int t = Integer.parseInt(tripleQty.getText().trim());
                if (s > 0) types.put(Reservation.RoomType.SINGLE, s);
                if (d > 0) types.put(Reservation.RoomType.DOUBLE, d);
                if (t > 0) types.put(Reservation.RoomType.TRIPLE, t);
                if (types.isEmpty()) { showError("Add at least one room type."); return; }

                Reservation res = new Reservation(
                    guestField.getText().trim(),
                    phoneField.getText().trim(),
                    types, checkIn, checkOut, price
                );
                hotel.addReservation(res);
                refreshTable();
                dlg.dispose();
                showSuccess("Reservation created. Deposit required: $" + res.getDepositAmount());
            } catch (DateTimeParseException ex) {
                showError("Date format must be YYYY-MM-DD.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(form,             BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void markDeposit() {
        Reservation r = getSelected(); if (r == null) return;
        if (r.isDepositPaid()) { showError("Deposit already marked as paid."); return; }

        String payId = JOptionPane.showInputDialog(this,
            "Enter payment ID for deposit ($" + r.getDepositAmount() + "):",
            "Mark Deposit Paid", JOptionPane.PLAIN_MESSAGE);
        if (payId != null && !payId.isBlank()) {
            try {
                r.markDepositPaid(payId.trim());
                refreshTable();
                showSuccess("Deposit marked as paid.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void confirmReservation() {
        Reservation r = getSelected(); if (r == null) return;
        try {
            r.confirm();
            refreshTable();
            showSuccess("Reservation confirmed.");
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

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

    private void viewDetails() {
        Reservation r = getSelected(); if (r == null) return;
        JTextArea area = new JTextArea(r.toString());
        area.setEditable(false);
        area.setBackground(Theme.SURFACE);
        area.setForeground(Theme.TEXT);
        area.setFont(Theme.FONT_BODY);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                                      "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Reservation getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a reservation."); return null; }
        // Match by full ID from hotel list (we truncated the display)
        return hotel.getReservations().get(row);
    }

    private JDialog styledDialog(String title, int w, int h) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Theme.SURFACE);
        return dlg;
    }

    private JPanel formPanel(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.SURFACE);
        p.setBorder(new EmptyBorder(20, 24, 10, 24));
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
        row.setBackground(Theme.SURFACE);
        row.setBorder(new EmptyBorder(0, 0, 0, 16));
        for (JButton b : buttons) row.add(b);
        return row;
    }

    private void showError(String msg)   {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
}