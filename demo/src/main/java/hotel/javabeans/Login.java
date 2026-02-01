package hotel.javabeans;

public class Login {
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_GUEST = "Guest";
    
    private String username;
    private String password;
    private String role;
    private boolean isLoggedIn;
    
    public Login(String username, String password, String role) {
        setUsername(username);
        setPassword(password);
        setRole(role);
        this.isLoggedIn = false;
    }
    
    // Getters
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getRole() {
        return role;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    // Setters 
    public void setUsername(String username) {
        if(username != null && !username.isEmpty()) {
            if(username.length() >= 3) {
                this.username = username;
            } else {
                throw new IllegalArgumentException("Username must be at least 3 characters long.");
            }
        } else {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
    }
    
    public void setPassword(String password) {
        if(password != null && !password.isEmpty()) {
            if(password.length() >= 6) {
                this.password = password;
            } else {
                throw new IllegalArgumentException("Password must be at least 6 characters long.");
            }
        } else {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
    }
    
    public void setRole(String role) {
        if(role != null && !role.isEmpty()) {
            if(role.equals(ROLE_STAFF) || role.equals(ROLE_GUEST)) {
                this.role = role;
            } else {
                throw new IllegalArgumentException("Role must be 'Staff' or 'Guest'.");
            }
        } else {
            throw new IllegalArgumentException("Role cannot be null or empty.");
        }
    }
    
    // Authentication 
    public boolean authenticate(String username, String password) {
        if(this.username.equals(username) && this.password.equals(password)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.isLoggedIn = false;
    }
    
    public boolean hasStaffAccess() {
        return isLoggedIn && role.equals(ROLE_STAFF);
    }
    
    public boolean hasGuestAccess() {
        return isLoggedIn && role.equals(ROLE_GUEST);
    }
    
    public String getRolePermissions() {
        if(!isLoggedIn) {
            return "Not logged in";
        }
        
        if(role.equals(ROLE_STAFF)) {
            return "Staff Access: Full management permissions - can manage rooms, reservations, guests, invoices, and payments";
        } else if(role.equals(ROLE_GUEST)) {
            return "Guest Access: Limited permissions - can view own reservations and make bookings";
        }
        return "Unknown role";
    }
}