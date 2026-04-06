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
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hotel.javabeans.Hotel;
import hotel.javabeans.payment.CashPayment;
import hotel.javabeans.payment.CreditCardPayment;
import hotel.javabeans.payment.Payment;
import hotel.javabeans.payment.PaymentMethods;
import hotel.javabeans.payment.QrPayment;

/**
 * PaymentsPanel — lists all payments in the hotel and lets staff
 * record Cash, Credit Card, or QR payments manually.
 * "Pay Invoice" links from InvoicesPanel via PaymentDialog.show().
 */
public class PaymentsPanel extends JPanel {

    private final Hotel hotel;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalLabel;
    private JLabel countLabel;

    private static final String[] COLUMNS = {
        "Payment ID", "Type", "Amount", "Date", "Status"
    };

    public PaymentsPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG);
        outer.setBorder(new EmptyBorder(24, 28, 14, 28));

        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(Theme.sectionTitle("Payment Records"));
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(Theme.sectionSub("All cash, card and QR payments recorded in the system"));
        outer.add(titleBlock, BorderLayout.WEST);

        // Summary badges
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        badges.setOpaque(false);

        totalLabel = makeBadgeLabel("Revenue: $0.00", Theme.GREEN);
        countLabel = makeBadgeLabel("0 Payments", Theme.PINK_SOFT);
        badges.add(countLabel);
        badges.add(totalLabel);
        outer.add(badges, BorderLayout.EAST);

        // Toolbar row
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(Theme.BG);
        toolbar.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton cashBtn  = Theme.primaryButton("+ Cash");
        JButton cardBtn  = Theme.primaryButton("+ Card");
        JButton qrBtn    = Theme.primaryButton("+ QR");
        JButton viewBtn  = Theme.ghostButton("👁  Details");
        JButton delBtn   = Theme.dangerButton("✕  Remove");

        // Override widths
        cashBtn.setPreferredSize(new Dimension(88, 34));
        cardBtn.setPreferredSize(new Dimension(88, 34));
        qrBtn.setPreferredSize(new Dimension(88, 34));

        cashBtn.addActionListener(e -> addCash());
        cardBtn.addActionListener(e -> addCard());
        qrBtn.addActionListener(e  -> addQr());
        viewBtn.addActionListener(e -> viewDetails());
        delBtn.addActionListener(e  -> removeSelected());

        toolbar.add(cashBtn);
        toolbar.add(cardBtn);
        toolbar.add(qrBtn);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(viewBtn);
        toolbar.add(delBtn);

        JPanel combined = new JPanel(new BorderLayout());
        combined.setOpaque(false);
        combined.add(outer,   BorderLayout.CENTER);
        combined.add(toolbar, BorderLayout.SOUTH);
        combined.setBorder(new EmptyBorder(0, 0, 8, 0));
        return combined;
    }

    private JLabel makeBadgeLabel(String text, Color color) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(color, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Theme.alpha(color, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(color);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(4, 12, 4, 12));
        return lbl;
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
                    String status = (String) getModel().getValueAt(row, 4);
                    c.setForeground(statusColor(status));
                }
                return c;
            }
        };
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Right-align Amount column
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightAlign);

        // Centre-align Status
        DefaultTableCellRenderer centreAlign = new DefaultTableCellRenderer();
        centreAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(centreAlign);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        refreshTable();

        JScrollPane sp = Theme.scrollPane(table);
        sp.setBorder(new EmptyBorder(0, 28, 0, 28));
        return sp;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));

        JLabel legend = new JLabel(
            "●  COMPLETED   ●  PENDING   ●  FAILED   ●  REFUNDED");
        legend.setFont(new Font("SansSerif", Font.PLAIN, 10));
        legend.setForeground(Theme.TEXT_DIM);
        bar.add(legend);
        return bar;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<hotel.javabeans.payment.Payment> payments = hotel.getPayments();

        double total = 0;
        for (hotel.javabeans.payment.Payment p : payments) {
            String type = paymentType(p);
            tableModel.addRow(new Object[]{
                p.getPaymentId(),
                type,
                String.format("$%.2f", p.getAmountPaid()),
                p.getPaymentDate().toString(),
                p.getPaymentStatus().name()
            });
            if (p.getPaymentStatus() == Payment.PaymentStatus.COMPLETED)
                total += p.getAmountPaid();
        }

        totalLabel.setText(String.format("Revenue: $%.2f", total));
        countLabel.setText(payments.size() + " Payment" + (payments.size() == 1 ? "" : "s"));
    }

    private String paymentType(Payment p) {
        if (p instanceof CreditCardPayment) return "💳  Credit Card";
        if (p instanceof QrPayment)         return "📱  QR / Bank";
        return "💵  Cash";
    }

    private Color statusColor(String status) {
        return switch (status) {
            case "COMPLETED" -> Theme.GREEN;
            case "PENDING"   -> Theme.YELLOW;
            case "FAILED"    -> Theme.RED;
            case "REFUNDED"  -> Theme.BLUE;
            default          -> Theme.TEXT;
        };
    }

    // ── Add: Cash ─────────────────────────────────────────────────────────────
    private void addCash() {
        JDialog dlg = styledDialog("Add Cash Payment", 380, 280);
        JPanel form = buildFormPanel();

        Components.StyledField idF  = styledField();
        idF.setText("PAY-" + System.currentTimeMillis());
        Components.StyledField amtF = styledField();
        Components.StyledCombo<String> statusCb = styledCombo("COMPLETED", "PENDING", "FAILED");

        addRow(form, "Payment ID:", idF);
        addRow(form, "Amount ($):", amtF);
        addRow(form, "Status:",     statusCb);

        JButton save = Theme.primaryButton("Add");
        save.addActionListener(e -> {
            try {
                CashPayment p = new CashPayment(
                    idF.getText().trim(),
                    Double.parseDouble(amtF.getText().trim()),
                    LocalDate.now(),
                    Payment.PaymentStatus.valueOf((String) statusCb.getSelectedItem())
                );
                hotel.addPayment(p);
                refreshTable();
                dlg.dispose();
                showSuccess("Cash payment recorded.");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });

        finishDialog(dlg, form, save);
    }

    // ── Add: Credit Card ──────────────────────────────────────────────────────
    private void addCard() {
        JDialog dlg = styledDialog("Add Card Payment", 400, 370);
        JPanel form = buildFormPanel();

        Components.StyledField idF     = styledField(); idF.setText("PAY-" + System.currentTimeMillis());
        Components.StyledField amtF    = styledField();
        Components.StyledField numF    = styledField();
        Components.StyledField holderF = styledField();
        Components.StyledField expF    = styledField(); expF.setText("12/28");

        addRow(form, "Payment ID:",          idF);
        addRow(form, "Amount ($):",          amtF);
        addRow(form, "Card Number (10 dig):", numF);
        addRow(form, "Card Holder (≥5 ch):", holderF);
        addRow(form, "Expiry (MM/YY):",      expF);

        JButton save = Theme.primaryButton("Add");
        save.addActionListener(e -> {
            try {
                CreditCardPayment p = new CreditCardPayment(
                    idF.getText().trim(),
                    Double.parseDouble(amtF.getText().trim()),
                    LocalDate.now(),
                    Payment.PaymentStatus.COMPLETED,
                    numF.getText().trim(),
                    holderF.getText().trim(),
                    expF.getText().trim()
                );
                hotel.addPayment(p);
                refreshTable();
                dlg.dispose();
                showSuccess("Card payment recorded.");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });

        finishDialog(dlg, form, save);
    }

    // ── Add: QR ───────────────────────────────────────────────────────────────
    private void addQr() {
        JDialog dlg = styledDialog("Add QR / Bank Payment", 420, 580);
        JPanel form = buildFormPanel();

        Components.StyledField idF   = styledField(); idF.setText("PAY-" + System.currentTimeMillis());
        Components.StyledField amtF  = styledField();
        Components.StyledField accF  = styledField();
        Components.StyledField bankF = styledField();

        addRow(form, "Payment ID:",         idF);
        addRow(form, "Amount ($):",         amtF);
        addRow(form, "Account No (9 dig):", accF);
        addRow(form, "Bank Name:",          bankF);

        // ── QR Image section ─────────────────────────────────────────────────
        final File[] qrFile = {null};

        // Label spanning 2 columns
        GridBagConstraints gcLbl = new GridBagConstraints();
        gcLbl.gridx = 0; gcLbl.gridy = formRow; gcLbl.gridwidth = 2;
        gcLbl.anchor = GridBagConstraints.WEST;
        gcLbl.insets = new Insets(10, 4, 4, 4);
        JLabel qrLbl = new JLabel("QR CODE IMAGE (optional — for guest to scan)");
        qrLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        qrLbl.setForeground(Theme.PINK_DIM);
        form.add(qrLbl, gcLbl);
        formRow++;

        // Image preview panel spanning 2 columns
        JLabel qrImageLabel = new JLabel("No image selected", SwingConstants.CENTER);
        qrImageLabel.setFont(Theme.FONT_SMALL);
        qrImageLabel.setForeground(Theme.TEXT_DIM);

        JPanel qrPreview = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        0, new float[]{6, 4}, 0));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
            }
        };
        qrPreview.setOpaque(false);
        qrPreview.setPreferredSize(new Dimension(360, 200));
        qrPreview.add(qrImageLabel, BorderLayout.CENTER);

        GridBagConstraints gcImg = new GridBagConstraints();
        gcImg.gridx = 0; gcImg.gridy = formRow; gcImg.gridwidth = 2;
        gcImg.fill = GridBagConstraints.HORIZONTAL;
        gcImg.insets = new Insets(0, 4, 6, 4);
        form.add(qrPreview, gcImg);
        formRow++;

        // Browse / Clear buttons spanning 2 columns
        JPanel browseRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        browseRow.setOpaque(false);

        JButton browseBtn = Theme.ghostButton("📂  Browse Image…");
        browseBtn.setFont(Theme.FONT_SMALL);
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select QR Code Image");
            chooser.setFileFilter(new FileNameExtensionFilter(
                    "Image files (PNG, JPG, GIF, BMP)", "png", "jpg", "jpeg", "gif", "bmp"));
            chooser.setAcceptAllFileFilterUsed(false);
            int chosen = chooser.showOpenDialog(dlg);
            if (chosen == JFileChooser.APPROVE_OPTION) {
                qrFile[0] = chooser.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(qrFile[0]);
                    if (img != null) {
                        Image scaled = img.getScaledInstance(190, 190, Image.SCALE_SMOOTH);
                        qrImageLabel.setIcon(new ImageIcon(scaled));
                        qrImageLabel.setText(null);
                    }
                } catch (Exception ex) {
                    qrImageLabel.setText("⚠  Could not load image");
                }
            }
        });

        JButton clearBtn = Theme.ghostButton("✕  Clear");
        clearBtn.setFont(Theme.FONT_SMALL);
        clearBtn.addActionListener(e -> {
            qrFile[0] = null;
            qrImageLabel.setIcon(null);
            qrImageLabel.setText("No image selected");
        });

        browseRow.add(browseBtn);
        browseRow.add(Box.createHorizontalStrut(8));
        browseRow.add(clearBtn);

        GridBagConstraints gcBtn = new GridBagConstraints();
        gcBtn.gridx = 0; gcBtn.gridy = formRow; gcBtn.gridwidth = 2;
        gcBtn.anchor = GridBagConstraints.WEST;
        gcBtn.insets = new Insets(0, 4, 4, 4);
        form.add(browseRow, gcBtn);
        formRow++;

        JButton save = Theme.primaryButton("Add");
        save.addActionListener(e -> {
            try {
                QrPayment p = new QrPayment(
                    idF.getText().trim(),
                    Double.parseDouble(amtF.getText().trim()),
                    LocalDate.now(),
                    Payment.PaymentStatus.COMPLETED,
                    accF.getText().trim(),
                    bankF.getText().trim()
                );
                hotel.addPayment(p);
                refreshTable();
                dlg.dispose();
                showSuccess("QR payment recorded.");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });

        finishDialog(dlg, form, save);
    }

    // ── View details ──────────────────────────────────────────────────────────
    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a payment to view."); return; }

        Payment p = hotel.getPayments().get(row);

        JDialog dlg = styledDialog("Payment Details", 420, 340);
        JPanel body = new JPanel();
        body.setBackground(Theme.SURFACE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Detail rows
        addDetailRow(body, "Payment ID",  p.getPaymentId());
        addDetailRow(body, "Type",        paymentType(p).replaceAll(".*  ", ""));
        addDetailRow(body, "Amount",      String.format("$%.2f", p.getAmountPaid()));
        addDetailRow(body, "Date",        p.getPaymentDate().toString());
        addDetailRow(body, "Status",      p.getPaymentStatus().name());

        if (p instanceof CreditCardPayment cc) {
            addDetailRow(body, "Card Details", cc.toString().lines()
                .filter(l -> l.contains(":")).findFirst().orElse(""));
        } else if (p instanceof QrPayment) {
            addDetailRow(body, "Method", "QR / Bank Transfer");
        }

        // Process & validate buttons
        body.add(Box.createVerticalStrut(16));
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton processBtn = Theme.ghostButton("Process");
        processBtn.addActionListener(ev -> {
            p.processPayment();
            showSuccess("processPayment() called — see console.");
        });
        JButton validateBtn = Theme.ghostButton("Validate");
        validateBtn.addActionListener(ev -> {
            if (p instanceof PaymentMethods pm) pm.validatePayment();
            showSuccess("validatePayment() called — see console.");
        });
        actionRow.add(processBtn);
        actionRow.add(validateBtn);
        body.add(actionRow);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Theme.CARD);
        JButton closeBtn = Theme.ghostButton("Close");
        closeBtn.addActionListener(e -> dlg.dispose());
        btns.add(closeBtn);

        dlg.add(new JScrollPane(body) {{ setBorder(null); getViewport().setBackground(Theme.SURFACE); }}, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.PINK_DIM);
        lbl.setPreferredSize(new Dimension(120, 20));
        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BODY);
        val.setForeground(Theme.TEXT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(6));
    }

    // ── Remove ────────────────────────────────────────────────────────────────
    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a payment to remove."); return; }

        Payment p = hotel.getPayments().get(row);
        int ok = JOptionPane.showConfirmDialog(this,
            "Remove payment " + p.getPaymentId() + "?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            hotel.removePayment(p);
            refreshTable();
            showSuccess("Payment removed.");
        }
    }

    // ── Dialog helpers ────────────────────────────────────────────────────────
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

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.SURFACE);
        form.setBorder(new EmptyBorder(16, 20, 10, 20));
        return form;
    }

    private int formRow = 0;

    private void addRow(JPanel form, String label, JComponent field) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = formRow; gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_DIM);
        form.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 1;
        form.add(field, gc);
        formRow++;
    }

    private void finishDialog(JDialog dlg, JPanel form, JButton save) {
        formRow = 0; // reset counter for next dialog
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Theme.CARD);
        btns.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        JButton cancel = Theme.ghostButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        btns.add(cancel);
        btns.add(save);
        dlg.add(form,  BorderLayout.CENTER);
        dlg.add(btns,  BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private Components.StyledField styledField() {
        return new Components.StyledField();
    }

    private Components.StyledCombo<String> styledCombo(String... items) {
        return new Components.StyledCombo<>(items);
    }

    // ── Mini component wrappers (use Theme factories directly) ────────────────
    private static class Components {
        static class StyledField extends JTextField {
            StyledField() {
                setBackground(Theme.CARD);
                setForeground(Theme.TEXT);
                setCaretColor(Theme.PINK_SOFT);
                setFont(Theme.FONT_BODY);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        }

        static class StyledCombo<T> extends JComboBox<T> {
            StyledCombo(T[] items) {
                super(items);
                setBackground(Theme.CARD);
                setForeground(Theme.TEXT);
                setFont(Theme.FONT_BODY);
                setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
                setRenderer(new DefaultListCellRenderer() {
                    @Override public Component getListCellRendererComponent(JList<?> list,
                            Object value, int index, boolean isSelected, boolean hasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                        setBackground(isSelected ? Theme.PINK_DIM : Theme.CARD);
                        setForeground(Theme.TEXT);
                        setBorder(new EmptyBorder(4, 8, 4, 8));
                        return this;
                    }
                });
            }
        }
    }

    private void showError(String m)   { JOptionPane.showMessageDialog(this, m, "Error",   JOptionPane.ERROR_MESSAGE); }
    private void showSuccess(String m) { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
}