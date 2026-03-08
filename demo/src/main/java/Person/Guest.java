package Person;
public class Guest {
    private String id;
    private String name;
    private String gender;
    private String phone;
    
    private static final String MALE = "Male";
    private static final String FEMALE = "Female";
    
    
    
    public Guest(String id, String name,String gender, String phone) {
       setId(id);
        setName(name);
        setGender(gender);
        setPhone(phone);
        
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
     public void setId(String id) {
        if(id != null && !id.isEmpty()) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Guest ID cannot be null or  empty.");
        }
    }
        
    public void setName(String name) {
       if(name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
    }
    public void setGender(String gender) {
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
    
    public void setPhone(String phone) {
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