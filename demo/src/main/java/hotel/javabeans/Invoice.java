package hotel.javabeans;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import hotel.javabeans.payment.Payment;

public class Invoice {

    // ========================================================== ENUM ==========================================================
    public enum RoomType {
        SINGLE, DOUBLE, TRIPLE
    }

    // ========================================================== CONSTANTS =====================================================
    public static final double TAX_RATE = 0.1;
    public static final double EXTRA_PERSON_FEE = 10.0;

    // ========================================================== FIELDS ========================================================
    private final String invoiceId;
    private final String bookingId;

    // how many rooms for each type
    private Map<RoomType, Integer> roomTypes = new EnumMap<>(RoomType.class);

    // price per night for each type
    private Map<RoomType, Double> roomRatePerNight = new EnumMap<>(RoomType.class);

    private int numberOfNights;
    private int numberOfGuests;

    private double roomCharges;
    private double extraPersonCharge;
    private double discount;
    private double taxAmount;
    private double depositApplied;
    private double totalAmount;

    private boolean paid;
    private final LocalDate issueDate;
    private Payment payment;

    // ========================================================== CONSTRUCTOR ===================================================
    public Invoice(String bookingId,
                   Map<RoomType, Integer> roomTypes,
                   Map<RoomType, Double> roomRatePerNight,
                   int numberOfNights,
                   int numberOfGuests,
                   double discount,
                   double depositApplied,
                   Payment payment) {

        this.invoiceId = generateInvoiceId();
        this.bookingId = validateRequiredString(bookingId, "Booking ID");
        this.issueDate = LocalDate.now();

        setRoomTypes(roomTypes);
        setRoomRatePerNight(roomRatePerNight);
        setNumberOfNights(numberOfNights);
        setNumberOfGuests(numberOfGuests);
        setDiscount(discount);
        setDepositApplied(depositApplied);

        this.paid = false;
        this.payment = null;

        if (payment != null) {
            payInvoice(payment);
        } else {
            recalculateInvoice();
        }
    }

    // ========================================================== VALIDATION ====================================================
    private String generateInvoiceId() {
        return "IV" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String validateRequiredString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
        return value.trim();
    }

