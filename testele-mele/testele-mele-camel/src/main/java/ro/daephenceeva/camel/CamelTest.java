package ro.daephenceeva.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelTest extends CamelTestSupport {

	private static final String URL_DEV_GET_BRAND_BY_ID = "http://mamsp-shop001-qs.v976.gmx.net:8580/site/restservices/sparclub/brand/10035";
	private static final String URL_DEV_GET_VOUCHERS_BY_BRAND = "http://mamsp-shop001-qs.v976.gmx.net:8580/site/restservices/sparclub/voucher?brandid=10035";
	
	private final static String URL_AC1_GET_BRAND = "https://ac1.sparclub.de/restservices/sparclub/brand/10035";
	private final static String URL__AC1_GET_VOUCHERS = "https://ac1.sparclub.de/restservices/sparclub/voucher?brandid=10035";

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
	    return new RouteBuilder() {
	        @Override
	        public void configure() throws Exception {
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
	    };
	}
	
	@Test
	public void testDebugger() {
		System.out.println("baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	}
	
	@Override
	public boolean isUseDebugger() {
	    // must enable debugger
	    return true;
	}
	 
	@Override
	protected void debugBefore(Exchange exchange, Processor processor,
	                           ProcessorDefinition<?> definition, String id, String shortName) {
	    // this method is invoked before we are about to enter the given processor
	    // from your Java editor you can just add a breakpoint in the code line below
	    log.info("Before " + definition + " with body " + exchange.getIn().getBody());
	}
	
}
