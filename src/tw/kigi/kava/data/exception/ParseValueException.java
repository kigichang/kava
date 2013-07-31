package tw.kigi.kava.data.exception;

public class ParseValueException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1530663496813741377L;

	public ParseValueException() {
	}

	public ParseValueException(String message) {
		super(message);
	}

	public ParseValueException(Throwable cause) {
		super(cause);
	}

	public ParseValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseValueException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
