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
import javax.swing.table.JTableHeader;

public final class Theme {

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel sectionSub(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_DIM);
        return l;
    }

    private Theme() {}

    // ── Palette — Light / White theme ─────────────────────────────────────────
    public static final Color BG       = new Color(0xF5F6FA);   // page background
    public static final Color SURFACE  = new Color(0xFFFFFF);   // cards & panels
    public static final Color CARD     = new Color(0xF0F2F8);   // input fields
    public static final Color BORDER   = new Color(0xDDE1EE);   // borders / dividers

    // Primary accent — deep navy blue
    public static final Color ACCENT      = new Color(0x2D4EAA);
    public static final Color ACCENT_SOFT = new Color(0x4A6FD4);
    public static final Color ACCENT_DIM  = new Color(0x8AA3DC);
    public static final Color ACCENT_GLOW = new Color(0x6080E0);

    // Legacy aliases so existing panels compile without changes
    public static final Color PINK      = ACCENT;
    public static final Color PINK_SOFT = ACCENT_SOFT;
    public static final Color PINK_DIM  = ACCENT_DIM;
    public static final Color PINK_GLOW = ACCENT_GLOW;
    public static final Color GOLD      = new Color(0xC8952A);

    public static final Color TEXT      = new Color(0x1A2240);
    public static final Color TEXT_DIM  = new Color(0x7A849C);

    public static final Color GREEN     = new Color(0x1A8044);
    public static final Color RED       = new Color(0xCC2828);
    public static final Color YELLOW    = new Color(0xB87010);
    public static final Color BLUE      = new Color(0x1A5BAA);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Georgia",    Font.BOLD,  20);
    public static final Font FONT_HEADER = new Font("Georgia",    Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("SansSerif",  Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("SansSerif",  Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("SansSerif",  Font.BOLD,  10);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 11);

    // ── Utility ───────────────────────────────────────────────────────────────
    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    // ── Component factories ───────────────────────────────────────────────────
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JTextField textField() {
        final JTextField f = new JTextField();
        f.setBackground(SURFACE);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        setBorderNormal(f);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { setBorderFocus(f); }
            public void focusLost(java.awt.event.FocusEvent e)   { setBorderNormal(f); }
        });
        return f;
    }

    public static JPasswordField passwordField() {
        final JPasswordField f = new JPasswordField();
        f.setBackground(SURFACE);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        setBorderNormal(f);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { setBorderFocus(f); }
            public void focusLost(java.awt.event.FocusEvent e)   { setBorderNormal(f); }
        });
        return f;
    }

    private static void setBorderNormal(JComponent c) {
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
    }

    private static void setBorderFocus(JComponent c) {
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_DIM, 1),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
    }

    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(SURFACE);
        cb.setForeground(TEXT);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        cb.setRenderer(new javax.swing.DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? alpha(ACCENT, 30) : SURFACE);
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
                Color c = getModel().isPressed() ? ACCENT.darker()
                        : getModel().isRollover() ? ACCENT_SOFT
                        : ACCENT;
                g2.setPaint(new GradientPaint(0, 0, c, 0, getHeight(), c.darker()));
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
                    g2.setColor(alpha(ACCENT, 14));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setBackground(SURFACE);
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
                Color c = getModel().isPressed() ? RED.darker()
                        : getModel().isRollover() ? RED.brighter()
                        : RED;
                g2.setColor(c);
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
        table.setSelectionBackground(alpha(ACCENT, 35));
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader h = table.getTableHeader();
        h.setBackground(CARD);
        h.setForeground(TEXT_DIM);
        h.setFont(FONT_LABEL);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER));
        h.setReorderingAllowed(false);
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