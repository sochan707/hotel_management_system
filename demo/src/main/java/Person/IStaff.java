package Person;

public interface IStaff {
    String getId();
    String getName();
    String getGender();
    String getPhone();
    String getPosition();
    boolean isActive();
    
    boolean can(String action);
}
