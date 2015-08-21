package com.oneandone.infrro.bschecker.demo;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.oneandone.infrro.spp.monitoring.bstatus.annotation.checker.EJBChecker;
import com.oneandone.infrro.spp.monitoring.bstatus.annotation.checker.EJBCheckerMethod;
import com.oneandone.infrro.spp.monitoring.bstatus.model.BSChannel;
import com.oneandone.infrro.spp.monitoring.bstatus.model.BStatus;
import com.oneandone.infrro.spp.monitoring.bstatus.model.BStatusType;

@Stateless
@Local(IDemoEJB30Local.class)
@Remote(IDemoEJB30Remote.class)
//@LocalBean
@EJBChecker
// (channel=BSChannel.CRITICAL)
public class DemoEJB30FullBean implements IDemoEJB30Local, IDemoEJB30Remote {
	@EJBCheckerMethod(timeout = 10, channel = { BSChannel.CRITICAL, BSChannel.VERIFY, BSChannel.DEBUG })
	public BStatus getStatus() {
		return new BStatus(BStatusType.OK, "Local/Remote interface - ALL Channels");
	}

	@EJBCheckerMethod()
	public BStatus getStatus30Remote() {
//		return DemoUtil.getOKStatus("Remote interface - CRITICAL channel");
		return new BStatus(BStatusType.OK, "Remote interface - CRITICAL channel");
	}

	@EJBCheckerMethod()
	public BStatus getStatus30Local() {
//		return DemoUtil.getOKStatus("Local interface - CRITICAL channel");
		return new BStatus(BStatusType.ERROR, "Local interface - CRITICAL channel");
	}

	// LocalBean --- no need of another interface -- only add the @LocalBean annotation to the class and declare the
	// method as public
	@EJBCheckerMethod()
	public BStatus getStatus30LocalBean() {
//		return DemoUtil.getOKStatus("LocalBean - DEBUG channel");
		return new BStatus(BStatusType.OK, "LocalBean - DEBUG channel");
	}
}
