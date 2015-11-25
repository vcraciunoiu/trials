package org.vladcrc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

}
