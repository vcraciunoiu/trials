package springstuff;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JustSomeTeste {

	public static void main(String[] args) {
		String configLocation = "application-context.xml";
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configLocation);
		
		MyAwsomeService serviciu = (MyAwsomeService) ctx.getBean("serviciu");
		
		serviciu.faCevaTransactional();
		ctx.close();
	}

}
