package hotel.javabeans.Person;

public class Manager extends Staff implements IStaff {

    public Manager(String id, String firstName, String lastName, String gender, String phone) {
        super(id, firstName, lastName, gender, phone, "Manager");
    }

    /** Manager can perform every action. */
    @Override
    public boolean can(String action) {
        return true;
    }

    @Override
    public String getPosition() {
        return "Manager";
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String toString() {
        return "Manager{id='" + getId() + "', name='" + getFirstName() + " " + getLastName() +
               "', phone='" + getPhone() + "', gender='" + getGender() +
               "', position='Manager', active=" + isActive() + '}';
    }
}