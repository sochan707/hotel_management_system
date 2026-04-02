package hotel.javabeans.payment;

public class QrPayment extends Payment implements PaymentMethods {

    public QrPayment() {
        super(null, 0, null, null);
    }

    @Override
    public void processPayment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validatePayment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double calculateTotal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
