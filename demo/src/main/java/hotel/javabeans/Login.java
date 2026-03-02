package hotel.javabeans;

public abstract class Login {
    private String username;
    private String password;
    private boolean isLoggedIn;

    public Login(String username, String password) {
        setUsername(username);
        setPassword(password);
        this.isLoggedIn = false;
    }

    // Abstract methods — subclasses define role-specific behavior
    public abstract String getRole();
    public abstract boolean hasAccess();
    public abstract String getRolePermissions();

    public boolean authenticate(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() { this.isLoggedIn = false; }

    public boolean isLoggedIn()    { return isLoggedIn; }
    public String getUsername()    { return username; }

    public void setUsername(String username) {
        if (username != null && username.length() >= 3) this.username = username;
        else throw new IllegalArgumentException("Username must be at least 3 characters long.");
    }
    public void setPassword(String password) {
        if (password != null && password.length() >= 6) this.password = password;
        else throw new IllegalArgumentException("Password must be at least 6 characters long.");
    }
}