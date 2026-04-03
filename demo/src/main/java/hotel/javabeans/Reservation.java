package hotel.javabeans;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class Reservation {
    public enum RoomType {
        SINGLE,
        DOUBLE,
        TRIPLE;

        public static RoomType from(String value) { // from() method converts a String input into a valid RoomType enum constant.
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Room type cannot be null or empty.");
            }
            switch (value.trim().toUpperCase()) {
                case "SINGLE": return SINGLE;
                case "DOUBLE": return DOUBLE;
                case "TRIPLE": return TRIPLE;
                default:
                    throw new IllegalArgumentException("Invalid room type: " + value);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    public static final double DEPOSIT_RATE = 0.4; // luy kok 40%

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final String reservationId;
    private final String guestName;
    private String phone;
    private Map<RoomType, Integer> roomTypes = new EnumMap<>(RoomType.class);
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double estimatedPrice;
    private double depositAmount;
    private boolean depositPaid = false;
    private String depositPaymentId;
    private boolean confirmed = false;
    private LocalDate reservationDate;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Reservation(String guestName, String phone,
                       Map<RoomType, Integer> roomTypes,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       double estimatedPrice) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = validateGuestName(guestName);
        setPhone(phone);
        setRoomTypes(roomTypes);
        setCheckInDate(checkInDate);
        setCheckOutDate(checkOutDate);
        setEstimatedPrice(estimatedPrice);
        this.reservationDate = LocalDate.now();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void computeDeposit() {
        this.depositAmount = round2(this.estimatedPrice * DEPOSIT_RATE);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // -------------------------------------------------------------------------
    // Setters + Validation
    // -------------------------------------------------------------------------

    private String validateGuestName(String guestName){ // use private kom oy ke change name nhe nhai
        if (guestName == null || guestName.trim().isEmpty())
            throw new IllegalArgumentException("Guest name cannot be null or empty.");
        return guestName.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        this.phone = phone.trim();
    }

    // set 1 roomType or more yor marn room
    public void setRoomTypes(Map<RoomType, Integer> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            throw new IllegalArgumentException("Room types cannot be null or empty.");
        }
        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Room type key cannot be null.");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException(
                    "Quantity for room type " + entry.getKey() + " must be positive.");
            }
        }
        this.roomTypes = new EnumMap<>(roomTypes);
    }

    public void addRoomType(RoomType roomType, int quantity) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.roomTypes.put(roomType, this.roomTypes.getOrDefault(roomType, 0) + quantity);
    }

    public void removeRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        if (!this.roomTypes.containsKey(roomType)) {
            throw new IllegalArgumentException("Room type " + roomType + " is not in this reservation.");
        }
        this.roomTypes.remove(roomType);
        if (this.roomTypes.isEmpty()) {
            throw new IllegalStateException("Reservation must have at least one room type.");
        }
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
        computeDeposit();
    }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    public void markDepositPaid(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
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
        return round2(estimatedPrice - depositAmount);
    }

    public int getTotalRooms() {
        return roomTypes.values() //representing the quantity of rooms for each type
                        .stream()
                        .mapToInt(Integer::intValue) // ex: {1,2,3} => 1 , 2 , and 3
                        .sum(); // 1+2+3 = 6
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getReservationId()             { return reservationId; }
    public String getGuestName()                 { return guestName; }
    public String getPhone()                     { return phone; }
    public Map<RoomType, Integer> getRoomTypes() { return new EnumMap<>(roomTypes); }
    public LocalDate getCheckInDate()            { return checkInDate; }
    public LocalDate getCheckOutDate()           { return checkOutDate; }
    public double getEstimatedPrice()            { return estimatedPrice; }
    public double getDepositAmount()             { return depositAmount; }
    public boolean isDepositPaid()               { return depositPaid; }
    public String getDepositPaymentId()          { return depositPaymentId; }
    public boolean isConfirmed()                 { return confirmed; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Reservation{" +
               "reservationId='"   + reservationId  + '\'' +
               ", guestName='"     + guestName       + '\'' +
               ", phone='"         + phone           + '\'' +
               ", roomTypes="      + roomTypes       +
               ", totalRooms="     + getTotalRooms() +
               ", checkInDate="    + checkInDate     +
               ", checkOutDate="   + checkOutDate    +
               ", estimatedPrice=" + estimatedPrice  +
               ", depositAmount="  + depositAmount   +
               ", depositPaid="    + depositPaid     +
               ", confirmed="      + confirmed       +
               '}';
    }
}