package pachetu;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(IMyEjb3FacadeRemote.class)
public class MyEjb3Facade {

	public String giveMeSomething() {
		return "here you are something";
	}

	public String giveMeSomethingElse() {
		return "here you are something else";
	}

}
