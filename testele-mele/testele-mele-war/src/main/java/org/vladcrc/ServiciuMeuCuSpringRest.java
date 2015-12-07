package org.vladcrc;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/superserviciu")
public class ServiciuMeuCuSpringRest {

    @Value("${aaa.bbb}")
    private String cemaivariabila;

    @RequestMapping("/salutare")
    public String greeting() {
        return "Ce faci bah jmekere ? " + cemaivariabila;
    }

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String printHello(ModelMap model) {
		model.addAttribute("message", "Hello Spring MVC Framework!");
		return "hello";
	}

	@RequestMapping(value = "/testare", method = RequestMethod.GET)
	@ResponseBody
	public String someConfiguration() {
		StringBuilder toreturn = new StringBuilder();

		toreturn.append("{ ");
		toreturn.append("\"auth.endpoint.uri\": \"https://authserver/v1/auth\", ");
		toreturn.append("\"job.timeout\": 3600, ");
		toreturn.append("\"job.maxretry\": 4, ");
		toreturn.append("\"sns.broadcast.topic_name\": \"broadcast\", ");
		toreturn.append("\"sns.broadcast.visibility_timeout\": 30, ");
		toreturn.append("\"score.factor\": 2.4, ");
		toreturn.append("\"jpa.showSql\": false ");
		toreturn.append("}");
		
		return toreturn.toString();
	}

//	@RequestMapping(value = "/testare2", method = RequestMethod.GET, produces = "application/json")
//	public String someConfiguration2() {
//		Collections.singletonMap(key, value)("{", value);
//		
//		toreturn.append("\"auth.endpoint.uri\": \"https://authserver/v1/auth\"," + System.getProperty("line.separator"));
//		toreturn.append("\"job.timeout\": 3600, " + );
//		toreturn.append("\"job.maxretry\": 4");
//		toreturn.append("\"sns.broadcast.topic_name\": \"broadcast\"");
//		toreturn.append("\"sns.broadcast.visibility_timeout\": 30");
//		toreturn.append("\"score.factor\": 2.4");
//		toreturn.append("\"jpa.showSql\": false");
//		toreturn.append("}");
//		
//		return toreturn.toString();
//	}

}
