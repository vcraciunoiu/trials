package ro.daephenceeva.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class MainClass {

	private final static String URL_GET_BRAND = "https://ac1.sparclub.de/restservices/sparclub/brand";
	private final static String URL_GET_VOUCHERS = "https://ac1.sparclub.de/restservices/sparclub/voucher?brandid=1234";
	
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
		    public void configure() {

		    	from(URL_GET_BRAND).log("dada");

		    	
//		    	from("timer:start?repeatCount=1").autoStartup(true).to("direct:getBrand").to("direct:getVouchers").end();
		        
//		        from("timer:start?repeatCount=1").autoStartup(true).onCompletion().log("aha").end()
//                .routeId("startAtDeployRouteId").log("aha")
//                .to("direct:doIt");
		        
//		        from("direct:doIt")
//		        .to("direct:getBrand").to("direct:getVouchers");
//
//		        from("direct:getBrand")
//                .setHeader("CamelHttpMethod", constant("GET"))
//                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//		        .to("ahc:" + URL_GET_BRAND)
//		        .log("am luat brandul");
//		        
//		        from("direct:getVouchers")
//                .setHeader("CamelHttpMethod", constant("GET"))
//                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//		        .to("ahc:" + URL_GET_VOUCHERS)
//		        .log("am luat voucherele");
		    }
		});

		context.start();
	}

}
