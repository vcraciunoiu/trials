package springstuff;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

public class MyAwsomeService {

	@Resource
	SybaseService sybaseService;
	
	@Resource
	PostgresService postgresService;
	
	@Resource
	TdnsClient tdnsClient;

	@Transactional
	public void faCevaTransactional() {
		
		sybaseService.faInserturile();
		
		postgresService.faInserturile();
		
		tdnsClient.faSiTuCeva();
	};

}
