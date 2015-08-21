package pachetu;


public class Clasa {

    public static void main(String[] args) {
    	String brandIdParam = null;
		String sourceParam = "AFIFI";
		String logMsg = "Getting vouchers. " + (brandIdParam==null?"":"brandIdParam="+brandIdParam+" ") + (sourceParam==null?"":"sourceParam="+sourceParam);
    	System.out.println(logMsg);
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
