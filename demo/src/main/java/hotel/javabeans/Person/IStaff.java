package hotel.javabeans.Person;

public interface IStaff {
    String getId();
    String getFirstName();
    String getLastName();
    String getGender();
    String getPhone();
    String getPosition();
    boolean isActive();
    boolean can(String action);
}