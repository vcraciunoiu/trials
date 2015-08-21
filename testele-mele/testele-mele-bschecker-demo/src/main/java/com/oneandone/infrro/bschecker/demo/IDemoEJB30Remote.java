package com.oneandone.infrro.bschecker.demo;

import com.oneandone.infrro.spp.monitoring.bstatus.model.BStatus;

public interface IDemoEJB30Remote {
	BStatus getStatus();
	BStatus getStatus30Remote();
}
