package hotel.javabeans.ui;

import hotel.javabeans.*;
import hotel.javabeans.Login;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window after successful staff login.
 * Role-based sidebar navigation:
 *  - Manager     : Rooms, Guests, Staff, Reservations
 *  - Receptionist: Rooms, Guests, Reservations (no Staff tab)
 */
public class DashboardFrame extends JFrame {

    private final Hotel hotel;
    private final Login currentUser;

    private JPanel     contentArea;
    private CardLayout cardLayout;

    private static final String NAV_ROOMS        = "Rooms";
    private static final String NAV_GUESTS       = "Guests";
    private static final String NAV_STAFF        = "Staff";
    private static final String NAV_RESERVATIONS = "Reservations";

    public DashboardFrame(Hotel hotel, Login currentUser) {
        this.hotel       = hotel;
        this.currentUser = currentUser;
        buildUI();
    }

    private void buildUI() {
        setTitle("Grand Palace — " + hotel.getHotelName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom shadow line
                g2.setColor(Theme.BORDER);
                g2.fillRect(0, getHeight()-1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 54));
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Hotel name left
        JLabel nameLbl = new JLabel("⛨  " + hotel.getHotelName().toUpperCase());
        nameLbl.setFont(new Font("Georgia", Font.BOLD, 15));
        nameLbl.setForeground(Theme.ACCENT);
        bar.add(nameLbl, BorderLayout.WEST);

        // User info right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        // Role badge
        Color badgeColor = currentUser.getRole().equals("Manager") ? Theme.ACCENT : Theme.GREEN;
        JLabel roleBadge = new JLabel(currentUser.getRole().toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(badgeColor, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.alpha(badgeColor, 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        roleBadge.setFont(Theme.FONT_LABEL);
        roleBadge.setForeground(badgeColor);
        roleBadge.setOpaque(false);
        roleBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JLabel userLbl = new JLabel(currentUser.getUsername());
        userLbl.setFont(Theme.FONT_BODY);
        userLbl.setForeground(Theme.TEXT);

        JButton logoutBtn = Theme.ghostButton("Logout");
        logoutBtn.setFont(Theme.FONT_SMALL);
        logoutBtn.addActionListener(e -> handleLogout());

        right.add(roleBadge);
        right.add(userLbl);
        right.add(logoutBtn);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JButton activeNavBtn = null;

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.SURFACE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border
                g2.setColor(Theme.BORDER);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        sidebar.add(Box.createVerticalStrut(16));

        String role = currentUser.getRole();

        if (role.equals("Manager")) {
            // Full access
            addNavItem(sidebar, "🏠   " + NAV_ROOMS,        NAV_ROOMS);
            addNavItem(sidebar, "👤   " + NAV_GUESTS,       NAV_GUESTS);
            addNavItem(sidebar, "👔   " + NAV_STAFF,        NAV_STAFF);
            addNavItem(sidebar, "📋   " + NAV_RESERVATIONS, NAV_RESERVATIONS);
        } else {
            // Receptionist — no Staff management
            addNavItem(sidebar, "🏠   " + NAV_ROOMS,        NAV_ROOMS);
            addNavItem(sidebar, "👤   " + NAV_GUESTS,       NAV_GUESTS);
            addNavItem(sidebar, "📋   " + NAV_RESERVATIONS, NAV_RESERVATIONS);
        }

        sidebar.add(Box.createVerticalGlue());

        // Stats
        JLabel overviewLbl = new JLabel("  OVERVIEW");
        overviewLbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        overviewLbl.setForeground(Theme.TEXT_DIM);
        overviewLbl.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(overviewLbl);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(buildSidebarStats());
        sidebar.add(Box.createVerticalStrut(16));

        return sidebar;
    }

    private void addNavItem(JPanel sidebar, String label, String cardName) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (this == activeNavBtn) {
                    g2.setColor(Theme.alpha(Theme.ACCENT, 12));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // Active left bar
                    g2.setColor(Theme.ACCENT);
                    g2.fillRect(0, 0, 3, getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(Theme.alpha(Theme.ACCENT, 7));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));

        btn.addActionListener(e -> {
            if (activeNavBtn != null) activeNavBtn.setForeground(Theme.TEXT_DIM);
            activeNavBtn = btn;
            btn.setForeground(Theme.ACCENT);
            cardLayout.show(contentArea, cardName);
            sidebar.repaint();
        });

        if (activeNavBtn == null) {
            activeNavBtn = btn;
            btn.setForeground(Theme.ACCENT);
        }
        sidebar.add(btn);
    }

    private JPanel buildSidebarStats() {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 14, 0, 14));
        p.add(statLine("Rooms",  String.valueOf(hotel.getTotalRooms())));
        p.add(statLine("Guests", String.valueOf(hotel.getTotalGuests())));
        p.add(statLine("Staff",  String.valueOf(hotel.getTotalStaff())));

        // Separator line above stats
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Theme.BORDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 14, 0, 14));
        wrapper.add(sep, BorderLayout.NORTH);
        wrapper.add(p,   BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel statLine(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_DIM);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 12));
        val.setForeground(Theme.ACCENT);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG);

        contentArea.add(new RoomsPanel(hotel),        NAV_ROOMS);
        contentArea.add(new GuestsPanel(hotel),       NAV_GUESTS);
        contentArea.add(new StaffPanel(hotel),        NAV_STAFF);
        contentArea.add(new ReservationsPanel(hotel), NAV_RESERVATIONS);

        cardLayout.first(contentArea);
        return contentArea;
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    private void handleLogout() {
        int ok = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            try { currentUser.logout(); } catch (Exception ignored) {}
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}