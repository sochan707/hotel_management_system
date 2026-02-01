package hotel.javabeans;

public class Invoice {
    private String invoiceId;
    private String typeOfRoom;
    private double roomCharges;
    private int numberOfRooms;
    private int numberOfNights;
    private int numberOfGuests;
    private double extraPersonCharge;
    private double discount;
    private double totalAmount;
    private boolean isPaid;

    public Invoice(String invoiceId, String typeOfRoom, double roomCharges, int numberOfRooms, int numberOfNights, int numberOfGuests, double totalAmount, boolean isPaid) {
        setInvoiceId(invoiceId);
        setTypeOfRoom(typeOfRoom);
        setRoomCharges(roomCharges);
        setNumberOfRooms(numberOfRooms);
        setNumberOfNights(numberOfNights);
        setNumberOfGuests(numberOfGuests);
        setExtraPersonCharge(numberOfGuests, typeOfRoom);
        setDiscount(totalAmount);
        setTotalAmount(totalAmount);
        setIsPaid(isPaid);
    }

    public void setInvoiceId(String invoiceId) {
        if(invoiceId != null && !invoiceId.isEmpty()) {
            this.invoiceId = invoiceId;
        } else {
            throw new IllegalArgumentException("Invoice ID cannot be null or empty.");
        }
    }

    public void setTypeOfRoom(String typeOfRoom) {
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

    public void setNumberOfRooms(int numberOfRooms) {
        if(numberOfRooms > 0) {
            this.numberOfRooms = numberOfRooms;
        } else {
            throw new IllegalArgumentException("Number of rooms cannot be negative or zero.");
        }
    }

    public void setNumberOfNights(int numberOfNights) {
        if(numberOfNights > 0) {
            this.numberOfNights = numberOfNights;
        } else {
            throw new IllegalArgumentException("Number of nights cannot be negative or zero.");
        }
    }

    public void setNumberOfGuests(int numberOfGuests) {
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
        if(discount >= 0.00) {
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
    
}
