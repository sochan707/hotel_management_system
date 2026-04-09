package hotel.javabeans.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import hotel.javabeans.payment.CashPayment;
import hotel.javabeans.payment.CreditCardPayment;
import hotel.javabeans.payment.Payment;
import hotel.javabeans.payment.QrPayment;

/**
 * Reusable modal dialog for creating a Payment (Cash, Credit Card, or QR).
 * Call PaymentDialog.show(parent, requiredAmount) — returns a Payment or null if cancelled.
 */
public class PaymentDialog extends JDialog {

    private Payment result = null;

    // ── Shared fields ─────────────────────────────────────────────────────────
    private Components.StyledCombo<String> typeCombo;
    private Components.StyledField paymentIdField;
    private Components.StyledField amountField;
    private JPanel dynamicFields;

    // ── Card fields ───────────────────────────────────────────────────────────
    private Components.StyledField cardNumberField;
    private Components.StyledField cardHolderField;
    private Components.StyledField cardExpiryField;

    // ── QR fields ─────────────────────────────────────────────────────────────
    private Components.StyledField accountNumberField;
    private Components.StyledField bankField;
    private JLabel qrImageLabel;
    private File qrImageFile = null;

    private final double requiredAmount;

    private PaymentDialog(Frame owner, double requiredAmount) {
        super(owner, "Process Payment", true);
        this.requiredAmount = requiredAmount;
        buildUI();
        setSize(440, 500);
        setLocationRelativeTo(owner);
        setResizable(true);
    }

    /** Static factory — returns the created Payment, or null if cancelled. */
    public static Payment show(Component parent, double requiredAmount) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(parent);
        PaymentDialog dlg = new PaymentDialog(owner, requiredAmount);
        dlg.setVisible(true);
        return dlg.result;
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    private void buildUI() {
        getContentPane().setBackground(Theme.SURFACE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Pink bottom line
                g2.setColor(Theme.PINK);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("💳  Process Payment");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(Theme.PINK_SOFT);
        header.add(title, BorderLayout.WEST);

        if (requiredAmount > 0) {
            JLabel amtLbl = new JLabel(String.format("Due: $%.2f", requiredAmount));
            amtLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
            amtLbl.setForeground(Theme.GREEN);
            header.add(amtLbl, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setBackground(Theme.SURFACE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 24, 12, 24));

        // Payment type selector
        form.add(fieldLabel("PAYMENT TYPE"));
        form.add(Box.createVerticalStrut(5));
        String[] types = {"Cash", "Credit Card", "QR / Bank Transfer"};
        typeCombo = new Components.StyledCombo<>(types);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        typeCombo.setAlignmentX(LEFT_ALIGNMENT);
        typeCombo.addActionListener(e -> refreshDynamicFields());
        form.add(typeCombo);
        form.add(Box.createVerticalStrut(14));

        // Payment ID
        form.add(fieldLabel("PAYMENT ID"));
        form.add(Box.createVerticalStrut(5));
        paymentIdField = new Components.StyledField(16);
        paymentIdField.setText("PAY-" + System.currentTimeMillis());
        paymentIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        paymentIdField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(paymentIdField);
        form.add(Box.createVerticalStrut(14));

        // Amount
        form.add(fieldLabel("AMOUNT ($)"));
        form.add(Box.createVerticalStrut(5));
        amountField = new Components.StyledField(12);
        amountField.setText(requiredAmount > 0 ? String.format("%.2f", requiredAmount) : "");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        amountField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(amountField);
        form.add(Box.createVerticalStrut(16));

        // Dynamic area
        dynamicFields = new JPanel();
        dynamicFields.setOpaque(false);
        dynamicFields.setLayout(new BoxLayout(dynamicFields, BoxLayout.Y_AXIS));
        dynamicFields.setAlignmentX(LEFT_ALIGNMENT);
        form.add(dynamicFields);

        // Status
        JLabel statusLbl = new JLabel(" ");
        statusLbl.setFont(Theme.FONT_SMALL);
        statusLbl.setForeground(Theme.RED);
        statusLbl.setAlignmentX(LEFT_ALIGNMENT);
        form.add(statusLbl);

        refreshDynamicFields();

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.SURFACE);
        add(sp, BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(Theme.CARD);
        btnBar.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));

        Components.GhostButton cancelBtn = new Components.GhostButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        Components.GoldButton payBtn = new Components.GoldButton("Confirm Payment");
        payBtn.addActionListener(e -> processPayment(statusLbl));

        btnBar.add(cancelBtn);
        btnBar.add(payBtn);
        add(btnBar, BorderLayout.SOUTH);
    }

