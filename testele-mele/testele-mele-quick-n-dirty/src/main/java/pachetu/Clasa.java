package pachetu;

public class Clasa {

    public static void main(String[] args) {
//    	String fileExtension = "html";
//		
//    	String valueOf = MimeTypes.valueOf(fileExtension).getContentType();
//		System.out.println(valueOf);
//		
//		String string = MimeTypes.valueOf(fileExtension).toString();
//		System.out.println(string);
		
		String resourceName = "/gigi.zip";
		String[] split = resourceName.split("\\.");
		System.out.println(split[1]);
    }

	enum MimeTypes {
		APPLICATION_FORM_URLENCODED ("application/x-www-form-urlencoded"),
		APPLICATION_JSON ("application/json"),
		APPLICATION_OCTET_STREAM ("application/octet-stream"),
		APPLICATION_XML ("application/xml"),
		MULTIPART_FORM_DATA ("multipart/form-data"),
		html ("text/html"),
		TEXT_PLAIN ("text/plain"),
		TEXT_XML ("text/xml")
		;
		
	    public String contentType;
	    
	    public String getContentType() {
			return contentType;
		}
	    
		MimeTypes(String contentType) { 
	        this.contentType = contentType;
	    }	
	}
	
	private static String oMethoda() {
		return someTrickyMethod();
	}

	private static String someTrickyMethod() throws IllegalArgumentException, NullPointerException {
		if (1 == 1) {
			throw new IllegalArgumentException("gigi");
		}
		
		return "corect";
	}

	private static void testDisplayFriendlyDuration() {
		long millis = 23456789;
                             
        long x;
        x = millis / 1000;
        System.out.println("x=" + x);
        long seconds = x % 60;
        System.out.println("seconds=" + seconds);
        x /= 60;
        System.out.println("x=" + x);
        long minutes = x % 60;
        System.out.println("minutes=" + minutes);
        x /= 60;
        System.out.println("x=" + x);
        long hours = x % 24;
        System.out.println("hours=" + hours);
        x /= 24;
        System.out.println("x=" + x);
        long days = x;
        System.out.println("days=" + days);
        
        String result = days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
        System.out.println(result);
	}

}