    private int validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive.");
        }
        return value;
    }

    private double validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return value;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ========================================================== SETTERS =======================================================
    public void setRoomTypes(Map<RoomType, Integer> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            throw new IllegalArgumentException("Room types cannot be null or empty.");
        }

        Map<RoomType, Integer> copy = new EnumMap<>(RoomType.class);

        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            RoomType type = entry.getKey();
            Integer quantity = entry.getValue();

            if (type == null) {
                throw new IllegalArgumentException("Room type cannot be null.");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity for " + type + " must be positive.");
            }

            copy.put(type, quantity);
        }

        this.roomTypes = copy;
        recalculateInvoice();
    }

    public void setRoomRatePerNight(Map<RoomType, Double> roomRatePerNight) {
        if (roomRatePerNight == null || roomRatePerNight.isEmpty()) {
            throw new IllegalArgumentException("Room rates cannot be null or empty.");
        }

        Map<RoomType, Double> copy = new EnumMap<>(RoomType.class);

        for (Map.Entry<RoomType, Double> entry : roomRatePerNight.entrySet()) {
            RoomType type = entry.getKey();
            Double rate = entry.getValue();

            if (type == null) {
                throw new IllegalArgumentException("Room type in rates cannot be null.");
            }
            if (rate == null || rate <= 0) {
                throw new IllegalArgumentException("Room rate for " + type + " must be positive.");
            }

            copy.put(type, rate);
        }

        for (RoomType type : this.roomTypes.keySet()) {
            if (!copy.containsKey(type)) {
                throw new IllegalArgumentException("Missing room rate for " + type + ".");
            }
        }

        this.roomRatePerNight = copy;
        recalculateInvoice();
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = validatePositive(numberOfNights, "Number of nights");
        recalculateInvoice();
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = validatePositive(numberOfGuests, "Number of guests");
        recalculateInvoice();
    }

    public void setDiscount(double discount) {
        this.discount = round2(validateNonNegative(discount, "Discount"));
        recalculateInvoice();
    }

    public void setDepositApplied(double depositApplied) {
        this.depositApplied = round2(validateNonNegative(depositApplied, "Deposit applied"));
        recalculateInvoice();
    }

    // ========================================================== BUSINESS LOGIC ================================================
    private int getIncludedGuests(RoomType roomType) {
        switch (roomType) {
            case SINGLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default: throw new IllegalStateException("Unsupported room type.");
        }
    }

    private int getTotalIncludedGuests() {
        int total = 0;

        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            total += getIncludedGuests(entry.getKey()) * entry.getValue();
        }

        return total;
    }

    private void calculateRoomCharges() {
        double total = 0.0;

        for (Map.Entry<RoomType, Integer> entry : roomTypes.entrySet()) {
            RoomType type = entry.getKey();
            int quantity = entry.getValue();
            double rate = roomRatePerNight.get(type);

            total += rate * quantity * numberOfNights;
        }

        this.roomCharges = round2(total);
    }

    private void calculateExtraPersonCharge() {
        int includedGuests = getTotalIncludedGuests();
        int extraGuests = Math.max(0, numberOfGuests - includedGuests);
        this.extraPersonCharge = round2(extraGuests * EXTRA_PERSON_FEE);
    }

    private double calculateSubtotal() {
        double subtotal = roomCharges + extraPersonCharge - discount;
        return Math.max(0, round2(subtotal));
    }

    public void recalculateInvoice() {
        if (roomTypes == null || roomTypes.isEmpty()
                || roomRatePerNight == null || roomRatePerNight.isEmpty()
                || numberOfNights <= 0
                || numberOfGuests <= 0) {
            return;
        }

        for (RoomType type : roomTypes.keySet()) {
            if (!roomRatePerNight.containsKey(type)) {
                throw new IllegalStateException("Missing room rate for " + type + ".");
            }
        }

        calculateRoomCharges();
        calculateExtraPersonCharge();

        double subtotal = calculateSubtotal();
        this.taxAmount = round2(subtotal * TAX_RATE);

        double grossTotal = round2(subtotal + taxAmount);

        if (depositApplied > grossTotal) {
            throw new IllegalArgumentException("Deposit applied cannot exceed invoice gross total.");
        }

        this.totalAmount = round2(grossTotal - depositApplied);
    }

    public int getTotalRooms() {
        int total = 0;
        for (int quantity : roomTypes.values()) {
            total += quantity;
        }
        return total;
    }

    public void payInvoice(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null.");
        }

        if (this.paid) {
            throw new IllegalStateException("Invoice is already paid.");
        }

        if (payment.getPaymentStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment must be completed.");
        }

        if (round2(payment.getAmountPaid()) < this.totalAmount) {
            throw new IllegalArgumentException("Payment amount is less than invoice total.");
        }

        this.payment = payment;
        this.paid = true;
    }

    // ========================================================== GETTERS =======================================================
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Map<RoomType, Integer> getRoomTypes() {
        return new EnumMap<>(roomTypes);
    }

    public Map<RoomType, Double> getRoomRatePerNight() {
        return new EnumMap<>(roomRatePerNight);
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public double getExtraPersonCharge() {
        return extraPersonCharge;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getDepositApplied() {
        return depositApplied;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean getIsPaid() {
        return isPaid();
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Payment getPayment() {
        return payment;
    }

    // ========================================================== TOSTRING ======================================================
    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", roomTypes=" + roomTypes +
                ", roomRatePerNight=" + roomRatePerNight +
                ", totalRooms=" + getTotalRooms() +
                ", numberOfNights=" + numberOfNights +
                ", numberOfGuests=" + numberOfGuests +
                ", roomCharges=" + roomCharges +
                ", extraPersonCharge=" + extraPersonCharge +
                ", discount=" + discount +
                ", taxAmount=" + taxAmount +
                ", depositApplied=" + depositApplied +
                ", totalAmount=" + totalAmount +
                ", paid=" + paid +
                ", issueDate=" + issueDate +
                ", payment=" + (payment != null ? payment : "No payment info") +
                '}';
    }
}