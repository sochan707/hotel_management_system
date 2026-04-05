package hotel.javabeans.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import hotel.javabeans.Hotel;
import hotel.javabeans.Login;
import hotel.javabeans.Person.Guest;
import hotel.javabeans.Person.Manager;
import hotel.javabeans.Person.Receptionist;
import hotel.javabeans.Room;
import hotel.javabeans.TypeOfRoom;

/**
 * Login window — only Staff (Manager / Receptionist) may authenticate.
 * Left panel shows clickable demo-account cards; right panel is the form.
 */
public class LoginFrame extends JFrame {

    // ── Demo accounts — STAFF ONLY ────────────────────────────────────────────
    private static final DemoAccount[] ACCOUNTS = {
        new DemoAccount("Alice Wonders",   "alice",      "alice123",   "Manager",      "Full system access",              new Color(0x2D4EAA)),
        new DemoAccount("David Chen",      "david",      "david123",   "Manager",      "Full system access",              new Color(0x4A6FD4)),
        new DemoAccount("Bob Smith",       "bob",        "bob12345",   "Receptionist", "Check-in / check-out, bookings",  new Color(0x1A8044)),
        new DemoAccount("Clara Johnson",   "clara",      "clara123",   "Receptionist", "Check-in / check-out, bookings",  new Color(0x358060)),
    };

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JCheckBox      showPassCheck;
    private JLabel         statusLabel;

    private Login[] logins;
    private Hotel   hotel;

    public LoginFrame() {
        initData();
        buildUI();
    }

    // ── Sample data ───────────────────────────────────────────────────────────
    private void initData() {
        hotel = new Hotel("Grand Palace Hotel", "123 Royal Avenue, Phnom Penh");

        hotel.addRoom(new Room("101", TypeOfRoom.SINGLE, 50.0));
        hotel.addRoom(new Room("102", TypeOfRoom.SINGLE, 55.0));
        hotel.addRoom(new Room("103", TypeOfRoom.SINGLE, 50.0));
        hotel.addRoom(new Room("201", TypeOfRoom.DOUBLE, 80.0));
        hotel.addRoom(new Room("202", TypeOfRoom.DOUBLE, 85.0));
        hotel.addRoom(new Room("203", TypeOfRoom.DOUBLE, 80.0));
        hotel.addRoom(new Room("301", TypeOfRoom.TRIPLE, 120.0));
        hotel.addRoom(new Room("302", TypeOfRoom.TRIPLE, 130.0));
        hotel.getRooms().get(0).setStatus(Room.OCCUPIED);
        hotel.getRooms().get(3).setStatus(Room.BOOKED);
        hotel.getRooms().get(6).setStatus(Room.OCCUPIED);

        hotel.addStaff(new Manager     ("S001", "Alice", "Wonders",  "Female", "012345678"));
        hotel.addStaff(new Manager     ("S002", "David", "Chen",     "Male",   "012345670"));
        hotel.addStaff(new Receptionist("S003", "Bob",   "Smith",    "Male",   "012345679"));
        hotel.addStaff(new Receptionist("S004", "Clara", "Johnson",  "Female", "012345671"));

        hotel.addGuest(new Guest("G001", "John",  "Doe",    "Male",   "098765432"));
        hotel.addGuest(new Guest("G002", "Jane",  "Doe",    "Female", "098765433"));
        hotel.addGuest(new Guest("G003", "Carol", "White",  "Female", "098765434"));

        // Only staff can log in
        logins = new Login[]{
            makeLogin("alice", "alice123",  "Manager"),
            makeLogin("david", "david123",  "Manager"),
            makeLogin("bob",   "bob12345",  "Receptionist"),
            makeLogin("clara", "clara123",  "Receptionist"),
        };
    }

