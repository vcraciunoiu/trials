package ro.daephenceeva.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GigiProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("processing " + exchange.getIn().getBody(String.class));
	}

}
