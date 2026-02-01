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
        setRoomNumber(roomNumber);
        setRoomType(roomType);
        setPrice(price);
        this.status = AVAILABLE; // Default status
    
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

     public void setRoomNumber(String roomNumber) {
        if(roomNumber != null && !roomNumber.isEmpty()) {
            this.roomNumber = roomNumber;
        } else {
            throw new IllegalArgumentException("Room number cannot be null or empty.");
        }
    }
    public void setRoomType(String roomType) {
         if(roomType != null && !roomType.isEmpty()) {
            if(roomType.equals(SINGLE) || roomType.equals(DOUBLE) || 
               roomType.equals(TRIPLE)) {
                this.roomType = roomType;
            } else {
                throw new IllegalArgumentException("Room type must be Single, Double, or Triple.");
            }
        } else {
            throw new IllegalArgumentException("Room type cannot be null or empty.");
        }
    }
    
    public void setPrice(double price) {
        if(price > 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
    }
    
    public void setStatus(String status) {
         if(status != null && !status.isEmpty()) {
            if(status.equals(AVAILABLE) || status.equals(BOOKED) || 
               status.equals(OCCUPIED)) {
                this.status = status;
            } else {
                throw new IllegalArgumentException("Status must be Available, Booked, or Occupied.");
            }
        } else {
            throw new IllegalArgumentException("Status cannot be null or empty.");
        }
    }
}