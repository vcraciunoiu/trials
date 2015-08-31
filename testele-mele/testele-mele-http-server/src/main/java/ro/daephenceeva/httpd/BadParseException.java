package ro.daephenceeva.httpd;

import java.io.IOException;

public class BadParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadParseException(IOException e) {
		super(e);
	}

	public BadParseException(String message) {
		super(message);
	}

}
