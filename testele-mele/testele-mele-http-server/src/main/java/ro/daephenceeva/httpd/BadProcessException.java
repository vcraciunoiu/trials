package ro.daephenceeva.httpd;

public class BadProcessException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadProcessException(String message) {
		super(message);
	}

}
