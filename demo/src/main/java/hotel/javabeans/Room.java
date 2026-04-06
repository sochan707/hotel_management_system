package hotel.javabeans;

public class Room {
   
    public static final String AVAILABLE = "Available";
    public static final String BOOKED = "Booked";
    public static final String OCCUPIED = "Occupied";
    
    private String roomNumber;
    private TypeOfRoom TypeOfRoom;
    private double price;
    private String status;
    
    public Room(String roomNumber, TypeOfRoom TypeOfRoom, double price) {
        setRoomNumber(roomNumber);
        setTypeOfRoom(TypeOfRoom);
        setPrice(price);
        this.status = AVAILABLE; // Default status
    
}
public String getRoomNumber() {
        return roomNumber;
    }
    
    public TypeOfRoom getTypeOfRoom() {
        return TypeOfRoom;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getStatus() {
        return status;
    }

    public void setRoomNumber(String roomNumber) {
    if (roomNumber == null || roomNumber.trim().isEmpty()) {
        throw new IllegalArgumentException("Room number cannot be null or empty.");
    }
    
    this.roomNumber = roomNumber.trim();
}
 public void setTypeOfRoom(TypeOfRoom TypeOfRoom) {
        if (TypeOfRoom == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        this.TypeOfRoom = TypeOfRoom;
    }
    
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        
        this.price = price;
    }
    
    
    public void setStatus(String status) {
         if(status != null && !status.trim().isEmpty()) {
            if(status.equals(AVAILABLE) || status.equals(BOOKED) || 
               status.equals(OCCUPIED)) {
                this.status = status;
            } else {
                throw new IllegalArgumentException("Status must be Available, Booked, or Occupied.");
            }
        } else {
            throw new IllegalArgumentException("Status cannot be empty.");
        }
    }
    public boolean isAvailable() {
        return AVAILABLE.equals(status);
    }

    public boolean isBooked() {
        return BOOKED.equals(status);
    }

    public boolean isOccupied() {
        return OCCUPIED.equals(status);
    }
    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", TypeOfRoom=" + (TypeOfRoom != null ? TypeOfRoom.getDisplayName() : "N/A") +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}