    private Login makeLogin(String user, String pass, String role) {
        return new Login(user, pass) {
            @Override public String  getRole()   { return role; }
            @Override public boolean hasAccess() { return isLoggedIn(); }
            @Override public String  getRolePermissions() {
                return role.equals("Manager")
                    ? "Full access: rooms, guests, staff, reservations, invoices"
                    : "Limited access: rooms, guests, reservations, check-in/out";
            }
        };
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("Grand Palace — Staff Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildRoot());
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Theme.BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        return root;
    }

    // ── LEFT — branding + account cards ──────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Soft blue-grey gradient
                g2.setPaint(new GradientPaint(0, 0, new Color(0xEBEFF8), 0, getHeight(), new Color(0xDFE5F4)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border
                g2.setColor(Theme.BORDER);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(360, 0));
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(44, 36, 36, 36));

        // ── Branding ─────────────────────────────────────────────────────────
        JLabel icon = new JLabel("⛨");
        icon.setFont(new Font("Serif", Font.PLAIN, 38));
        icon.setForeground(Theme.ACCENT);
        icon.setAlignmentX(LEFT_ALIGNMENT);
        left.add(icon);
        left.add(Box.createVerticalStrut(8));

        JLabel nameL = new JLabel("GRAND PALACE");
        nameL.setFont(new Font("Georgia", Font.BOLD, 22));
        nameL.setForeground(Theme.ACCENT);
        nameL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(nameL);

        JLabel tagL = new JLabel("Hotel Management System");
        tagL.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tagL.setForeground(Theme.TEXT_DIM);
        tagL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(tagL);
        left.add(Box.createVerticalStrut(6));

        // Accent underline
        JPanel ul = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Theme.ACCENT, 140, 0, new Color(0, 0, 0, 0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        ul.setMaximumSize(new Dimension(140, 2));
        ul.setOpaque(false);
        ul.setAlignmentX(LEFT_ALIGNMENT);
        left.add(ul);
        left.add(Box.createVerticalStrut(28));

        // ── Staff accounts section ────────────────────────────────────────────
        JLabel sectionL = new JLabel("STAFF ACCOUNTS");
        sectionL.setFont(new Font("SansSerif", Font.BOLD, 9));
        sectionL.setForeground(Theme.TEXT_DIM);
        sectionL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(sectionL);

        JLabel hintL = new JLabel("Click a card to auto-fill credentials");
        hintL.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hintL.setForeground(new Color(0xAAB5CC));
        hintL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(hintL);
        left.add(Box.createVerticalStrut(10));

        for (int i = 0; i < ACCOUNTS.length; i++) {
            final int idx = i;
            left.add(buildAccountCard(ACCOUNTS[i], idx));
            left.add(Box.createVerticalStrut(8));
        }

        left.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("© 2025 Grand Palace Hotel  ·  Staff portal");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        footer.setForeground(new Color(0xBCC5DC));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        left.add(footer);
        return left;
    }

    private JPanel buildAccountCard(DemoAccount acc, int idx) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private boolean hov = false;
            {
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hov = true;  repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                    public void mouseExited (MouseEvent e) { hov = false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                    public void mouseClicked(MouseEvent e) { fillCredentials(idx); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? Theme.alpha(acc.color, 22) : Theme.alpha(Color.WHITE, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hov ? Theme.alpha(acc.color, 100) : Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // colour strip
                g2.setColor(acc.color);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        card.setBorder(new EmptyBorder(10, 14, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel nameL = new JLabel(acc.displayName);
        nameL.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameL.setForeground(Theme.TEXT);

        JLabel credL = new JLabel(acc.username + "  ·  " + acc.password);
        credL.setFont(Theme.FONT_MONO);
        credL.setForeground(Theme.ACCENT_SOFT);

        JLabel descL = new JLabel(acc.description);
        descL.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descL.setForeground(Theme.TEXT_DIM);

        text.add(nameL);
        text.add(Box.createVerticalStrut(1));
        text.add(credL);
        text.add(descL);

        JLabel badge = buildBadge(acc.role, acc.color);
        card.add(text,  BorderLayout.CENTER);
        card.add(badge, BorderLayout.EAST);
        return card;
    }

    private JLabel buildBadge(String role, Color color) {
        JLabel lbl = new JLabel(role) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.alpha(color, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.alpha(color, 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(color);
        lbl.setOpaque(false);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        lbl.setPreferredSize(new Dimension(84, 22));
        return lbl;
    }

    // ── RIGHT — login form ────────────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(buildFormCard());
        return right;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // White card with drop shadow simulation
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 18, 18);
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 18, 18);
                // Accent top border
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(50, 0, getWidth()-54, 0);
                // Blue top glow
                g2.setPaint(new GradientPaint(0, 0, Theme.alpha(Theme.ACCENT, 12), 0, 50, new Color(0, 0, 0, 0)));
                g2.fillRoundRect(1, 1, getWidth()-5, 50, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 490));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 36, 46));

        JLabel heading = new JLabel("Staff Sign In");
        heading.setFont(new Font("Georgia", Font.BOLD, 24));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);

        JLabel sub = new JLabel("Access the hotel management portal");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Theme.TEXT_DIM);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(6));

