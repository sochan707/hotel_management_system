package hotel.javabeans;

import java.util.Scanner;

public class LoginDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Create some sample login accounts
        Login staffLogin = new Login("admin", "admin123", Login.ROLE_STAFF);
        Login guestLogin = new Login("guest01", "guest123", Login.ROLE_GUEST);
        
        System.out.println("=== Hotel Management System Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Try to authenticate
        Login currentUser = null;
        
        if(staffLogin.authenticate(username, password)) {
            currentUser = staffLogin;
            System.out.println("\n✓ Login successful!");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println(currentUser.getRolePermissions());
            
            if(currentUser.hasStaffAccess()) {
                System.out.println("\n--- Staff Menu ---");
                System.out.println("1. Manage Rooms");
                System.out.println("2. Manage Reservations");
                System.out.println("3. Manage Guests");
                System.out.println("4. View Invoices");
                System.out.println("5. Process Payments");
                System.out.println("6. Logout");
            }
            
        } else if(guestLogin.authenticate(username, password)) {
            currentUser = guestLogin;
            System.out.println("\n Login successful!");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println(currentUser.getRolePermissions());
            
            if(currentUser.hasGuestAccess()) {
                System.out.println("\n--- Guest Menu ---");
                System.out.println("1. View My Reservations");
                System.out.println("2. Make New Booking");
                System.out.println("3. View Available Rooms");
                System.out.println("4. Logout");
            }
            
        } else {
            System.out.println("\n Login failed! Invalid username or password.");
        }
        
        // Example: Logout
        if(currentUser != null) {
            System.out.print("\nDo you want to logout? (yes/no): ");
            String response = scanner.nextLine();
            if(response.equalsIgnoreCase("yes")) {
                currentUser.logout();
                System.out.println("Logged out successfully!");
                System.out.println("Logged in status: " + currentUser.isLoggedIn());
            }
        }
        
        scanner.close();
    }
}