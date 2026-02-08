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
    private String roomStatus;
    
    public Room(String roomNumber, String roomType, double price) {
        setRoomNumber(roomNumber);
        setRoomType(roomType);
        setPrice(price);
        this.roomStatus = AVAILABLE; // Default status
    
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
    
    public String getRoomStatus() {
        return roomStatus;
    }

     private void setRoomNumber(String roomNumber) {
        if(roomNumber != null && !roomNumber.isEmpty()) {
            this.roomNumber = roomNumber;
        } else {
            throw new IllegalArgumentException("Room number cannot be null or empty.");
        }
    }
    private void setRoomType(String roomType) {
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
    
    private void setPrice(double price) {
        if(price > 0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
    }
    
    private void setRoomStatus(String roomStatus) {
         if(roomStatus != null && !roomStatus.isEmpty()) {
            if(roomStatus.equals(AVAILABLE) || roomStatus.equals(BOOKED) || 
               roomStatus.equals(OCCUPIED)) {
                this.roomStatus = roomStatus;
            } else {
                throw new IllegalArgumentException("Status must be Available, Booked, or Occupied.");
            }
        } else {
            throw new IllegalArgumentException("Status cannot be empty.");
        }
    }
    public boolean isAvailable() {
       return AVAILABLE.equals(roomStatus);
    }

    public boolean isBooked() {
       return BOOKED.equals(roomStatus);
    }

    public boolean isOccupied() {
        return OCCUPIED.equals(roomStatus);
    }
}