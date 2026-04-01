package hotel.javabeans;

import hotel.javabeans.payment.Payment; 

public class Invoice {
    private final String invoiceId;
    private String typeOfRoom;
    private double roomCharges;
    private int numberOfRooms;
    private int numberOfNights;
    private int numberOfGuests;
    private double extraPersonCharge;
    private double discount;
    private double totalAmount;
    private boolean isPaid;

    private Payment payment;

    public Invoice(String invoiceId, String typeOfRoom, double roomCharges, int numberOfRooms, int numberOfNights, int numberOfGuests, double totalAmount, boolean isPaid, Payment payment) {
        this.invoiceId = validateInvoiceId(invoiceId);
        this.typeOfRoom = validateTypeOfRoom(typeOfRoom);
        this.roomCharges = validatePositive(roomCharges, "Room charge");
        this.numberOfRooms = validatePositive(numberOfRooms, "Number of room");
        this.numberOfNights = validatePositive(numberOfNights, "Number of night");
        this.numberOfGuests = validatePositive(numberOfGuests, "Number of guest");

        setExtraPersonCharge(numberOfGuests, typeOfRoom);
        setDiscount(totalAmount);
        setTotalAmount(totalAmount);
        setIsPaid(isPaid);
        this.payment = payment;
    }

    // ====================================== VALIDATION ==========================================

    private String validateInvoiceId (String invoiceId){
        if(invoiceId == null && invoiceId.isEmpty())
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        return "IV" + invoiceId;
    }

    private String validateTypeOfRoom (String typeOfRoom){
        if(!"Single".equals(typeOfRoom) || !"Double".equals(typeOfRoom) || !"Triple".equals(typeOfRoom))
            throw new IllegalArgumentException("Invalid room type.");
        return typeOfRoom;
    }

    private int validatePositive(int value, String fieldName) {
        if (value <= 0) throw new IllegalArgumentException(fieldName + " must be positive.");
        return value;
    }

    // =================================== OVERLOADING ==========================================
    private double validatePositive(double value, String fieldName) {
        if (value <= 0) throw new IllegalArgumentException(fieldName + " must be positive.");
        return value;
    }

    private double validateRoomCharges (double roomCharges){
        if(roomCharges < 0.00)
            throw new IllegalArgumentException("Room charges cannot be negative.");
        return roomCharges;
    }
    //=============================================================================================

    // private double extraPersonCharge(int numberOfGuests, String typeOfRoom){
    //     int includedGuests = 0;
    //     switch(typeOfRoom) {
    //         case "Single":
    //             includedGuests = 1;
    //             break;
    //         case "Double":
    //             includedGuests = 2;
    //             break;
    //         case "Triple":
    //             includedGuests = 3;
    //             break;
    //     }
    //     if(numberOfGuests > includedGuests) {
    //         return (numberOfGuests - includedGuests) * 10.00; // $10 per extra person
    //     } else {
    //         return 0.00;
    //     }
    // }

    // ==================================== SETTER ========================================

    public void setTypeOfRoom(String typeOfRoom) { // incase guest wanna change room
        if("Single".equals(typeOfRoom) || "Double".equals(typeOfRoom) || "Triple".equals(typeOfRoom)) {
            this.typeOfRoom = typeOfRoom;
        } else {
            throw new IllegalArgumentException("Invalid room type.");
        }
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

    public void setExtraPersonCharge(int numberOfGuests, String typeOfRoom) {
        int includedGuests = 0;
        switch(typeOfRoom) {
            case "Single":
                includedGuests = 1;
                break;
            case "Double":
                includedGuests = 2;
                break;
            case "Triple":
                includedGuests = 3;
                break;
        }
        if(numberOfGuests > includedGuests) {
            this.extraPersonCharge = (numberOfGuests - includedGuests) * 10.00; // $10 per extra person
        } else {
            this.extraPersonCharge = 0.00;
        }
    }

    public void setDiscount(double totalAmount) {
        if(discount >= 0.00 && discount <= 1.00) {
            this.discount = discount;
        } else {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }
    }

    public void setTotalAmount(double totalAmount) {
        if(totalAmount >= 0.00) {
            this.totalAmount = (roomCharges * numberOfRooms * numberOfNights) + extraPersonCharge - discount;
        } else {
            throw new IllegalArgumentException("Total amount cannot be negative.");
        }
    }

    public void setIsPaid(boolean isPaid) {
        if(isPaid == true || isPaid == false) {
            this.isPaid = isPaid;
        } else {
            throw new IllegalArgumentException("Invalid payment status.");
        }
    }

    // ==================================== GETTER ========================================

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getTypeOfRoom() {
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
