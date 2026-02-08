package hotel.javabeans;

import java.util.Objects;

import hotel.javabeans.Invoice.RoomType;

public class Invoice {
    private String invoiceId;
    private RoomType roomType;
    private double roomCharges;
    private int numberOfRooms;
    private int numberOfNights;
    private int numberOfGuests;
    private boolean isPaid;

    private static final double EXTRA_PERSON_FEE = 10.00;

    public Invoice(
            String invoiceId,
            RoomType roomType,
            double roomCharges,
            int numberOfRooms,
            int numberOfNights,
            int numberOfGuests
    ) {
        setInvoiceId(invoiceId);
        setRoomType(roomType);
        setRoomCharges(roomCharges);
        setNumberOfRooms(numberOfRooms);
        setNumberOfNights(numberOfNights);
        setNumberOfGuests(numberOfGuests);
        this.isPaid = false;
    }

    public enum RoomType {
        SINGLE(1),
        DOUBLE(2),
        TRIPLE(3);

        private final int includedGuests;

        RoomType(int includedGuests) {
            this.includedGuests = includedGuests;
        }

        public int getIncludedGuests() {
            return includedGuests;
        }
    }


    public void setInvoiceId(String invoiceId) {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID cannot be null or blank.");
        }
        this.invoiceId = invoiceId;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = Objects.requireNonNull(
            roomType, "Room type cannot be null."
        );
    }

    public void setRoomCharges(double roomCharges) { // holiday ey lerng tlai room jg oy staff input tv
        if (roomCharges < 0) {
            throw new IllegalArgumentException("Room charges cannot be negative.");
        }
        this.roomCharges = roomCharges;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        if(numberOfRooms > 0) {
            this.numberOfRooms = numberOfRooms;
        } else {
            throw new IllegalArgumentException("Number of rooms cannot be negative or zero.");
        }
    }

    public void setNumberOfNights(int numberOfNights) {
        if (numberOfNights <= 0) {
            throw new IllegalArgumentException("Number of nights must be greater than zero.");
        }
        this.numberOfNights = numberOfNights;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than zero.");
        }
        this.numberOfGuests = numberOfGuests;
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    public double calculateExtraPersonCharge() {
        int extraGuests = Math.max(0, numberOfGuests - roomType.getIncludedGuests());
        return extraGuests * EXTRA_PERSON_FEE;
    }

    public double calculateTotalAmount() {
        return (roomCharges * numberOfRooms * numberOfNights) + calculateExtraPersonCharge();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public RoomType getRoomType() {
        return roomType;
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

    public boolean IsPaid() {
        return isPaid;
    }
    
}
