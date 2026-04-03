package hotel.javabeans.Person;

public abstract class Person {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String gender;
    protected String phone;
  
    
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
       
    
    public Person(String id, String firstName, String lastName,String gender, String phone) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setPhone(phone);
    }
    
    public String getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
        
    // `position` belongs to Staff subclasses, not base Person via this class.
    
     protected void setId(String id) {
        if(id != null && !id.isEmpty()) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Staff ID cannot be null or  empty.");
        }
    }
    
    protected void setFirstName(String firstName) {
        if(firstName != null && !firstName.isEmpty()) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("Firstname cannot be null or empty.");
        }
    }
    protected void setLastName(String lastName) {
        if(lastName != null && !lastName.isEmpty()) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("Lastname cannot be null or empty.");
        }
    }
    protected void setGender(String gender) {
         if(gender != null && !gender.isEmpty()) {
            if(gender.equalsIgnoreCase(MALE) || gender.equalsIgnoreCase(FEMALE)) {
                this.gender = gender;
            } else {
                throw new IllegalArgumentException("Gender must be 'Male' or 'Female'.");
            }
        } else {
            throw new IllegalArgumentException("Gender cannot be null or empty.");
        }
    }
    
    protected void setPhone(String phone) {
         if(phone != null && !phone.isEmpty()) {
            if(phone.length() >= 9 && phone.length() <= 15) {
                this.phone = phone;
            } else {
                throw new IllegalArgumentException("Phone number must be between 9 and 15 characters.");
            }
        } else {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
    }
    

   
}
