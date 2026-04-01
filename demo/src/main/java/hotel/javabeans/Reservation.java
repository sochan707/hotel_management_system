package hotel.javabeans;

import java.time.LocalDate;

public class Reservation {
        public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE
    }

    private String guestName;
    private String phone;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double estimatedPrice;
    private boolean confirmed = false;


    public Reservation(String guestName, String phone, RoomType roomType,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       double estimatedPrice) {
        setGuestName(guestName);
        setPhone(phone);
        setRoomType(roomType);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setEstimatedPrice(estimatedPrice);
    }

    //private validation
    private RoomType validateRoomType(String roomType) {
    if(roomType == null) {
        throw new IllegalArgumentException("Room type cannot be null.");
    }
    
    switch(roomType.trim().toUpperCase()) {
        case "SINGLE": return RoomType.SINGLE;
        case "DOUBLE": return RoomType.DOUBLE;
        case "TRIPLE": return RoomType.TRIPLE;
        default:
            throw new IllegalArgumentException("Invalid room type: " + roomType);
    }
    }


    
    // Setters with validation
    
public void setGuestName(String guestName){
        if(guestName != null && !guestName.trim().isEmpty()){
            this.guestName = guestName;
        }else{
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
    }

    public void setPhone(String phone){
        if(phone != null && !phone.trim().isEmpty()){
            this.phone = phone;
        }else{
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
    }
    // accepts only enum
    public void setRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
            this.roomType = roomType;
    }
    // accept string from user input
    public void setRoomType(String roomTypeStr) {
        this.roomType = validateRoomType(roomTypeStr);
    }

    public void setCheckInDate(LocalDate checkInDate){
        if (checkInDate == null)
            throw new IllegalArgumentException("Check-in cannot be null");
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate))
            throw new IllegalArgumentException("Check-in must be before check-out");
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate){
        if (checkOutDate == null)
            throw new IllegalArgumentException("Check-out cannot be null");
        if (checkInDate != null && checkOutDate.isBefore(checkInDate))
            throw new IllegalArgumentException("Check-out must be after check-in");
        this.checkOutDate = checkOutDate;
    }
    public void setEstimatedPrice(double estimatedPrice) {
        if (estimatedPrice <= 0) throw new IllegalArgumentException("Price must be positive");
        this.estimatedPrice = estimatedPrice;
    }


    // Getters
 
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public RoomType getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getEstimatedPrice() { return estimatedPrice; }
    public boolean isConfirmed() { return confirmed; }
}