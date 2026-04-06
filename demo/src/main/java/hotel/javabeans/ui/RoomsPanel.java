package hotel.javabeans.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Hotel;
import hotel.javabeans.Room;
import hotel.javabeans.TypeOfRoom;

public class RoomsPanel extends JPanel {

    private final Hotel hotel;
    private final boolean isManager;
    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {"Room No.", "Type", "Price/Night", "Status"};

    public RoomsPanel(Hotel hotel, String role) {
        this.hotel     = hotel;
        this.isManager = "Manager".equalsIgnoreCase(role);
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Theme.BG);
        h.setBorder(new EmptyBorder(24, 28, 14, 28));

        JLabel title = Theme.label("Room Management", Theme.FONT_TITLE, Theme.TEXT);
        JLabel sub   = Theme.label("Manage all hotel rooms and their current status",
                                    Theme.FONT_SMALL, Theme.TEXT_DIM);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(sub);

        h.add(text, BorderLayout.WEST);
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
                    String status = (String) getModel().getValueAt(row, 3);
                    c.setBackground(Theme.SURFACE);
                    c.setForeground(statusColor(status));
                }
                return c;
            }
        };
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Centre-align all columns
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < COLUMNS.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(centre);

        refreshTable();
        JScrollPane sp = Theme.scrollPane(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        return sp;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 14));
        bar.setBackground(Theme.BG);
        bar.setBorder(new EmptyBorder(0, 18, 0, 0));

        JButton addBtn    = Theme.primaryButton("+ Add Room");
        JButton editBtn   = Theme.ghostButton("✏  Edit");
        JButton removeBtn = Theme.dangerButton("✕  Remove");
        JButton statusBtn = Theme.ghostButton("⟳  Change Status");
        statusBtn.setPreferredSize(new Dimension(150, 34));

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        removeBtn.addActionListener(e -> removeSelected());
        statusBtn.addActionListener(e -> changeStatus());

        // Change Status is available to all roles (Receptionist needs this for check-in workflow)
        bar.add(statusBtn);
        if (isManager) {
            bar.add(addBtn);
            bar.add(editBtn);
            bar.add(removeBtn);
        }
        return bar;
    }

    // ── Table refresh ─────────────────────────────────────────────────────────
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Room room : hotel.getRooms()) {
            tableModel.addRow(new Object[]{
                room.getRoomNumber(),
                room.getRoomType().getDisplayName(),
                String.format("$%.2f", room.getPrice()),
                room.getStatus()
            });
        }
    }

    // ── Add dialog ───────────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = styledDialog("Add New Room", 400, 300);

        JTextField numField   = Theme.textField();
        JComboBox<String> typeCb = Theme.comboBox("SINGLE", "DOUBLE", "TRIPLE");
        JTextField priceField = Theme.textField();

        JPanel form = formPanel(
            new String[]{"Room Number", "Room Type", "Price / Night ($)"},
            new JComponent[]{numField, typeCb, priceField}
        );

        JButton saveBtn   = Theme.primaryButton("Save");
        JButton cancelBtn = Theme.ghostButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String num   = numField.getText().trim();
                TypeOfRoom type = TypeOfRoom.valueOf((String) typeCb.getSelectedItem());
                double price = Double.parseDouble(priceField.getText().trim());

                hotel.addRoom(new Room(num, type, price));
                refreshTable();
                dlg.dispose();
                showSuccess("Room " + num + " added.");
            } catch (Hotel.HotelException ex) {
                showError(ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Price must be a valid number.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dlg.dispose());

        JPanel btnRow = btnRow(saveBtn, cancelBtn);
        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Edit dialog ──────────────────────────────────────────────────────────
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a room to edit."); return; }

        String roomNumber = (String) tableModel.getValueAt(row, 0);
        Room   room       = hotel.findRoomByNumber(roomNumber);
        if (room == null) { showError("Room not found."); return; }

        JDialog dlg = styledDialog("Edit Room — " + roomNumber, 400, 260);

        JComboBox<String> typeCb = Theme.comboBox("SINGLE", "DOUBLE", "TRIPLE");
        typeCb.setSelectedItem(room.getRoomType().name());
        JTextField priceField = Theme.textField();
        priceField.setText(String.valueOf(room.getPrice()));

        JPanel form = formPanel(
            new String[]{"Room Type", "Price / Night ($)"},
            new JComponent[]{typeCb, priceField}
        );

        JButton saveBtn   = Theme.primaryButton("Update");
        JButton cancelBtn = Theme.ghostButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                room.setRoomType(TypeOfRoom.valueOf((String) typeCb.getSelectedItem()));
                room.setPrice(Double.parseDouble(priceField.getText().trim()));
                refreshTable();
                dlg.dispose();
                showSuccess("Room " + roomNumber + " updated.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dlg.dispose());

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow(saveBtn, cancelBtn), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Remove ───────────────────────────────────────────────────────────────
    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a room to remove."); return; }

        String roomNumber = (String) tableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(
            this, "Remove room " + roomNumber + "?", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (ok == JOptionPane.YES_OPTION) {
            try {
                Room room = hotel.getRoom(roomNumber);
                hotel.removeRoom(room);
                refreshTable();
                showSuccess("Room " + roomNumber + " removed.");
            } catch (Hotel.HotelException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ── Change status ────────────────────────────────────────────────────────
    private void changeStatus() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a room."); return; }

        String roomNumber = (String) tableModel.getValueAt(row, 0);
        Room   room       = hotel.findRoomByNumber(roomNumber);
        if (room == null) return;

        String[] options = {Room.AVAILABLE, Room.BOOKED, Room.OCCUPIED};
        String choice = (String) JOptionPane.showInputDialog(
            this, "Select new status for room " + roomNumber,
            "Change Status", JOptionPane.PLAIN_MESSAGE,
            null, options, room.getStatus());

        if (choice != null) {
            try {
                room.setStatus(choice);
                refreshTable();
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Color statusColor(String status) {
        if (Room.AVAILABLE.equals(status)) return Theme.GREEN;
        if (Room.BOOKED.equals(status))    return Theme.YELLOW;
        if (Room.OCCUPIED.equals(status))  return Theme.RED;
        return Theme.TEXT;
    }

    private JDialog styledDialog(String title, int w, int h) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(0, 0));
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
            JLabel lbl = Theme.label(labels[i], Theme.FONT_BODY, Theme.TEXT_DIM);
            p.add(lbl, gc);
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

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}