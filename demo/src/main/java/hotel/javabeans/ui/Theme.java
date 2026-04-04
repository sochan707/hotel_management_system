package hotel.javabeans.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Central design tokens — deep dark background, rose-pink accents.
 */
public final class Theme {

    private Theme() {}

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color BG        = new Color(0x13080F);
    public static final Color SURFACE   = new Color(0x1E0F1A);
    public static final Color CARD      = new Color(0x2A1422);
    public static final Color BORDER    = new Color(0x3D1F30);

    public static final Color PINK      = new Color(0xE8437A);
    public static final Color PINK_SOFT = new Color(0xF472A8);
    public static final Color PINK_DIM  = new Color(0x8C2248);
    public static final Color PINK_GLOW = new Color(0xFF6B9D);

    // Keep GOLD alias so DashboardFrame's roleBadge compile if referenced
    public static final Color GOLD      = PINK;

    public static final Color TEXT      = new Color(0xF5E6EF);
    public static final Color TEXT_DIM  = new Color(0xA07088);

    public static final Color GREEN     = new Color(0x4ADE80);
    public static final Color RED       = new Color(0xFF6B6B);
    public static final Color YELLOW    = new Color(0xFBBF24);
    public static final Color BLUE      = new Color(0x7DD3FC);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Georgia",    Font.BOLD,  20);
    public static final Font FONT_HEADER = new Font("Georgia",    Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("SansSerif",  Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("SansSerif",  Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("SansSerif",  Font.BOLD,  10);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 11);

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    // ── Component factories ───────────────────────────────────────────────────

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JTextField textField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x1A0A14));
        f.setForeground(TEXT);
        f.setCaretColor(PINK_SOFT);
        f.setFont(FONT_BODY);
        setBorderNormal(f);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { setBorderFocus(f); }
            public void focusLost(java.awt.event.FocusEvent e)   { setBorderNormal(f); }
        });
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(new Color(0x1A0A14));
        f.setForeground(TEXT);
        f.setCaretColor(PINK_SOFT);
        f.setFont(FONT_BODY);
        setBorderNormal(f);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { setBorderFocus(f); }
            public void focusLost(java.awt.event.FocusEvent e)   { setBorderNormal(f); }
        });
        return f;
    }

    private static void setBorderNormal(JComponent f) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
    }
    private static void setBorderFocus(JComponent f) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_DIM, 1),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
    }

    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(CARD);
        cb.setForeground(TEXT);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        cb.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected, boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                setBackground(isSelected ? PINK_DIM : CARD);
                setForeground(TEXT);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? PINK_DIM
                           : getModel().isRollover() ? PINK_GLOW : PINK;
                g2.setPaint(new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(alpha(PINK, 22));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(PINK_DIM);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setBackground(CARD);
        b.setForeground(TEXT_DIM);
        b.setFont(FONT_BODY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? RED.darker()
                           : getModel().isRollover() ? RED.brighter() : RED;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 36));
        return b;
    }

    public static void styleTable(JTable table) {
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setFont(FONT_BODY);
        table.setRowHeight(34);
        table.setGridColor(BORDER);
        table.setSelectionBackground(alpha(PINK, 80));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(CARD);
        header.setForeground(PINK_SOFT);
        header.setFont(FONT_LABEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PINK_DIM));
        header.setReorderingAllowed(false);
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(SURFACE);
        sp.getViewport().setBackground(SURFACE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setBackground(BG);
        sp.getHorizontalScrollBar().setBackground(BG);
        return sp;
    }
}