    private void refreshDynamicFields() {
        dynamicFields.removeAll();
        String type = (String) typeCombo.getSelectedItem();

        if ("Credit Card".equals(type)) {
            dynamicFields.add(fieldLabel("CARD NUMBER (10 digits)"));
            dynamicFields.add(Box.createVerticalStrut(5));
            cardNumberField = new Components.StyledField(14);
            cardNumberField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            cardNumberField.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(cardNumberField);
            dynamicFields.add(Box.createVerticalStrut(10));

            dynamicFields.add(fieldLabel("CARD HOLDER NAME (min 5 chars)"));
            dynamicFields.add(Box.createVerticalStrut(5));
            cardHolderField = new Components.StyledField(16);
            cardHolderField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            cardHolderField.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(cardHolderField);
            dynamicFields.add(Box.createVerticalStrut(10));

            dynamicFields.add(fieldLabel("EXPIRY DATE (MM/YY)"));
            dynamicFields.add(Box.createVerticalStrut(5));
            cardExpiryField = new Components.StyledField(8);
            cardExpiryField.setText("12/28");
            cardExpiryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            cardExpiryField.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(cardExpiryField);
            dynamicFields.add(Box.createVerticalStrut(10));

        } else if ("QR / Bank Transfer".equals(type)) {
            dynamicFields.add(fieldLabel("ACCOUNT NUMBER (9 digits)"));
            dynamicFields.add(Box.createVerticalStrut(5));
            accountNumberField = new Components.StyledField(12);
            accountNumberField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            accountNumberField.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(accountNumberField);
            dynamicFields.add(Box.createVerticalStrut(10));

            dynamicFields.add(fieldLabel("BANK NAME"));
            dynamicFields.add(Box.createVerticalStrut(5));
            bankField = new Components.StyledField(16);
            bankField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            bankField.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(bankField);
            dynamicFields.add(Box.createVerticalStrut(14));

            // ── QR Image Upload ───────────────────────────────────────────────
            dynamicFields.add(fieldLabel("QR CODE IMAGE (optional — for guest to scan)"));
            dynamicFields.add(Box.createVerticalStrut(8));

            // Fixed-size image preview panel
            JPanel qrPreviewPanel = new JPanel(new BorderLayout()) {
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
            qrPreviewPanel.setOpaque(false);
            qrPreviewPanel.setPreferredSize(new Dimension(390, 200));
            qrPreviewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            qrPreviewPanel.setAlignmentX(LEFT_ALIGNMENT);

            qrImageLabel = new JLabel("No image selected", SwingConstants.CENTER);
            qrImageLabel.setFont(Theme.FONT_SMALL);
            qrImageLabel.setForeground(Theme.TEXT_DIM);
            qrPreviewPanel.add(qrImageLabel, BorderLayout.CENTER);

            if (qrImageFile != null) {
                loadQrPreview(qrImageFile);
            }

            dynamicFields.add(qrPreviewPanel);
            dynamicFields.add(Box.createVerticalStrut(8));

            // Browse / Clear buttons
            JPanel browseRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            browseRow.setOpaque(false);
            browseRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            browseRow.setAlignmentX(LEFT_ALIGNMENT);

            JButton browseBtn = Theme.ghostButton("📂  Browse Image…");
            browseBtn.setFont(Theme.FONT_SMALL);
            browseBtn.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select QR Code Image");
                chooser.setFileFilter(new FileNameExtensionFilter(
                        "Image files (PNG, JPG, GIF, BMP)", "png", "jpg", "jpeg", "gif", "bmp"));
                chooser.setAcceptAllFileFilterUsed(false);
                int chosen = chooser.showOpenDialog(PaymentDialog.this);
                if (chosen == JFileChooser.APPROVE_OPTION) {
                    qrImageFile = chooser.getSelectedFile();
                    loadQrPreview(qrImageFile);
                }
            });

            JButton clearBtn = Theme.ghostButton("✕  Clear");
            clearBtn.setFont(Theme.FONT_SMALL);
            clearBtn.addActionListener(e -> {
                qrImageFile = null;
                qrImageLabel.setIcon(null);
                qrImageLabel.setText("No image selected");
            });

            browseRow.add(browseBtn);
            browseRow.add(Box.createHorizontalStrut(8));
            browseRow.add(clearBtn);
            dynamicFields.add(browseRow);
            dynamicFields.add(Box.createVerticalStrut(10));

        } else {
            // Cash — no extra fields, just a note
            JLabel note = new JLabel("ℹ  No additional details required for cash.");
            note.setFont(Theme.FONT_SMALL);
            note.setForeground(Theme.TEXT_DIM);
            note.setAlignmentX(LEFT_ALIGNMENT);
            dynamicFields.add(note);
            dynamicFields.add(Box.createVerticalStrut(10));
        }

