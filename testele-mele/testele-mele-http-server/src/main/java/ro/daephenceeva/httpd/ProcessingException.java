package ro.daephenceeva.httpd;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = 1L;

	private Integer status;
	
	public ProcessingException(Integer status, String message) {
		super(message);
		this.setStatus(status);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
