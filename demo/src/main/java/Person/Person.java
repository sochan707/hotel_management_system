package Person;

public abstract class Person {
    protected String id;
    protected String name;
    protected String gender;
    protected String phone;
    protected String position;
    
    protected static final String MALE = "Male";
    protected static final String FEMALE = "Female";
       
    
    public Person(String id, String name,String gender, String phone,String position) {
        setId(id);
        setName(name);
        setGender(gender);
        setPhone(phone);
        setPosition(position);
        
        
        
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
        
    public String getPosition() {
        return position;
    }
    
     public void setId(String id) {
        if(id != null && !id.isEmpty()) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Staff ID cannot be null or  empty.");
        }
    }
    
    public void setName(String name) {
        if(name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("taff name cannot be null or empty.");
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
    
    public void setPosition(String position) {
         if(position != null && !position.isEmpty()) {

            this.position=position;
         } else {
             throw new IllegalArgumentException("Position cannot be null or empty.");
         }
    }
    
             // ====== toString ======
    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + id + '\'' +
                ", fullName='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
