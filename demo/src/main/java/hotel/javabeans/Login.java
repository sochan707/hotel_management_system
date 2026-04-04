package hotel.javabeans;

/**
 * Abstract base class for authenticated users.
 * Subclasses define role-specific behaviour (Staff, Guest, etc.).
 */
public abstract class Login {

    // ── Custom exception ──────────────────────────────────────────────────────
    public static class LoginException extends RuntimeException {
        public LoginException(String message) { super(message); }
    }

    // ── Fields ────────────────────────────────────────────────────────────────
    private String  username;
    private String  password;
    private boolean loggedIn;
    private int     failedAttempts;
    private static final int MAX_ATTEMPTS = 5;

    // ── Constructor ───────────────────────────────────────────────────────────
    public Login(String username, String password) {
        setUsername(username);
        setPassword(password);
        this.loggedIn      = false;
        this.failedAttempts = 0;
    }

    // ── Abstract role API ─────────────────────────────────────────────────────
    public abstract String  getRole();
    public abstract boolean hasAccess();
    public abstract String  getRolePermissions();

    // ── Core auth ─────────────────────────────────────────────────────────────

    /**
     * Tries to authenticate with the supplied credentials.
     *
     * @throws LoginException if the account is locked due to too many failures
     */
    public boolean authenticate(String username, String password) {
        if (failedAttempts >= MAX_ATTEMPTS) {
            throw new LoginException(
                "Account locked after " + MAX_ATTEMPTS + " failed attempts. Contact an administrator.");
        }
        if (username == null || password == null) {
            failedAttempts++;
            return false;
        }
        if (this.username.equals(username.trim()) && this.password.equals(password)) {
            this.loggedIn      = true;
            this.failedAttempts = 0;
            return true;
        }
        failedAttempts++;
        return false;
    }

    /**
     * Logs the current user out.
     *
     * @throws LoginException if no user is logged in
     */
    public void logout() {
        if (!loggedIn) throw new LoginException("No user is currently logged in.");
        this.loggedIn = false;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public boolean isLoggedIn()       { return loggedIn; }
    public String  getUsername()      { return username; }
    public int     getFailedAttempts(){ return failedAttempts; }
    public boolean isLocked()         { return failedAttempts >= MAX_ATTEMPTS; }

    // ── Setters with validation ───────────────────────────────────────────────
    public void setUsername(String username) {
        if (username == null || username.trim().length() < 3)
            throw new LoginException("Username must be at least 3 characters long.");
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6)
            throw new LoginException("Password must be at least 6 characters long.");
        this.password = password;
    }

    /** Allows changing the password after verifying the old one. */
    public void changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword))
            throw new LoginException("Current password is incorrect.");
        setPassword(newPassword);
    }

    public void resetFailedAttempts() { this.failedAttempts = 0; }
}