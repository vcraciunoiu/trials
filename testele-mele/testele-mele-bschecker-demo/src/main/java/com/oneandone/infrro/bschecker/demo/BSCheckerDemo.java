package com.oneandone.infrro.bschecker.demo;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.oneandone.infrro.spp.monitoring.bstatus.annotation.checker.EJBChecker;
import com.oneandone.infrro.spp.monitoring.bstatus.annotation.checker.EJBCheckerMethod;
import com.oneandone.infrro.spp.monitoring.bstatus.model.BStatus;
import com.oneandone.infrro.spp.monitoring.bstatus.model.BStatusType;

@Stateless
@Remote(IBSCheckerDemo.class)
@EJBChecker
public class BSCheckerDemo implements IBSCheckerDemo {

	@EJBCheckerMethod()
	public BStatus checkMethodNumberOne() {
		return new BStatus(BStatusType.OK, "All systems rulls.");
	}

	@EJBCheckerMethod()
	public BStatus checkMethodNumberTwo() {
		return new BStatus(BStatusType.WARNING, "Something is going on here...");	
	}

	@EJBCheckerMethod()
	public BStatus checkMethodNumberThree() {
		return new BStatus(BStatusType.ERROR, "Dude, u'r in trouble");	
	}

	@EJBCheckerMethod()
	public BStatus checkMethodNumberFour() {
		return new BStatus(BStatusType.ERROR, "Dude, u'r in trouble again");	
	}

}
