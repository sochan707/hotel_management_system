package hotel.javabeans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Hotel;
import hotel.javabeans.Person.Manager;
import hotel.javabeans.Person.Receptionist;
import hotel.javabeans.Person.Staff;

public class StaffPanel extends JPanel {

    private final Hotel hotel;
    private final boolean isManager;
    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {"ID", "First Name", "Last Name", "Gender", "Phone", "Position", "Active"};

    public StaffPanel(Hotel hotel, String role) {
        this.hotel     = hotel;
        this.isManager = "Manager".equalsIgnoreCase(role);
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
        JLabel title = Theme.label("Staff Management", Theme.FONT_TITLE, Theme.TEXT);
        JLabel sub   = Theme.label("Manage hotel staff — managers and receptionists",
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
                    String pos = (String) getModel().getValueAt(row, 5);
                    c.setForeground("Manager".equals(pos) ? Theme.GOLD : Theme.TEXT);
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

        JButton addBtn    = Theme.primaryButton("+ Add Staff");
        JButton removeBtn = Theme.dangerButton("✕  Remove");
        JButton viewBtn   = Theme.ghostButton("👁  Details");

        addBtn.addActionListener(e -> showAddDialog());
        removeBtn.addActionListener(e -> removeSelected());
        viewBtn.addActionListener(e -> viewSelected());

        bar.add(viewBtn);
        if (isManager) {
            bar.add(addBtn);
            bar.add(removeBtn);
        }
        return bar;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Staff s : hotel.getStaffMembers()) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getFirstName(), s.getLastName(),
                s.getGender(), s.getPhone(), s.getPosition(),
                s.isActive() ? "Yes" : "No"
            });
        }
    }

    private void showAddDialog() {
        JDialog dlg = styledDialog("Add Staff Member", 420, 380);

        JTextField idField    = Theme.textField();
        JTextField fnField    = Theme.textField();
        JTextField lnField    = Theme.textField();
        JComboBox<String> genderCb   = Theme.comboBox("Male", "Female");
        JTextField phoneField = Theme.textField();
        JComboBox<String> positionCb = Theme.comboBox("Manager", "Receptionist");

        JPanel form = formPanel(
            new String[]{"Staff ID", "First Name", "Last Name", "Gender", "Phone", "Position"},
            new JComponent[]{idField, fnField, lnField, genderCb, phoneField, positionCb}
        );

        JButton save   = Theme.primaryButton("Save");
        JButton cancel = Theme.ghostButton("Cancel");

        save.addActionListener(e -> {
            try {
                String id       = idField.getText().trim();
                String fn       = fnField.getText().trim();
                String ln       = lnField.getText().trim();
                String gender   = (String) genderCb.getSelectedItem();
                String phone    = phoneField.getText().trim();
                String position = (String) positionCb.getSelectedItem();

                Staff staff = "Manager".equals(position)
                    ? new Manager(id, fn, ln, gender, phone)
                    : new Receptionist(id, fn, ln, gender, phone);

                hotel.addStaff(staff);
                refreshTable();
                dlg.dispose();
                showSuccess(position + " added successfully.");
            } catch (Hotel.HotelException | IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.add(form,             BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a staff member to remove."); return; }

        String id = (String) tableModel.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
            "Remove staff member " + id + "?", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (ok == JOptionPane.YES_OPTION) {
            try {
                hotel.removeStaff(hotel.getStaff(id));
                refreshTable();
                showSuccess("Staff member removed.");
            } catch (Hotel.HotelException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void viewSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a staff member."); return; }
        String id    = (String) tableModel.getValueAt(row, 0);
        Staff  staff = hotel.findStaffById(id);
        if (staff == null) return;

        String perms = buildPermissions(staff);
        JTextArea area = new JTextArea(staff.toString() + "\n\nPermissions:\n" + perms);
        area.setEditable(false);
        area.setBackground(Theme.SURFACE);
        area.setForeground(Theme.TEXT);
        area.setFont(Theme.FONT_BODY);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Staff Details",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildPermissions(Staff staff) {
        String[] actions = {"checkin","checkout","bookroom","cancelbooking",
                            "viewbooking","viewguest","viewroom","manageinvoice","managestaff"};
        StringBuilder sb = new StringBuilder();
        for (String a : actions)
            sb.append("  ").append(staff.can(a) ? "✓" : "✗").append("  ").append(a).append("\n");
        return sb.toString();
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