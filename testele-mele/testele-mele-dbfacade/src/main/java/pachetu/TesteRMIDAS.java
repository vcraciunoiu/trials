package pachetu;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.BNAFacadeBean;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.BNAFacadeBeanHome;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.BNAFacadeVersionData;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.SSEFacadeBean;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.SSEFacadeBeanHome;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.SSEFacadeVersionData;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.TALFacadeBean;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.TALFacadeBeanHome;
import de.schlund.j2ee.apps.rmiDataAccessService.bean.remote.TALFacadeVersionData;
import de.schlund.j2ee.apps.rmiDataAccessService.dasrmiobj.ex.RmiDasAuthentificationException;
import de.schlund.j2ee.apps.rmiDataAccessService.dasrmiobj.ex.RmiDataAccessServiceException;
import de.schlund.j2ee.apps.rmiDataAccessService.dasrmiobj.ex.RmiDataAccessServiceNoResultException;

public class TesteRMIDAS {

	private static final String ENV_LOCAL = "local";
	private static final String ENV_SWIS_TEST = "test-swis";
	private static final String ENV_SWIS_AC1 = "ac1-swis";
	private static final String ENV_SWIS_PROD = "prod-swis";
	private static final String ENV_ACC_AC1 = "ac1-acc";
	
	private static TALFacadeBean talfacadebean;
	private static BNAFacadeBean bnafacadebean;
	private static SSEFacadeBean ssefacadebean;

	public static void main(String[] args) {
		setAll(ENV_SWIS_PROD);

		try {
//			talfacadebean.getServiceIdsByMVertragNr(12345L);
			
//			Long arg0 = 1L;
//			String arg1 = "what";
//			String arg2 = "how";
//			bnafacadebean.getAllPhoneNumbersByExtKdNr(arg0, arg1, arg2);

//			ssefacadebean.findLTESubscriberIDByContractId(59810037L);
			
//			ssefacadebean.getKundeNrByServiceId(1L);
			
//			List sseTalInfoResultsByKundeNr = ssefacadebean.getSseTalInfoResultsByKundeNr(14882790L);
//			for (Object object : sseTalInfoResultsByKundeNr) {
//				System.out.println(object);
//			}
			
//			Long webDeUserId = ssefacadebean.getWebDeUserIdByExtKdNr(31914259L);
//			System.out.println(webDeUserId);
			
//			Long vertragNR = 51785525L;
//			Long techOrderIds = ssefacadebean.getTechAuftragByMVertragNr(vertragNR);
//			System.out.println("techOrderIds=" + techOrderIds);
			
			Long techOrderId = 37800700L;  //2550465L;
			Long mVertragNrByTechAuftrag = ssefacadebean.getMVertragNrByTechAuftrag(techOrderId);
			System.out.println("da bah, asta e: " + mVertragNrByTechAuftrag);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof RmiDataAccessServiceNoResultException) {
				List<String> errorList = ((RmiDataAccessServiceNoResultException)e).getErrorList();
				System.out.println("lista de erori din server:");
				for (String string : errorList) {
					System.out.println("eroarea este: " + string);
				}
			}
		}
	}

	public static void setAll(String mediul) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		
		if (mediul.equals(ENV_LOCAL)) {
			props.put(Context.PROVIDER_URL, "jnp://vcraciunoiu.ro.schlund.net:1199");
		} else if (mediul.equals(ENV_SWIS_TEST)) {
			props.put(Context.PROVIDER_URL, "jnp://swisbosstest01.schlund.de:1299,jnp://swisbosstest02.schlund.de:1299");
		} else if (mediul.equals(ENV_ACC_AC1)){
			props.put(Context.PROVIDER_URL, "jnp://ac1accfufiboss01.schlund.de:1200,jnp://ac1accfufiboss02.schlund.de:1200");
		}  else if (mediul.equals(ENV_SWIS_AC1)){
			props.put(Context.PROVIDER_URL, "jnp://ac1swisbossb01.schlund.de:1300,jnp://ac1swisbossb01.schlund.de:1300");
		}  else if (mediul.equals(ENV_SWIS_PROD)){
			props.put(Context.PROVIDER_URL, "jnp://swisboss1.schlund.de:1300,jnp://swisboss2.schlund.de:1300,jnp://swisbossb03.schlund.de:1300,jnp://swisbossb04.schlund.de:1300");
		}
		
		Context ctx = null;

		try {
			ctx = new InitialContext(props);
		} catch (NamingException e) {
			throw new RuntimeException("fail to get initial context", e);
		}

		try {
			TALFacadeBeanHome talFacadeBeanHome = (TALFacadeBeanHome) ctx
					.lookup(TALFacadeVersionData.getInstance().getJndiFacadeBeanPath());
			talfacadebean = talFacadeBeanHome.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}

		try {
			BNAFacadeBeanHome bnaFacadeBeanHome = (BNAFacadeBeanHome) ctx
					.lookup(BNAFacadeVersionData.getInstance().getJndiFacadeBeanPath());
			bnafacadebean = bnaFacadeBeanHome.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}

		try {
			SSEFacadeBeanHome sseFacadeBeanHome = (SSEFacadeBeanHome) ctx
					.lookup(SSEFacadeVersionData.getInstance().getJndiFacadeBeanPath());
			ssefacadebean = sseFacadeBeanHome.create();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}
	
	}

}
