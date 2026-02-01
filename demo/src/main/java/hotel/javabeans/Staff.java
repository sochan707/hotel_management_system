public class Staff {
    private String id;
    private String name;
    private String gender;
    private String phone;
    private String position;
    
    
    
    public Staff(String id, String name,String gender, String phone,String position) {
        this.id = id;
        this.name = name;
        this.gender=gender;
        this.phone = phone;
        this.position = position;
        
        
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
        
    public void setName(String name) {
        this.name = name;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
   
}