package hotel.javabeans.Person;

public abstract class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;

    public static final String MALE   = "Male";
    public static final String FEMALE = "Female";

    public Person(String id, String firstName, String lastName, String gender, String phone) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setPhone(phone);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getId()        { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getGender()    { return gender; }
    public String getPhone()     { return phone; }
// ── Setters with validation ───────────────────────────────────────────────
    protected void setId(String id) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("ID cannot be null or empty.");
        this.id = id.trim();
    }

    protected void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty())
            throw new IllegalArgumentException("First name cannot be null or empty.");
        this.firstName = firstName.trim();
    }

    protected void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty())
            throw new IllegalArgumentException("Last name cannot be null or empty.");
        this.lastName = lastName.trim();
    }

    protected void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty())
            throw new IllegalArgumentException("Gender cannot be null or empty.");
        String g = gender.trim();
        if (g.equalsIgnoreCase(MALE) || g.equalsIgnoreCase(FEMALE)) {
            this.gender = g.substring(0, 1).toUpperCase() + g.substring(1).toLowerCase();
        } else {
            throw new IllegalArgumentException("Gender must be 'Male' or 'Female'.");
        }
    }

    protected void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        String p = phone.trim();
        if (p.length() < 9 || p.length() > 15)
            throw new IllegalArgumentException("Phone number must be between 9 and 15 characters.");
        this.phone = p;
    }
}