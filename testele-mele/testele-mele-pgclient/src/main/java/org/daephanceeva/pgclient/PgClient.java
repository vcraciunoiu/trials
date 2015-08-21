package org.daephanceeva.pgclient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.postgresql.util.PSQLException;

public class PgClient {

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("bah tampitule, tre sa bagi 4: host db user pass");
			return;
		}

		String host = args[0];
		String port = args[1];
		String dbname = args[2];
		String user = args[3];
		String pass = args[4];

		System.out.println("parola este: " + pass);

		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://" 
					+ host + ":" + port + "/" + dbname 
					+ "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", user, pass);

			System.out.println("m-am conectat bah tampitule. baga query");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String query = br.readLine();
				if (query.equals("")) {
					break;
				}

				try {
					Statement st = connection.createStatement();
					ResultSet rs = st.executeQuery(query);
					while (rs.next()) {
						System.out.print("Column 1 returned ");
						System.out.println(rs.getString(1));
					}
					rs.close();
					st.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

			connection.close();
		} catch (Exception e) {
			if (e instanceof PSQLException) {
				PSQLException pe = (PSQLException) e;
				System.out.println(pe.getErrorCode() + ", " + pe.getSQLState() + ", " + pe.getMessage());
			}
			e.printStackTrace();
		}

		System.out.println("gata bah am plecat. te pup papa");
	}

	public static void main1(String[] args) {
		String host = "localhost";
		String dbname = "teste";
		String user = "jboss";
		String pass = "12345";

//		System.out.println("parola este: " + pass);

		try {
//			Class.forName("org.postgresql.Driver");
//			Connection connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":5432/" + dbname, user,
//					pass);

			System.out.println("m-am conectat bah tampitule. baga query");

//			String search1 = "2014-07-02 18:58:34";
//			String search2 = "2014-07-06";
//			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			final TimeZone utc = TimeZone.getTimeZone("UTC");
//			TimeZone mytimezone = TimeZone.getDefault();
//			dateFormatter.setTimeZone(mytimezone);
//			Date data1 = dateFormatter.parse(search1);
//			System.out.println(data1);
//			DateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			dateFormatter2.setTimeZone(TimeZone.getTimeZone("UTC"));
//			String resultatul = dateFormatter2.format(data1);
//			System.out.println(resultatul);
//			Timestamp timpul = new Timestamp(System.currentTimeMillis());
//			System.out.println(timpul);
			
//			String query = "insert into tabela1 values (nume, timpu)";
//
//			Statement st = connection.createStatement();
//			ResultSet rs = st.executeQuery(query);
//			while (rs.next()) {
//				System.out.print("Column 1 returned ");
//				System.out.println(rs.getString(1));
//			}
//			rs.close();
//			st.close();
//
//			connection.close();
		} catch (Exception e) {
			if (e instanceof PSQLException) {
				PSQLException pe = (PSQLException) e;
				System.out.println(pe.getErrorCode() + ", " + pe.getSQLState() + ", " + pe.getMessage());
			}
			e.printStackTrace();
		}

	}

}