        // Accent rule
        JPanel rule = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Theme.ACCENT, 100, 0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        rule.setOpaque(false);
        rule.setAlignmentX(LEFT_ALIGNMENT);
        card.add(rule);
        card.add(Box.createVerticalStrut(26));

        card.add(fieldLabel("USERNAME"));
        card.add(Box.createVerticalStrut(5));
        usernameField = Theme.textField();
        usernameField.setText("alice");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));

        card.add(fieldLabel("PASSWORD"));
        card.add(Box.createVerticalStrut(5));
        passwordField = Theme.passwordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));

        showPassCheck = new JCheckBox("Show password");
        showPassCheck.setOpaque(false);
        showPassCheck.setFont(new Font("SansSerif", Font.PLAIN, 11));
        showPassCheck.setForeground(Theme.TEXT_DIM);
        showPassCheck.setFocusPainted(false);
        showPassCheck.setAlignmentX(LEFT_ALIGNMENT);
        showPassCheck.addActionListener(e ->
            passwordField.setEchoChar(showPassCheck.isSelected() ? '\0' : '●'));
        card.add(showPassCheck);
        card.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(Theme.RED);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));

        JButton loginBtn = Theme.primaryButton("SIGN IN  →");
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(22));

        JSeparator div = new JSeparator();
        div.setForeground(Theme.BORDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(div);
        card.add(Box.createVerticalStrut(14));

        JLabel hint = new JLabel("← Select a staff account on the left");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Theme.TEXT_DIM);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);

        // Keyboard shortcuts
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleLogin());
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(loginBtn));
        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(Theme.ACCENT_DIM);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            setStatus("Please enter your username and password.", false);
            return;
        }

        try {
            Login matched = null;
            for (Login l : logins) {
                if (l.authenticate(user, pass)) { matched = l; break; }
            }

            if (matched != null) {
                setStatus("✓  Welcome, " + matched.getUsername() + "!", true);
                final Login logged = matched;
                Timer t = new Timer(650, e -> {
                    dispose();
                    new DashboardFrame(hotel, logged).setVisible(true);
                });
                t.setRepeats(false);
                t.start();
            } else {
                setStatus("✗  Invalid username or password.", false);
                passwordField.setText("");
                shake(this);
            }
        } catch (Login.LoginException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillCredentials(int idx) {
        usernameField.setText(ACCOUNTS[idx].username);
        passwordField.setText(ACCOUNTS[idx].password);
        statusLabel.setText(" ");
        usernameField.requestFocus();
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? Theme.GREEN : Theme.RED);
        statusLabel.setText(msg);
    }

    private void shake(JFrame f) {
        Point o = f.getLocation();
        int[] dx = {-9, 9, -7, 7, -5, 5, -3, 3, 0};
        Timer t = new Timer(28, null);
        int[] i = {0};
        t.addActionListener(e -> {
            if (i[0] < dx.length) f.setLocation(o.x + dx[i[0]++], o.y);
            else { t.stop(); f.setLocation(o); }
        });
        t.start();
    }

    // ── Inner record ──────────────────────────────────────────────────────────
    private static class DemoAccount {
        final String displayName, username, password, role, description;
        final Color  color;
        DemoAccount(String dn, String u, String p, String r, String d, Color c) {
            displayName = dn; username = u; password = p;
            role = r; description = d; color = c;
        }
    }
}