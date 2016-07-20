package it.polito.ToMi.exception;

public class ConflictException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6840937750579790243L;
	private String message;

	public ConflictException() {
		super();
	}

	public ConflictException(String message) {
		super();
		this.message = message;
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return "Conflict Exception: "+message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
