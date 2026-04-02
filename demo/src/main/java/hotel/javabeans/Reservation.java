package hotel.javabeans;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {

    public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE
    }

    public static final double DEPOSIT_RATE = 0.4;

    private final String reservationId;
    private String guestName;
    private String phone;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double estimatedPrice;
    private double depositAmount;
    private boolean depositPaid = false;
    private String depositPaymentId;
    private boolean confirmed = false;



    public Reservation(String guestName, String phone, RoomType roomType,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       double estimatedPrice) {
        this.reservationId = UUID.randomUUID().toString();
        setGuestName(guestName);
        setPhone(phone);
        setRoomType(roomType);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setEstimatedPrice(estimatedPrice); // also calls computeDeposit()
    }

    // Private helpers

    private void computeDeposit() {
        this.depositAmount = roundTwoDecimals(this.estimatedPrice * DEPOSIT_RATE);
    }

    private static double roundTwoDecimals(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private RoomType validateRoomType(String roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        switch (roomType.trim().toUpperCase()) {
            case "SINGLE": return RoomType.SINGLE;
            case "DOUBLE": return RoomType.DOUBLE;
            case "TRIPLE": return RoomType.TRIPLE;
            default:
                throw new IllegalArgumentException("Invalid room type: " + roomType);
        }
    }

    
    // Setters with validation

    public void setGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        }
        this.guestName = guestName.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        this.phone = phone.trim();
    }

    // Accepts an enum value directly.
    public void setRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        this.roomType = roomType;
    }

    //Accepts a raw string from user input and converts it to the enum. 
    public void setRoomType(String roomTypeStr) {
        this.roomType = validateRoomType(roomTypeStr);
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null.");
        }
        if (checkOutDate != null && checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check-out date cannot be null.");
        }
        if (checkInDate != null && checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        this.checkOutDate = checkOutDate;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        if (estimatedPrice <= 0) {
            throw new IllegalArgumentException("Estimated price must be positive.");
        }
        this.estimatedPrice = estimatedPrice;
        computeDeposit(); // keep depositAmount in sync
    }

    //
    public void markDepositPaid(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required to mark deposit as paid.");
        }
        this.depositPaid = true;
        this.depositPaymentId = paymentId;
    }

    public void confirm() {
        if (!depositPaid) {
            throw new IllegalStateException("Cannot confirm reservation: deposit has not been paid.");
        }
        this.confirmed = true;
    }

    public double getRemainingBalance() {
        return roundTwoDecimals(estimatedPrice - depositAmount);
    }


    // Getters

    public String getReservationId()    { return reservationId; }
    public String getGuestName()        { return guestName; }
    public String getPhone()            { return phone; }
    public RoomType getRoomType()       { return roomType; }
    public LocalDate getCheckInDate()   { return checkInDate; }
    public LocalDate getCheckOutDate()  { return checkOutDate; }
    public double getEstimatedPrice()   { return estimatedPrice; }
    public double getDepositAmount()    { return depositAmount; }
    public boolean isDepositPaid()      { return depositPaid; }
    public String getDepositPaymentId() { return depositPaymentId; }
    public boolean isConfirmed()        { return confirmed; }

    
    // toString 

    @Override
    public String toString() {
        return "Reservation{" +
               "id='" + reservationId + '\'' +
               ", guest='" + guestName + '\'' +
               ", room=" + roomType +
               ", checkIn=" + checkInDate +
               ", checkOut=" + checkOutDate +
               ", estimatedPrice=" + estimatedPrice +
               ", depositAmount=" + depositAmount +
               ", depositPaid=" + depositPaid +
               ", confirmed=" + confirmed +
               '}';
    }
}