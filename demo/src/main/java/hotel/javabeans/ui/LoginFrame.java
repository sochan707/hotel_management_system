package hotel.javabeans.ui;

import hotel.javabeans.*;
import hotel.javabeans.Person.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private JCheckBox      showPassCheck;

    private static final DemoAccount[] ACCOUNTS = {
        new DemoAccount("Admin Staff",   "admin",      "admin123",   "Staff",   "Full system access",          new Color(0xE8437A)),
        new DemoAccount("Manager",       "manager01",  "manager123", "Manager", "Staff + management reports",  new Color(0xC084FC)),
        new DemoAccount("Receptionist",  "reception",  "recep123",   "Staff",   "Check-in / check-out only",   new Color(0xF472A8)),
        new DemoAccount("Guest — Alice", "alice",      "alice123",   "Guest",   "View own reservations",       new Color(0x4ADE80)),
        new DemoAccount("Guest — Bob",   "bob",        "bob12345",   "Guest",   "View own reservations",       new Color(0x7DD3FC)),
    };

    private Login[] logins;
    private Hotel   hotel;

    public LoginFrame() {
        initData();
        buildUI();
    }

    private void initData() {
        hotel = new Hotel("Grand Palace Hotel", "123 Royal Avenue, Phnom Penh");

        hotel.addRoom(new Room("101", RoomType.SINGLE, 50.0));
        hotel.addRoom(new Room("102", RoomType.SINGLE, 55.0));
        hotel.addRoom(new Room("103", RoomType.SINGLE, 50.0));
        hotel.addRoom(new Room("201", RoomType.DOUBLE, 80.0));
        hotel.addRoom(new Room("202", RoomType.DOUBLE, 85.0));
        hotel.addRoom(new Room("203", RoomType.DOUBLE, 80.0));
        hotel.addRoom(new Room("301", RoomType.TRIPLE, 120.0));
        hotel.addRoom(new Room("302", RoomType.TRIPLE, 130.0));
        hotel.getRooms().get(0).setStatus(Room.OCCUPIED);
        hotel.getRooms().get(3).setStatus(Room.BOOKED);
        hotel.getRooms().get(6).setStatus(Room.OCCUPIED);

        hotel.addStaff(new Manager("S001",      "Alice", "Wonders", "Female", "012345678"));
        hotel.addStaff(new Manager("S002",      "David", "Chen",    "Male",   "012345670"));
        hotel.addStaff(new Receptionist("S003", "Bob",   "Smith",   "Male",   "012345679"));
        hotel.addStaff(new Receptionist("S004", "Clara", "Johnson", "Female", "012345671"));

        hotel.addGuest(new Guest("G001", "Alice", "Nguyen", "Female", "098765432"));
        hotel.addGuest(new Guest("G002", "Bob",   "Kim",    "Male",   "098765433"));
        hotel.addGuest(new Guest("G003", "Carol", "White",  "Female", "098765434"));

        logins = new Login[]{
            makeLogin("admin",     "admin123",   "Staff"),
            makeLogin("manager01", "manager123", "Manager"),
            makeLogin("reception", "recep123",   "Staff"),
            makeLogin("alice",     "alice123",   "Guest"),
            makeLogin("bob",       "bob12345",   "Guest"),
        };
    }

    private Login makeLogin(String user, String pass, String role) {
        return new Login(user, pass) {
            @Override public String  getRole()            { return role; }
            @Override public boolean hasAccess()          { return isLoggedIn(); }
            @Override public String  getRolePermissions() {
                return switch (role) {
                    case "Manager" -> "Staff management, payroll, full reports";
                    case "Staff"   -> "Rooms, guests, reservations, invoices";
                    case "Guest"   -> "View own reservations and available rooms";
                    default        -> "Limited access";
                };
            }
        };
    }

    private void buildUI() {
        setTitle("Grand Palace — Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildRoot());
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Pink dot-grid texture
                g2.setColor(new Color(232, 67, 122, 16));
                for (int x = 0; x < getWidth(); x += 28)
                    for (int y = 0; y < getHeight(); y += 28)
                        g2.fillOval(x - 1, y - 1, 2, 2);
                // Ambient pink glow bottom-left
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point2D.Float(120, getHeight() - 80), 300,
                    new float[]{0f, 1f},
                    new Color[]{new Color(232, 67, 122, 50), new Color(0, 0, 0, 0)}
                );
                g2.setPaint(glow);
                g2.fillOval(-120, getHeight() - 350, 600, 480);
                g2.dispose();
            }
        };
        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        return root;
    }

    // ── LEFT ─────────────────────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(0x1E0F1A), 0, getHeight(), new Color(0x13080F)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Fading right border
                g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,0), 0, getHeight()/2, new Color(232,67,122,70)));
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(380, 0));
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(48, 38, 38, 38));

        JLabel icon = new JLabel("✦");
        icon.setFont(new Font("Serif", Font.PLAIN, 40));
        icon.setForeground(Theme.PINK);
        icon.setAlignmentX(LEFT_ALIGNMENT);
        left.add(icon);
        left.add(Box.createVerticalStrut(10));

        JLabel nameL = new JLabel("GRAND PALACE");
        nameL.setFont(new Font("Georgia", Font.BOLD, 24));
        nameL.setForeground(Theme.PINK_SOFT);
        nameL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(nameL);

        JLabel tagL = new JLabel("Hotel Management System");
        tagL.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tagL.setForeground(Theme.TEXT_DIM);
        tagL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(tagL);
        left.add(Box.createVerticalStrut(6));

        // Pink underline accent
        JPanel ul = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Theme.PINK, 160, 0, new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        ul.setMaximumSize(new Dimension(160, 2)); ul.setOpaque(false); ul.setAlignmentX(LEFT_ALIGNMENT);
        left.add(ul);
        left.add(Box.createVerticalStrut(28));

        JLabel sectionL = new JLabel("DEMO ACCOUNTS");
        sectionL.setFont(new Font("SansSerif", Font.BOLD, 9));
        sectionL.setForeground(Theme.PINK_DIM);
        sectionL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(sectionL);

        JLabel hintL = new JLabel("Click any card to auto-fill credentials");
        hintL.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hintL.setForeground(new Color(0x4A2035));
        hintL.setAlignmentX(LEFT_ALIGNMENT);
        left.add(hintL);
        left.add(Box.createVerticalStrut(10));

        for (int i = 0; i < ACCOUNTS.length; i++) {
            final int idx = i;
            left.add(buildAccountCard(ACCOUNTS[i], idx));
            left.add(Box.createVerticalStrut(7));
        }

        left.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("© 2025 Grand Palace Hotel  ·  All rights reserved");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 9));
        footer.setForeground(new Color(0x2A1020));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        left.add(footer);
        return left;
    }

    private JPanel buildAccountCard(DemoAccount acc, int idx) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            private boolean hov = false;
            { setOpaque(false);
              addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                public void mouseExited(MouseEvent e)  { hov = false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                public void mouseClicked(MouseEvent e) { fillCredentials(idx); }
              });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? Theme.alpha(acc.color, 28) : Theme.alpha(Theme.PINK, 10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hov ? Theme.alpha(acc.color, 130) : Theme.alpha(Theme.PINK_DIM, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(acc.color); // left color strip
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.dispose();
            }
        };
        card.setBorder(new EmptyBorder(10, 14, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel text = new JPanel(); text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel nameL = new JLabel(acc.displayName);
        nameL.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameL.setForeground(Theme.TEXT);

        JLabel credL = new JLabel(acc.username + "  ·  " + acc.password);
        credL.setFont(Theme.FONT_MONO);
        credL.setForeground(Theme.PINK_SOFT);

        JLabel descL = new JLabel(acc.description);
        descL.setFont(new Font("SansSerif", Font.PLAIN, 10));
        descL.setForeground(Theme.TEXT_DIM);

        text.add(nameL); text.add(Box.createVerticalStrut(1)); text.add(credL); text.add(descL);

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
                g2.setColor(Theme.alpha(color, 28)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Theme.alpha(color,110)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                super.paintComponent(g); g2.dispose();
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(color);
        lbl.setOpaque(false);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(3, 9, 3, 9));
        lbl.setPreferredSize(new Dimension(78, 22));
        return lbl;
    }

    // ── RIGHT ─────────────────────────────────────────────────────────────────
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
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(Theme.alpha(Theme.PINK, 70));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                // Pink top bar
                g2.setColor(Theme.PINK); g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(50, 0, getWidth()-50, 0);
                // Top inner glow
                g2.setPaint(new GradientPaint(0,0, Theme.alpha(Theme.PINK,18), 0,60, new Color(0,0,0,0)));
                g2.fillRoundRect(1,1,getWidth()-2,60,18,18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 500));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(42, 40, 38, 40));

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Georgia", Font.BOLD, 24));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        card.add(heading);

        JLabel sub = new JLabel("Sign in to manage Grand Palace");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Theme.TEXT_DIM);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(6));

        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,Theme.PINK,100,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();
            }
        };
        accent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        accent.setOpaque(false); accent.setAlignmentX(LEFT_ALIGNMENT);
        card.add(accent);
        card.add(Box.createVerticalStrut(26));

        card.add(fieldLabel("USERNAME"));
        card.add(Box.createVerticalStrut(5));
        usernameField = Theme.textField();
        usernameField.setText("admin");
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
        card.add(Box.createVerticalStrut(8));

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
        card.add(Box.createVerticalStrut(14));

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

        JLabel hint = new JLabel("← Select an account on the left to auto-fill");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(new Color(0x3D1F30));
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);

        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleLogin());
        SwingUtilities.invokeLater(() -> getRootPane().setDefaultButton(loginBtn));
        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(Theme.PINK_DIM);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) { setStatus("Please enter username and password.", false); return; }

        Login matched = null;
        for (Login l : logins) if (l.authenticate(user, pass)) { matched = l; break; }

        if (matched != null) {
            setStatus("✓  Welcome, " + matched.getUsername() + "!", true);
            final Login logged = matched;
            Timer t = new Timer(650, e -> { dispose(); new DashboardFrame(hotel, logged).setVisible(true); });
            t.setRepeats(false); t.start();
        } else {
            setStatus("✗  Invalid username or password.", false);
            passwordField.setText(""); shake(this);
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
        int[] dx = {-9,9,-7,7,-5,5,-3,3,0};
        Timer t = new Timer(28, null); int[] i = {0};
        t.addActionListener(e -> { if (i[0]<dx.length) f.setLocation(o.x+dx[i[0]++],o.y); else {t.stop();f.setLocation(o);} });
        t.start();
    }

    private static class DemoAccount {
        final String displayName, username, password, role, description;
        final Color color;
        DemoAccount(String dn,String u,String p,String r,String d,Color c){
            displayName=dn;username=u;password=p;role=r;description=d;color=c;
        }
    }
}