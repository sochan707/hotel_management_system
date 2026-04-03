package hotel.javabeans.Person;

public abstract class Person {
   private String id;
   private String firstName;
   private String lastName;
   private String gender;
   private String phone;
  
    
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
    
     protected void setId(String id) {
        if(id != null && !id.trim().isEmpty()) {
            this.id = id.trim();
        } else {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
    }
    
    protected void setFirstName(String firstName) {
        if(firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        } else {
            throw new IllegalArgumentException("Firstname cannot be null or empty.");
        }
    }
    protected void setLastName(String lastName) {
        if(lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        } else {
            throw new IllegalArgumentException("Lastname cannot be null or empty.");
        }
    }
    protected void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty.");
        }
        String g = gender.trim();
        if (g.equalsIgnoreCase(MALE) || g.equalsIgnoreCase(FEMALE)) {
            this.gender = g;
        } else {
            throw new IllegalArgumentException("Gender must be 'Male' or 'Female'.");
        }
    }
    
    protected void setPhone(String phone) {
         if(phone != null && !phone.trim().isEmpty()) {
            if(phone.length() >= 9 && phone.length() <= 15) {
                this.phone = phone.trim();
            } else {
                throw new IllegalArgumentException("Phone number must be between 9 and 15 characters.");
            }
        } else {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
    }
    

   
}
