package ro.daephenceeva.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * An example class for demonstrating some of the basics behind Camel. This
 * example sends some text messages on to a JMS Queue, consumes them and
 * persists them to disk
 */
public final class CamelJmsToFileExample {

	private final static String URL_GET_BRAND = "https://ac1.sparclub.de/restservices/sparclub/brand";
	private final static String URL_GET_VOUCHERS = "https://ac1.sparclub.de/restservices/sparclub/voucher?brandid=1234";

	private CamelJmsToFileExample() {        
    }
    
    public static void main(String args[]) throws Exception {
    	CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
		        from("direct:start")
            	
//            	from("mimi:getBrand")
                .setHeader("CamelHttpMethod", constant("GET"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		        .to("ahc:" + URL_GET_BRAND)
                .log("Status: ${header.CamelHttpResponseCode}");
		        
//		        from("direct:getVouchers").routeId("direct:getVouchers")
//                .setHeader("CamelHttpMethod", constant("GET"))
//                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//		        .to("ahc:" + URL_GET_VOUCHERS)
//		        .log("am luat voucherele");
            }
        });

        context.start();

        context.stop();
    }
}

