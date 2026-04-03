package hotel.javabeans;

import java.time.LocalDate;

import hotel.javabeans.payment.Payment; 

public class Invoice {
    private final String invoiceId;
    private final String bookingId;
    private RoomType typeOfRoom;
    private double roomCharges;
    private int numberOfRooms;
    private int numberOfNights;
    private int numberOfGuests;
    private double extraPersonCharge;
    private double discount;
    private double taxAmount;
    private double depositApplied;
    private double totalAmount;
    private boolean isPaid;
    private LocalDate issueDate;

    private Payment payment;

    public static final double TAX_RATE = 0.1;

    public Invoice(String invoiceId, RoomType typeOfRoom, double roomCharges, int numberOfRooms, int numberOfNights, int numberOfGuests, double totalAmount, boolean isPaid, LocalDate issueDate, Payment payment) {
        this.invoiceId = validateInvoiceId(invoiceId);
        this.typeOfRoom = typeOfRoom;
        this.roomCharges = validatePositive(roomCharges, "Room charge");
        this.numberOfRooms = validatePositive(numberOfRooms, "Number of room");
        this.numberOfNights = validatePositive(numberOfNights, "Number of night");
        this.numberOfGuests = validatePositive(numberOfGuests, "Number of guest");

        setExtraPersonCharge(numberOfGuests, typeOfRoom);
        setDiscount(totalAmount);
        setTotalAmount(totalAmount);
        this.isPaid = isPaid;
        this.issueDate = LocalDate.now();
        this.payment = payment;
    }

    public enum RoomType { SINGLE, DOUBLE, TRIPLE }

    // ====================================== VALIDATION ==========================================

    private String validateInvoiceId (String invoiceId){
        if(invoiceId == null && invoiceId.isEmpty())
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        return "IV" + invoiceId;
    }

    // =================================== OVERLOADING ==========================================

    private int validatePositive(int value, String fieldName) {
        if (value <= 0) throw new IllegalArgumentException(fieldName + " must be positive.");
        return value;
    }

    private double validatePositive(double value, String fieldName) {
        if (value <= 0) throw new IllegalArgumentException(fieldName + " must be positive.");
        return value;
    }
    //=============================================================================================

    // ==================================== SETTER ========================================

    public void setTypeOfRoom(RoomType typeOfRoom) { // incase guest wanna change room
        if (typeOfRoom == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        this.typeOfRoom = typeOfRoom;
    }

    public void setRoomCharges(double roomCharges) { // holiday ey lerng tlai room jg oy staff input tv 
        if(roomCharges >= 0.00) {
            this.roomCharges = roomCharges;
        } else {
            throw new IllegalArgumentException("Room charges cannot be negative.");
        }
    }

    public void setNumberOfRooms(int numberOfRooms) { // handle when guess want to cancal or add rooms
        if(numberOfRooms > 0) {
            this.numberOfRooms = numberOfRooms;
        } else {
            throw new IllegalArgumentException("Number of rooms cannot be negative or zero.");
        }
    }

    public void setNumberOfNights(int numberOfNights) { // handle when guest want to extent the stay
        if(numberOfNights > 0) {
            this.numberOfNights = numberOfNights;
        } else {
            throw new IllegalArgumentException("Number of nights cannot be negative or zero.");
        }
    }

    public void setNumberOfGuests(int numberOfGuests) { // unexpect guest come
        if(numberOfGuests > 0) {
            this.numberOfGuests = numberOfGuests;
        } else {
            throw new IllegalArgumentException("Number of guests cannot be negative or zero.");
        }
    }

    public void setExtraPersonCharge(int numberOfGuests, RoomType typeOfRoom) {
        int includedGuests = typeOfRoom == RoomType.SINGLE ? 1 : typeOfRoom == RoomType.DOUBLE ? 2 : 3;
        this.extraPersonCharge = numberOfGuests > includedGuests ? (numberOfGuests - includedGuests) * 10.00 : 0.00;
    }

    public void setDiscount(double totalAmount) {
        if (totalAmount < 0) 
            throw new IllegalArgumentException("Invalid discount.");
        this.discount = Math.min(1.00, totalAmount);  // Max discount of 100%
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = (roomCharges * numberOfRooms * numberOfNights) + extraPersonCharge - discount;
    }

    // ==================================== GETTER ========================================

    public String getInvoiceId() {
        return invoiceId;
    }

    public RoomType getTypeOfRoom() {
        return typeOfRoom;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public double getExtraPersonCharge() {
        return extraPersonCharge;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean getIsPaid() {
        return isPaid;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        return String.format("Invoice ID: %s\nRoom Type: %s\nTotal Amount: $%.2f\nIs Paid: %s\n%s",
                invoiceId, typeOfRoom, totalAmount, isPaid ? "Yes" : "No", payment != null ? payment : "No payment info.");
    }
    
}
