package hotel.javabeans.ui;
import java.awt.BorderLayout;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import hotel.javabeans.Hotel;
import hotel.javabeans.Person.Guest;

public class GuestsPanel extends JPanel {

    private final Hotel hotel;
    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {"ID", "First Name", "Last Name", "Gender", "Phone"};

    public GuestsPanel(Hotel hotel) {
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
        JLabel title = Theme.label("Guest Management", Theme.FONT_TITLE, Theme.TEXT);
        JLabel sub   = Theme.label("Register and manage hotel guests", Theme.FONT_SMALL, Theme.TEXT_DIM);
        JPanel text  = new JPanel(); text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title); text.add(Box.createVerticalStrut(3)); text.add(sub);
        h.add(text, BorderLayout.WEST);
        return h;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
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

        JButton addBtn    = Theme.primaryButton("+ Add Guest");
        JButton editBtn   = Theme.ghostButton("✏  Edit");
        JButton removeBtn = Theme.dangerButton("✕  Remove");
        JButton viewBtn   = Theme.ghostButton("👁  View");

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        removeBtn.addActionListener(e -> removeSelected());
        viewBtn.addActionListener(e -> viewSelected());

        bar.add(addBtn); bar.add(editBtn); bar.add(removeBtn); bar.add(viewBtn);
        return bar;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Guest g : hotel.getGuests()) {
            tableModel.addRow(new Object[]{
                g.getId(), g.getFirstName(), g.getLastName(), g.getGender(), g.getPhone()
            });
        }
    }

    private void showAddDialog() {
        JDialog dlg = styledDialog("Add Guest", 420, 360);

        JTextField idField    = Theme.textField();
        JTextField fnField    = Theme.textField();
        JTextField lnField    = Theme.textField();
        JComboBox<String> genderCb = Theme.comboBox("Male", "Female");
        JTextField phoneField = Theme.textField();

        JPanel form = formPanel(
            new String[]{"Guest ID", "First Name", "Last Name", "Gender", "Phone"},
            new JComponent[]{idField, fnField, lnField, genderCb, phoneField}
        );

        JButton save   = Theme.primaryButton("Save");
        JButton cancel = Theme.ghostButton("Cancel");

        save.addActionListener(e -> {
            try {
                Guest g = new Guest(
                    idField.getText().trim(),
                    fnField.getText().trim(),
                    lnField.getText().trim(),
                    (String) genderCb.getSelectedItem(),
                    phoneField.getText().trim()
                );
                hotel.addGuest(g);
                refreshTable();
                dlg.dispose();
                showSuccess("Guest added successfully.");
            } catch (Hotel.HotelException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(form,          BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a guest to edit."); return; }

        String id    = (String) tableModel.getValueAt(row, 0);
        Guest  guest = hotel.findGuestById(id);
        if (guest == null) { showError("Guest not found."); return; }

        JDialog dlg = styledDialog("Edit Guest — " + id, 420, 320);

        JTextField fnField    = Theme.textField(); fnField.setText(guest.getFirstName());
        JTextField lnField    = Theme.textField(); lnField.setText(guest.getLastName());
        JComboBox<String> genderCb = Theme.comboBox("Male", "Female");
        genderCb.setSelectedItem(guest.getGender());
        JTextField phoneField = Theme.textField(); phoneField.setText(guest.getPhone());

        JPanel form = formPanel(
            new String[]{"First Name", "Last Name", "Gender", "Phone"},
            new JComponent[]{fnField, lnField, genderCb, phoneField}
        );

        JButton save   = Theme.primaryButton("Update");
        JButton cancel = Theme.ghostButton("Cancel");

        save.addActionListener(e -> {
            try {
                // Re-create (setters are protected — create new and replace)
                hotel.removeGuest(guest);
                Guest updated = new Guest(id,
                    fnField.getText().trim(), lnField.getText().trim(),
                    (String) genderCb.getSelectedItem(), phoneField.getText().trim());
                hotel.addGuest(updated);
                refreshTable();
                dlg.dispose();
                showSuccess("Guest updated.");
            } catch (Hotel.HotelException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(form,          BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a guest to remove."); return; }

        String id = (String) tableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Remove guest " + id + "?",
                 "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (ok == JOptionPane.YES_OPTION) {
            try {
                hotel.removeGuest(hotel.getGuest(id));
                refreshTable();
                showSuccess("Guest removed.");
            } catch (Hotel.HotelException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a guest."); return; }
        String id    = (String) tableModel.getValueAt(row, 0);
        Guest  guest = hotel.findGuestById(id);
        if (guest == null) return;

        JOptionPane.showMessageDialog(this, guest.toString(), "Guest Details",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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