        dynamicFields.revalidate();
        dynamicFields.repaint();
        pack();
        String type2 = (String) typeCombo.getSelectedItem();
        int h = "QR / Bank Transfer".equals(type2) ? 680 : 520;
        setSize(Math.max(getWidth(), 440), Math.min(getHeight(), h));
    }

    private void loadQrPreview(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                int size = 170;
                Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                qrImageLabel.setIcon(new ImageIcon(scaled));
                qrImageLabel.setText(null);
            } else {
                qrImageLabel.setIcon(null);
                qrImageLabel.setText("⚠  Could not load image");
            }
        } catch (Exception ex) {
            qrImageLabel.setIcon(null);
            qrImageLabel.setText("⚠  Error: " + ex.getMessage());
        }
    }

    private void processPayment(JLabel statusLbl) {
        try {
            String payId  = paymentIdField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String type   = (String) typeCombo.getSelectedItem();

            Payment payment;
            switch (type) {
                case "Credit Card" -> payment = new CreditCardPayment(
                    payId, amount, LocalDate.now(), Payment.PaymentStatus.COMPLETED,
                    cardNumberField.getText().trim(),
                    cardHolderField.getText().trim(),
                    cardExpiryField.getText().trim()
                );
                case "QR / Bank Transfer" -> payment = new QrPayment(
                    payId, amount, LocalDate.now(), Payment.PaymentStatus.COMPLETED,
                    accountNumberField.getText().trim(),
                    bankField.getText().trim()
                );
                default -> payment = new CashPayment(
                    payId, amount, LocalDate.now(), Payment.PaymentStatus.COMPLETED
                );
            }

            result = payment;
            dispose();

        } catch (NumberFormatException ex) {
            statusLbl.setText("✗  Enter a valid amount.");
        } catch (IllegalArgumentException ex) {
            statusLbl.setText("✗  " + ex.getMessage());
        }
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(Theme.PINK_DIM);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Inner styled components (reuse Theme palette) ─────────────────────────
    // These mirror hotel.ui.Components but inline here so the dialog is self-contained
    private static class Components {
        static class StyledField extends JTextField {
            StyledField(int cols) {
                super(cols);
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

        static class GoldButton extends JButton {
            private boolean hov = false;
            GoldButton(String text) {
                super(text);
                setOpaque(false); setContentAreaFilled(false);
                setBorderPainted(false); setFocusPainted(false);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setPreferredSize(new Dimension(getPreferredSize().width + 28, 36));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hov ? Theme.PINK_GLOW : Theme.PINK;
                g2.setPaint(new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        }

        static class GhostButton extends JButton {
            private boolean hov = false;
            GhostButton(String text) {
                super(text);
                setOpaque(false); setContentAreaFilled(false);
                setBorderPainted(false); setFocusPainted(false);
                setFont(Theme.FONT_BODY);
                setForeground(Theme.TEXT_DIM);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setBorder(new EmptyBorder(7, 14, 7, 14));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) { g2.setColor(Theme.alpha(Theme.PINK, 18)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); }
                g2.setColor(Theme.PINK_DIM); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                super.paintComponent(g); g2.dispose();
            }
        }
    }
}