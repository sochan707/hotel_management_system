package hotel.javabeans.Person;
public class Manager extends Staff{
   
    public Manager(String id, String firstName, String lastName,String gender, String phone) {
       super(id, firstName, lastName, gender, phone, "Manager")

    }
    @Override
    public boolean can( String action) {
        return true;    // manager tver ey bn tang os
    }
    
     @Override
    public String toString() {
        return "Manager{" +
                "id='" + getId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", position='Manager'" +
                ", active=" + isActive() +
                '}';
    }
    
   
}