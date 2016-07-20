package it.polito.ToMi.exception;

public class ForbiddenException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5240243462084178505L;
	private String message;

	public ForbiddenException() {
		super();
	}

	public ForbiddenException(String message) {
		super();
		this.message = message;
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return "ForbiddenException: "+message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
