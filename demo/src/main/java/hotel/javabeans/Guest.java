public class Guest {
    private String id;
    private String name;
    private String gender;
    private String phone;
    
    
    
    public Guest(String id, String name,String gender, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.gender=gender;
        
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
        
    public void setName(String name) {
        this.name = name;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
   
}