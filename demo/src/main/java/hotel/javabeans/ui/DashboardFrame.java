package hotel.javabeans.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import hotel.javabeans.Hotel;
import hotel.javabeans.Login;

public class DashboardFrame extends JFrame {

    private final Hotel hotel;
    private final Login currentUser;
    private JPanel     contentArea;
    private CardLayout cardLayout;

    private static final String NAV_ROOMS        = "Rooms";
    private static final String NAV_GUESTS       = "Guests";
    private static final String NAV_STAFF        = "Staff";
    private static final String NAV_RESERVATIONS = "Reservations";
    private static final String NAV_BOOKINGS     = "Bookings";
    private static final String NAV_PAYMENTS     = "Payments";

    private BookingsPanel     bookingsPanel;
    private ReservationsPanel reservationsPanel;
    private PaymentsPanel     paymentsPanel;

    // ── Sidebar stat value labels (updated on every nav) ─────────────────────
    private JLabel statRooms, statGuests, statStaff, statReservations, statPayments;

    public DashboardFrame(Hotel hotel, Login currentUser) {
        this.hotel = hotel; this.currentUser = currentUser;
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
        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.SURFACE); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.PINK_DIM); g2.fillRect(0,getHeight()-1,getWidth(),1);
                g2.setPaint(new GradientPaint(0,0,Theme.alpha(Theme.PINK,22),300,0,new Color(0,0,0,0)));
                g2.fillRect(0,0,300,getHeight()); g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0,54));
        bar.setBorder(new EmptyBorder(0,20,0,20));
        JLabel nameLbl = new JLabel("✦  " + hotel.getHotelName().toUpperCase());
        nameLbl.setFont(new Font("Georgia",Font.BOLD,16));
        nameLbl.setForeground(Theme.PINK_SOFT);
        bar.add(nameLbl, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,14,0));
        right.setOpaque(false);
        JLabel roleBadge = new JLabel(currentUser.getRole().toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color c=currentUser.getRole().equals("Guest")?Theme.BLUE:Theme.PINK;
                g2.setColor(Theme.alpha(c,30));g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Theme.alpha(c,100));g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                super.paintComponent(g);g2.dispose();
            }
        };
        roleBadge.setFont(Theme.FONT_LABEL);
        roleBadge.setForeground(currentUser.getRole().equals("Guest")?Theme.BLUE:Theme.PINK);
        roleBadge.setOpaque(false); roleBadge.setBorder(new EmptyBorder(4,10,4,10));
        JLabel userLbl=new JLabel(currentUser.getUsername());
        userLbl.setFont(Theme.FONT_BODY);userLbl.setForeground(Theme.TEXT);
        JButton logoutBtn=Theme.ghostButton("Logout");
        logoutBtn.setFont(Theme.FONT_SMALL);
        logoutBtn.addActionListener(e->handleLogout());
        right.add(roleBadge);right.add(userLbl);right.add(logoutBtn);
        bar.add(right,BorderLayout.EAST);
        return bar;
    }

    private JButton activeNavBtn=null;

    private JPanel buildSidebar() {
        JPanel sidebar=new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(0x1E0F1A),0,getHeight(),new Color(0x13080F)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.PINK_DIM);g2.fillRect(getWidth()-1,0,1,getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(200,0));
        sidebar.setBorder(new EmptyBorder(20,0,20,0));
        sidebar.add(Box.createVerticalStrut(16));

        String role = currentUser.getRole();

        if ("Manager".equalsIgnoreCase(role)) {
            // Manager sees everything
            addNavItem(sidebar,"⊡  "+NAV_ROOMS,        NAV_ROOMS);
            addNavItem(sidebar,"◈  "+NAV_GUESTS,       NAV_GUESTS);
            addNavItem(sidebar,"◉  "+NAV_STAFF,        NAV_STAFF);
            addNavItem(sidebar,"✦  "+NAV_RESERVATIONS, NAV_RESERVATIONS);
            addNavItem(sidebar,"📋  "+NAV_BOOKINGS,    NAV_BOOKINGS);
            addNavItem(sidebar,"💳  "+NAV_PAYMENTS,    NAV_PAYMENTS);

        } else if ("Receptionist".equalsIgnoreCase(role)) {
            // Receptionist: Rooms (view only), Guests (view only),
            //               Reservations, Bookings, Payments — NO Staff
            addNavItem(sidebar,"⊡  "+NAV_ROOMS,        NAV_ROOMS);
            addNavItem(sidebar,"◈  "+NAV_GUESTS,       NAV_GUESTS);
            addNavItem(sidebar,"✦  "+NAV_RESERVATIONS, NAV_RESERVATIONS);
            addNavItem(sidebar,"📋  "+NAV_BOOKINGS,    NAV_BOOKINGS);
            addNavItem(sidebar,"💳  "+NAV_PAYMENTS,    NAV_PAYMENTS);

        } else {
            // Guest: Reservations and Rooms (view only)
            addNavItem(sidebar,"✦  "+NAV_RESERVATIONS, NAV_RESERVATIONS);
            addNavItem(sidebar,"⊡  "+NAV_ROOMS,        NAV_ROOMS);
        }

        sidebar.add(Box.createVerticalGlue());
        JLabel sh=new JLabel("  OVERVIEW");
        sh.setFont(new Font("SansSerif",Font.BOLD,9));sh.setForeground(Theme.PINK_DIM);sh.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(sh);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(buildSidebarStats());
        sidebar.add(Box.createVerticalStrut(16));
        return sidebar;
    }

    private void addNavItem(JPanel sidebar, String label, String cardName) {
        JButton btn=new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                if(this==activeNavBtn){
                    g2.setColor(Theme.alpha(Theme.PINK,22));g2.fillRect(0,0,getWidth(),getHeight());
                    g2.setPaint(new GradientPaint(0,0,Theme.PINK,3,0,Theme.PINK_DIM));g2.fillRect(0,0,3,getHeight());
                } else if(getModel().isRollover()){
                    g2.setColor(Theme.alpha(Theme.PINK,12));g2.fillRect(0,0,getWidth(),getHeight());
                }
                g2.dispose();super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g){}
        };
        btn.setFont(new Font("SansSerif",Font.PLAIN,13));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);btn.setBorderPainted(false);btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        btn.setBorder(new EmptyBorder(0,18,0,0));
        btn.addActionListener(e->{
            if(activeNavBtn!=null) activeNavBtn.setForeground(Theme.TEXT_DIM);
            activeNavBtn=btn;btn.setForeground(Theme.PINK_SOFT);
            if(NAV_PAYMENTS.equals(cardName)     && paymentsPanel!=null)      paymentsPanel.refreshTable();
            if(NAV_BOOKINGS.equals(cardName)     && bookingsPanel!=null)      bookingsPanel.refreshTable();
            if(NAV_RESERVATIONS.equals(cardName) && reservationsPanel!=null)  reservationsPanel.refreshTable();
            refreshStats();
            cardLayout.show(contentArea,cardName);
            sidebar.repaint();
        });
        if(activeNavBtn==null){activeNavBtn=btn;btn.setForeground(Theme.PINK_SOFT);}
        sidebar.add(btn);
    }

    private JPanel buildSidebarStats() {
        JPanel p=new JPanel(new GridLayout(5,1,0,4));
        p.setOpaque(false);p.setBorder(new EmptyBorder(0,14,0,14));

        statRooms        = new JLabel(String.valueOf(hotel.getTotalRooms()));
        statGuests       = new JLabel(String.valueOf(hotel.getTotalGuests()));
        statStaff        = new JLabel(String.valueOf(hotel.getTotalStaff()));
        statReservations = new JLabel(String.valueOf(hotel.getReservations().size()));
        statPayments     = new JLabel(String.valueOf(hotel.getPayments().size()));

        p.add(statLine("Rooms",        statRooms));
        p.add(statLine("Guests",       statGuests));
        p.add(statLine("Staff",        statStaff));
        p.add(statLine("Reservations", statReservations));
        p.add(statLine("Payments",     statPayments));

        JPanel wrapper=new JPanel(new BorderLayout());wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0,14,0,14));
        JPanel sep=new JPanel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setPaint(new GradientPaint(0,0,Theme.PINK_DIM,getWidth(),0,new Color(0,0,0,0)));
                g2.fillRect(0,0,getWidth(),1);g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(0,1));sep.setOpaque(false);
        wrapper.add(sep,BorderLayout.NORTH);wrapper.add(p,BorderLayout.CENTER);
        return wrapper;
    }

    public void refreshStats() {
        if (statRooms == null) return;
        statRooms       .setText(String.valueOf(hotel.getTotalRooms()));
        statGuests      .setText(String.valueOf(hotel.getTotalGuests()));
        statStaff       .setText(String.valueOf(hotel.getTotalStaff()));
        statReservations.setText(String.valueOf(hotel.getReservations().size()));
        statPayments    .setText(String.valueOf(hotel.getPayments().size()));
    }

    private JPanel statLine(String label, JLabel val) {
        JPanel row=new JPanel(new BorderLayout());row.setOpaque(false);
        JLabel lbl=new JLabel(label);lbl.setFont(Theme.FONT_SMALL);lbl.setForeground(Theme.TEXT_DIM);
        val.setFont(new Font("SansSerif",Font.BOLD,12));val.setForeground(Theme.PINK_SOFT);
        row.add(lbl,BorderLayout.WEST);row.add(val,BorderLayout.EAST);
        return row;
    }

    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG);

        String role = currentUser.getRole();

        contentArea.add(new RoomsPanel(hotel,  role), NAV_ROOMS);
        contentArea.add(new GuestsPanel(hotel, role), NAV_GUESTS);

        // Staff panel — only added for Manager (Receptionist never navigates here,
        // but we add it unconditionally so CardLayout doesn't throw on show())
        contentArea.add(new StaffPanel(hotel, role), NAV_STAFF);

        // Create Bookings before Reservations so the reference can be set
        bookingsPanel     = new BookingsPanel(hotel);
        reservationsPanel = new ReservationsPanel(hotel);

        // Wire: ReservationsPanel.bookNow() pushes into BookingsPanel
        reservationsPanel.setBookingsPanel(bookingsPanel);

        contentArea.add(reservationsPanel, NAV_RESERVATIONS);
        contentArea.add(bookingsPanel,     NAV_BOOKINGS);

        paymentsPanel = new PaymentsPanel(hotel);
        contentArea.add(paymentsPanel, NAV_PAYMENTS);

        cardLayout.first(contentArea);
        return contentArea;
    }

    private void handleLogout() {
        int ok=JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?","Logout",
            JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(ok==JOptionPane.YES_OPTION){
            try{currentUser.logout();}catch(Exception ignored){}
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}