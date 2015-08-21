package pachetu;

import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

public class LdapTests {

    private static String userDN;
    private static String roleDN;
    private static String contactsDN = "ou=People,ou=Contacts,o=1und1,c=DE";

    private static DirContext context;
    
	public static void main(String[] args) {
		try {
		    setEnv();
		    
			getRole();

//			checkIfpersonsExist();
			
//			getContact();
			
			context.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getContact() throws Exception {
		NamingEnumeration<SearchResult> result = context.search(new LdapName(contactsDN), 
				"uid=21405729", //1455-amass, 21424902-rita, 21405729-jcolley
				null);
		while (result.hasMoreElements()) {
			SearchResult searchResult = result.next();
			Attributes attributes = searchResult.getAttributes();
			Attribute company = attributes.get("company");
			Attribute companyid = attributes.get("companyid");
			System.out.println(searchResult);
			System.out.println(company);
			System.out.println(companyid);
		}
	}
	
	public static void getRole() throws Exception {
//	    final String userName = "rburgospablo";
//	    final String userName = "ccioriia";
        final String userName = "pqatrotest";

		NamingEnumeration<SearchResult> rolesInfo = context.search(new LdapName(roleDN), 
				"submember=uid=" + userName, null);

		if (rolesInfo.hasMoreElements()) {
			while (rolesInfo.hasMoreElements()) {
				Attribute role = rolesInfo.next().getAttributes().get("ou");
				String ldapGroup = (String) role.get();
				System.out.println(ldapGroup);
			}
		}

		rolesInfo = context.search(new LdapName(roleDN), "member=uid=" + userName, null);

		if (rolesInfo.hasMoreElements()) {
			while (rolesInfo.hasMoreElements()) {
				Attribute role = rolesInfo.next().getAttributes().get("ou");
				String ldapGroup = (String) role.get();
				System.out.println(ldapGroup);
			}
		}
	}

	public static void checkIfpersonsExist() throws Exception {
//		String email = "rburgos@arsys.es";
//		String uid = "rburgospablo";
        String uid = "ccioriia";

//		NamingEnumeration<SearchResult> userInfo = context.search(new LdapName(userDN), "mail=" + email, null);
		NamingEnumeration<SearchResult> userInfo = context.search(new LdapName(userDN), "uid=" + uid, null);
		if (!userInfo.hasMoreElements()) {
			System.out.println("The person with doesn't exist.");
		} else {
			System.out.println("exist!");
		}
	}

   private static void setEnv() throws Exception {
       Properties props = new Properties();
       props.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
       props.put("java.naming.security.authentication", "simple");
       props.put("com.sun.jndi.ldap.connect.pool", "true");

       // environment
       //STAGE-QA
//     props.put("java.naming.provider.url", "ldaps://dir1-qa.fe.server.lan:636/");
//     System.setProperty("javax.net.ssl.trustStore", "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net-truststore.jks");
//     System.setProperty("javax.net.ssl.trustStorePassword", "test123");
//     System.setProperty("javax.net.ssl.keyStore", "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net.jks");
//     System.setProperty("javax.net.ssl.keyStorePassword", "test123");
       //PROD
       props.put("java.naming.provider.url", "ldaps://ldap.1and1.org:636/");

       // credentials
       String ldapToolUser;
       String ldapToolPass;
       
//       ldapToolUser = "lgs_cronos";
//       ldapToolPass = "YTg2YTQ1NGY3NDJiMzll";

       ldapToolUser = "rhq-tool";
       ldapToolPass = "MjIwNDBkND";

//       ldapToolUser = "qatroservices";
//       ldapToolPass = "MTQyZTNkNz";

//       ldapToolUser = "qatrospptest";
//       ldapToolPass = "OGNlZjdhND";

       props.put("java.naming.security.principal", "uid=" + ldapToolUser + ",ou=accounts,ou=ims_service,o=1und1,c=DE");
       props.put("java.naming.security.credentials", ldapToolPass);
       userDN = "ou=users,ou=" + ldapToolUser + ",ou=ims_service,o=1und1,c=DE";
       roleDN = "ou=roles,ou=" + ldapToolUser + ",ou=ims_service,o=1und1,c=DE";
        
       context = new InitialDirContext(props);
    }

}
