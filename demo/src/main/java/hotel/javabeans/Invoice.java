package hotel.javabeans;

public class Invoice {
    private String invoiceId;
    private String typeOfRoom;
    private double roomCharges;
    private int numberOfRooms;
    private int numberOfNights;
    private int numberOfGuests;
    private double discount;
    private double totalAmount;
    private boolean isPaid;

    public Invoice(String invoiceId, String typeOfRoom, double roomCharges, int numberOfRooms, int numberOfNights, int numberOfGuests, double totalAmount, boolean isPaid) {
        setInvoiceId(invoiceId);
        setTypeOfRoom(typeOfRoom);
        setRoomCharges(roomCharges);
        setNumberOfRooms(numberOfRooms);
        setNumberOfNights(numberOfNights);
        setNumberOfGuests(numberOfGuests, typeOfRoom);

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

    public void setNumberOfGuests(int numberOfGuests, String typeOfRoom) {
        setTypeOfRoom(typeOfRoom);
        if(numberOfGuests > 0) {
            this.numberOfGuests = numberOfGuests;
        } else {
            throw new IllegalArgumentException("Number of guests cannot be negative or zero.");
        }
    }
}
