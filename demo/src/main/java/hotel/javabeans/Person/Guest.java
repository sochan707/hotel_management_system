package hotel.javabeans.Person;
public class Guest  extends person{
   
    
    public Guest(String id, String firstName, String lastName,String gender, String phone) {
       super (id, firstName, lastName, gender,phone);
       
        
    }
    
    @Override
    public String toString() {
        return "Guest{" +
                "GuestId='" + guestId + '\'' +
                ", FullName='" + firstName + '\'' +
                ", LastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                
                
                '}';
    }
   
}