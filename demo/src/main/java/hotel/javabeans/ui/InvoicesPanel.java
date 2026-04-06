package hotel.javabeans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Hotel;
import hotel.javabeans.Invoice;
import hotel.javabeans.payment.Payment;

/**
 * InvoicesPanel — lists all invoices generated on checkout.
 *
 * Workflow: Booking (CHECKOUT) → Invoice created automatically → paid here
 *           via PaymentDialog → Payment recorded in PaymentsPanel.
 *
 * Lifecycle: Reservation → Booking → Invoice → Payment
 */
public class InvoicesPanel extends JPanel {

    private final Hotel       hotel;
    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            totalLabel;
    private JLabel            unpaidLabel;

    // Reference wired by DashboardFrame so "Pay" can refresh PaymentsPanel
    private PaymentsPanel paymentsPanel;

    private static final String[] COLUMNS = {
        "Invoice ID", "Booking ID", "Nights", "Rooms",
        "Room Charges", "Tax", "Total", "Status"
    };

    public InvoicesPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        refreshTable();
    }

    // ── Wire-up ───────────────────────────────────────────────────────────────
    public void setPaymentsPanel(PaymentsPanel pp) { this.paymentsPanel = pp; }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.BORDER);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 20, 12, 20));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Invoices");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Generated automatically on guest checkout");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_DIM);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);
        header.add(buildToolbar(), BorderLayout.EAST);
        return header;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setOpaque(false);

        JButton payBtn     = Theme.primaryButton("💳  Pay Invoice");
        JButton viewBtn    = Theme.ghostButton("👁  Details");
        JButton refreshBtn = Theme.ghostButton("⟳  Refresh");

        payBtn    .addActionListener(e -> payInvoice());
        viewBtn   .addActionListener(e -> viewDetails());
        refreshBtn.addActionListener(e -> refreshTable());

        bar.add(viewBtn);
        bar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28));
            setForeground(Theme.BORDER);
        }});
        bar.add(payBtn);
        bar.add(refreshBtn);
        return bar;
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
                    c.setBackground(row % 2 == 0 ? Theme.SURFACE : Theme.alpha(Theme.CARD, 180));
                }
                return c;
            }
        };

        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Theme.SURFACE);
        table.setForeground(Theme.TEXT);
        table.setSelectionBackground(Theme.alpha(Theme.ACCENT, 40));
        table.setSelectionForeground(Theme.TEXT);
        table.getTableHeader().setFont(Theme.FONT_LABEL);
        table.getTableHeader().setBackground(Theme.CARD);
        table.getTableHeader().setForeground(Theme.TEXT_DIM);
        table.getTableHeader().setReorderingAllowed(false);

        // Right-align numeric columns
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int col : new int[]{2, 3, 4, 5, 6}) {
            table.getColumnModel().getColumn(col).setCellRenderer(rightAlign);
        }

        // Status column — colour coded
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                if (!sel) {
                    setBackground(row % 2 == 0 ? Theme.SURFACE : Theme.alpha(Theme.CARD, 180));
                }
                setForeground("PAID".equalsIgnoreCase(s) ? Theme.GREEN : Theme.RED);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        int[] widths = {160, 160, 55, 55, 100, 80, 100, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.SURFACE);
        return sp;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Theme.SURFACE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Theme.BORDER.darker());
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 20, 10, 20));

        totalLabel  = new JLabel();
        unpaidLabel = new JLabel();
        totalLabel .setFont(Theme.FONT_BODY); totalLabel .setForeground(Theme.TEXT_DIM);
        unpaidLabel.setFont(Theme.FONT_BODY); unpaidLabel.setForeground(Theme.RED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);
        left.add(totalLabel);
        left.add(unpaidLabel);
        footer.add(left, BorderLayout.WEST);

        JLabel hint = new JLabel("Select an UNPAID invoice, then click \"Pay Invoice\" to process payment");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.TEXT_DIM);
        footer.add(hint, BorderLayout.EAST);

        return footer;
    }

    // ── Data ──────────────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Invoice> invoices = hotel.getInvoices();
        double totalRevenue = 0;
        int    unpaidCount  = 0;

        for (Invoice inv : invoices) {
            tableModel.addRow(new Object[]{
                inv.getInvoiceId(),
                inv.getBookingId(),
                inv.getNumberOfNights(),
                inv.getTotalRooms(),
                String.format("$%.2f", inv.getRoomCharges()),
                String.format("$%.2f", inv.getTaxAmount()),
                String.format("$%.2f", inv.getTotalAmount()),
                inv.isPaid() ? "PAID" : "UNPAID"
            });
            if (inv.isPaid()) totalRevenue += inv.getTotalAmount();
            else              unpaidCount++;
        }

        totalLabel .setText(String.format("Total invoices: %d  |  Revenue collected: $%.2f", invoices.size(), totalRevenue));
        unpaidLabel.setText(unpaidCount > 0 ? "  Unpaid: " + unpaidCount : "");
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void payInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select an invoice first."); return; }

        List<Invoice> invoices = hotel.getInvoices();
        Invoice inv = invoices.get(row);

        if (inv.isPaid()) {
            showInfo("This invoice is already paid.");
            return;
        }

        Payment payment = PaymentDialog.show(this, inv.getTotalAmount());
        if (payment == null) return; // cancelled

        try {
            inv.payInvoice(payment);
            hotel.addPayment(payment);
            refreshTable();
            if (paymentsPanel != null) paymentsPanel.refreshTable();
            showSuccess(String.format(
                "Payment of $%.2f recorded for invoice %s.", inv.getTotalAmount(), inv.getInvoiceId()));
        } catch (Exception ex) {
            showError("Payment failed: " + ex.getMessage());
        }
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select an invoice first."); return; }

        Invoice inv = hotel.getInvoices().get(row);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.SURFACE);
        body.setBorder(new EmptyBorder(12, 20, 12, 20));

        addDetailRow(body, "Invoice ID",      inv.getInvoiceId());
        addDetailRow(body, "Booking ID",      inv.getBookingId());
        addDetailRow(body, "Issue Date",      inv.getIssueDate().toString());
        addDetailRow(body, "Nights",          String.valueOf(inv.getNumberOfNights()));
        addDetailRow(body, "Guests",          String.valueOf(inv.getNumberOfGuests()));
        addDetailRow(body, "Room Charges",    String.format("$%.2f", inv.getRoomCharges()));
        addDetailRow(body, "Extra Person",    String.format("$%.2f", inv.getExtraPersonCharge()));
        addDetailRow(body, "Discount",        String.format("$%.2f", inv.getDiscount()));
        addDetailRow(body, "Tax (10%)",       String.format("$%.2f", inv.getTaxAmount()));
        addDetailRow(body, "Deposit Applied", String.format("$%.2f", inv.getDepositApplied()));

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(Box.createVerticalStrut(4));
        body.add(sep);
        body.add(Box.createVerticalStrut(4));
        addDetailRow(body, "TOTAL",  String.format("$%.2f", inv.getTotalAmount()), true);
        addDetailRow(body, "Status", inv.isPaid() ? "PAID" : "UNPAID");

        JOptionPane.showMessageDialog(this, body,
            "Invoice Details — " + inv.getInvoiceId(),
            JOptionPane.PLAIN_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void addDetailRow(JPanel panel, String label, String value) {
        addDetailRow(panel, label, value, false);
    }

    private void addDetailRow(JPanel panel, String label, String value, boolean bold) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lbl = new JLabel(label);
        lbl.setFont(bold ? new Font("SansSerif", Font.BOLD, 12) : Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_DIM);

        JLabel val = new JLabel(value);
        val.setFont(bold ? new Font("SansSerif", Font.BOLD, 13) : Theme.FONT_BODY);
        val.setForeground(bold ? Theme.ACCENT : Theme.TEXT);
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(2));
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",    JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}