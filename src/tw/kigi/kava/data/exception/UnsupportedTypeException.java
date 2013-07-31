package tw.kigi.kava.data.exception;

public class UnsupportedTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9147604595432463301L;

	public UnsupportedTypeException() {
		
	}

	public UnsupportedTypeException(String message) {
		super(message);
		
	}

	public UnsupportedTypeException(Throwable cause) {
		super(cause);
		
	}

	public UnsupportedTypeException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UnsupportedTypeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
