package hotel.javabeans.Person;
public abstract class Staff extends Person implements IStaff{
    protected String position;
    protected boolean active = true;
    
    public Staff (String id, String firstName, String lastName, String gender, String phone , String position) {
        super(id, firstName, lastName, gender, phone)
        
    }

    protected void setPosition () {
        
    }
    public String getPosition() {
        return position
    };

             // ====== toString ======
    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + id + '\'' +
                ", fullName='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}