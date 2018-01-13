package co.uk.app.commerce.payment.exception;

public class PaymentApplicationException extends Exception {

	private static final long serialVersionUID = -7115355011299208758L;

	public PaymentApplicationException() {
		super();
	}

	public PaymentApplicationException(String message) {
		super(message);
	}

	public PaymentApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PaymentApplicationException(Throwable cause) {
		super(cause);
	}

	protected PaymentApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
