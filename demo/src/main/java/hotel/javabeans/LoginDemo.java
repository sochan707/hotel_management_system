package hotel.javabeans;

import java.util.Scanner;

public class LoginDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Anonymous subclasses — no new files needed!
        Login staffLogin = new Login("admin", "admin123") {
            @Override public String getRole() { return "Staff"; }
            @Override public boolean hasAccess() { return isLoggedIn(); }
            @Override public String getRolePermissions() {
                return isLoggedIn()
                    ? "Staff Access: Full management permissions - can manage rooms, reservations, guests, invoices, and payments"
                    : "Not logged in";
            }
        };

        Login guestLogin = new Login("guest01", "guest123") {
            @Override public String getRole() { return "Guest"; }
            @Override public boolean hasAccess() { return isLoggedIn(); }
            @Override public String getRolePermissions() {
                return isLoggedIn()
                    ? "Guest Access: Limited permissions - can view own reservations and make bookings"
                    : "Not logged in";
            }
        };

        System.out.println("=== Hotel Management System Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Login currentUser = null;

        if (staffLogin.authenticate(username, password)) {
            currentUser = staffLogin;
        } else if (guestLogin.authenticate(username, password)) {
            currentUser = guestLogin;
        }

        if (currentUser != null) {
            System.out.println("\n✓ Login successful!");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println(currentUser.getRolePermissions());

            if (currentUser.getRole().equals("Staff")) {
                System.out.println("\n--- Staff Menu ---");
                System.out.println("1. Manage Rooms");
                System.out.println("2. Manage Reservations");
                System.out.println("3. Manage Guests");
                System.out.println("4. View Invoices");
                System.out.println("5. Process Payments");
                System.out.println("6. Logout");
            } else {
                System.out.println("\n--- Guest Menu ---");
                System.out.println("1. View My Reservations");
                System.out.println("2. Make New Booking");
                System.out.println("3. View Available Rooms");
                System.out.println("4. Logout");
            }

            System.out.print("\nDo you want to logout? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                currentUser.logout();
                System.out.println("Logged out successfully!");
                System.out.println("Logged in status: " + currentUser.isLoggedIn());
            }
        } else {
            System.out.println("\n✗ Login failed! Invalid username or password.");
        }

        scanner.close();
    }
}