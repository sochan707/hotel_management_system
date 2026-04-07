package hotel.javabeans.Person;

public abstract class Staff extends Person {

    private String position;
    private boolean active = true;

    public Staff(String id, String firstName, String lastName,
                 String gender, String phone, String position) {
        super(id, firstName, lastName, gender, phone);
        setPosition(position);
    }

    protected void setPosition(String position) {
        if (position == null || position.trim().isEmpty())
            throw new IllegalArgumentException("Position cannot be null or blank.");

        String pos = position.trim();
        if (!pos.equalsIgnoreCase("Manager") && !pos.equalsIgnoreCase("Receptionist"))
            throw new IllegalArgumentException("Position must be 'Manager' or 'Receptionist'.");

        this.position = pos.equalsIgnoreCase("Manager") ? "Manager" : "Receptionist";
    }

    public void setActive(boolean active) { this.active = active; }


    @Override
    public String toString() {
        return "Staff{id='" + getId() + "', name='" + getFirstName() + " " + getLastName() +
               "', phone='" + getPhone() + "', gender='" + getGender() +
               "', position='" + position + "', active=" + active + '}';
    }
}