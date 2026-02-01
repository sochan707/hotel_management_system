package hotel.javabeans;

public class Room {
    
    public static String SINGLE = "Single";
    public static String DOUBLE = "Double";
    public static String TRIPLE = "Triple";
    
   
    public static String AVAILABLE = "Available";
    public static String BOOKED = "Booked";
    public static String OCCUPIED = "Occupied";
    
    private String roomNumber;
    private String roomType;
    private double price;
    private String status;
    
    public Room(String roomNumber, String roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.status = AVAILABLE;
    
}
public String getRoomNumber() {
        return roomNumber;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}