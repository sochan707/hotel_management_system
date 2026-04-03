package hotel.javabeans.Person;
public abstract class Staff extends Person implements IStaff{
   private String position;
   private boolean active = true;
    
    
    public Staff (String id, String firstName, String lastName, String gender, String phone , String position) {
        super(id, firstName, lastName, gender, phone);
        setPosition(position);
    }

    protected void setPosition (String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException ("Position cannot be null or blank");
        }
        if (!position.trim().equalsIgnoreCase("Manager") && !position.trim().equalsIgnoreCase("Receptionist")) {
            throw new IllegalArgumentException ("Position must be either 'Manager' or 'Receptionist'.");
        } 
       this.position = position.trim().equalsIgnoreCase("Manager") ? "Manager" : "Receptionist";
            
        
    }
    public String getPosition() {
        return position;
    }
    @Override
    public boolean isActive() {
        return active;
    }
    @Override
public boolean can(String action) {
    if (position == null) {
        return false;
    }
    String act = action.toLowerCase().trim();

    // manager can do everything
    if (position.equals("Manager")) {
        return true;                   
    }

    // If it's a Receptionist
   if (position.equals("Receptionist")) {
    
    if (act.equals("checkin") || 
        act.equals("checkout") || 
        act.equals("bookroom") || 
        act.equals("cancelbooking") || 
        act.equals("viewbooking") || 
        act.equals("viewguest") || 
        act.equals("viewroom")) {
        
        return true;
    } else {
        return false;
    }
}

    return false;   // Default: no permission
}




             // ====== toString ======
    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + getId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", gender='" + getGender() + '\'' +
                ", position='" + position + '\'' +
                ", active=" + isActive() +
                '}';
    }
}