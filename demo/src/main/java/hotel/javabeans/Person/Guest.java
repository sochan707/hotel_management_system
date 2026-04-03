package hotel.javabeans.Person;
<<<<<<< HEAD

public class Guest extends Person {

    public Guest(String id, String firstName, String lastName, String gender, String phone) {
        super(id, firstName, lastName, gender, phone);
=======
public class Guest  extends Person{
   
    
    public Guest(String id, String firstName, String lastName,String gender, String phone) {
       super (id, firstName, lastName, gender,phone);
       
        
>>>>>>> w7
    }

    @Override
    public String toString() {
        return "Guest{" +
<<<<<<< HEAD
                "id='" + getId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", phone='" + getPhone() + '\'' +
=======
                "GuestId='" + getId() + '\'' +
                ", FullName='" + getFirstName() + '\'' +
                ", LastName='" + getLastName() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", phone='" + getPhone() + '\'' +
                
                
>>>>>>> w7
                '}';
    }
}