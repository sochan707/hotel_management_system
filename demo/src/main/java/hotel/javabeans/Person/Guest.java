package hotel.javabeans.Person;

public class Guest extends Person {

    public Guest(String id, String firstName, String lastName, String gender, String phone) {
        super(id, firstName, lastName, gender, phone);
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id='"        + getId()        + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='"  + getLastName()  + '\'' +
                ", gender='"    + getGender()    + '\'' +
                ", phone='"     + getPhone()     + '\'' +
                '}';
    }
}