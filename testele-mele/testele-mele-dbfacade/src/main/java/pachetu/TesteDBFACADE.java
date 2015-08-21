package pachetu;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.schlund.j2ee.common.dbfacade.migraene.bean.remote.MigraeneFacadeBean;
import de.schlund.j2ee.common.dbfacade.migraene.bean.remote.MigraeneFacadeBeanHome;
import de.schlund.j2ee.common.dbfacade.migraene.bean.remote.MigraeneFacadeVersionData;
import de.schlund.j2ee.common.rmiobj_migraene.rcvalue.order.TechOrderValue;

public class TesteDBFACADE {

//	public static final String beanName = "ejb/remote/migraene/dbfacade_v2_45/MigraeneFacade";
	private static MigraeneFacadeBean migraenebean;

	public static void main(String[] args) {
		setAll();

		Long techOrderId = 59812049L; //59810037  -- 57563583 -- 59812049

		TechOrderValue techOrder = new TechOrderValue();
		techOrder.setTechOrderId(techOrderId);
		techOrder.deepInit();

		List techOrderList = null;
		try {
			techOrderList = migraenebean.selectData(null, techOrder);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (techOrderList.size() == 0) {
			System.out.println("no entry found for techauftrag id = " + techOrderId);
		} else {
			for (Iterator iterator = techOrderList.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				System.out.println(object);
			}
		}
	}

	public static void setAll() {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

//		props.put(Context.PROVIDER_URL, "jnp://ac1accfufiboss01.schlund.de:1200,jnp://ac1accfufiboss02.schlund.de:1200");
//		props.put(Context.PROVIDER_URL, "jnp://ac1swisbossb01.schlund.de:1300,jnp://ac1swisbossb01.schlund.de:1300");
		props.put(Context.PROVIDER_URL, "jnp://swisboss1.schlund.de:1300,jnp://swisboss2.schlund.de:1300,jnp://swisbossb03.schlund.de:1300,jnp://swisbossb04.schlund.de:1300");
		
		Context ctx = null;

		try {
			ctx = new InitialContext(props);
		} catch (NamingException e) {
			throw new RuntimeException("fail to get initial context", e);
		}

		try {
			MigraeneFacadeBeanHome migraeneFacadeBeanHome = (MigraeneFacadeBeanHome) ctx
					.lookup(MigraeneFacadeVersionData.getInstance().getJndiFacadeBeanPath());
			migraenebean = migraeneFacadeBeanHome.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}

	}

	// Object obj = null;
	// try {
	// obj = ctx.lookup(beanName);
	// MigraeneFacadeBeanHome mfh = (MigraeneFacadeBeanHome) PortableRemoteObject.narrow(obj,
	// MigraeneFacadeBeanHome.class);
	// migraenebean = mfh.create();
	// } catch (Exception e) {
	// throw new RuntimeException("could not obtain test home interface", e);
	// }
	//

}



