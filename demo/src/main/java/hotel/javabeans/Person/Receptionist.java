package hotel.javabeans.Person;

public class Receptionist extends Staff {

    public Receptionist(String id, String firstName, String lastName, String gender, String phone) {
        super(id, firstName, lastName, gender, phone, "Receptionist");
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;
        String act = action.toLowerCase().trim();
        return switch (act) {
            case "checkin", "checkout", "bookroom", "cancelbooking",
                 "viewbooking", "viewguest", "viewroom" -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return "Receptionist{id='" + getId() + "', name='" + getFirstName() + " " + getLastName() +
               "', phone='" + getPhone() + "', gender='" + getGender() +
               "', position='Receptionist', active=" + isActive() + '}';
    }
}