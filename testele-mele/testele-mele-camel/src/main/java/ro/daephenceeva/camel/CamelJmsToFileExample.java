package ro.daephenceeva.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ProcessorDefinition;

public final class CamelJmsToFileExample {

	private static final String URL_DEV_GET_BRAND_BY_ID = "http://mamsp-shop001-qs.v976.gmx.net:8580/site/restservices/sparclub/brand/10035";
	private static final String URL_DEV_GET_VOUCHERS_BY_BRAND = "http://mamsp-shop001-qs.v976.gmx.net:8580/site/restservices/sparclub/voucher?brandid=10035";
	
	private final static String URL_AC1_GET_BRAND = "https://ac1.sparclub.de/restservices/sparclub/brand/10035";
	private final static String URL__AC1_GET_VOUCHERS = "https://ac1.sparclub.de/restservices/sparclub/voucher?brandid=10035";

	private CamelJmsToFileExample() {        
    }
    
    public static void main(String args[]) throws Exception {
    	CamelContext context = new DefaultCamelContext();
    	
    	context.addRoutes(new RouteBuilder() {

			/*@Override
			public void configure() throws Exception {
				from("file://home/vlad/Documents/bleah-in.txt?noop=true")
//				.process(new GigiProcessor())
//				.bean(new Transormer(),"transformContent")
				.to("file://home/vlad/Documents/bleah-out.txt");	
			}*/
    		
			@Override
			public void configure() throws Exception {
//		    	from("direct:start")
//		    	  .to("http://mamsp-shop001-qs.v976.gmx.net:8580/site/restservices/sparclub/brand");
				
				from("direct:start").to("direct:getbrand").to("direct:getVouchers");
				
				from("direct:getbrand")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			    .setHeader("Content-Type", constant("application/json"))
			    .to("ahc:" + URL_DEV_GET_BRAND_BY_ID)
//			    .process(new GigiProcessor())
			    ;

				from("direct:getVouchers")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			    .setHeader("Content-Type", constant("application/json"))
			    .to("ahc:" + URL_DEV_GET_VOUCHERS_BY_BRAND)
			    ;
			}

    	});
    	
    	context.start();
    	Thread.sleep(3000);
    	context.stop();
    }
}

