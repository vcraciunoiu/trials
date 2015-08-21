package com.oneandone.spot.testelemele.h2example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClasaPrincipala {

	public static void main(String[] args) {
		try {
			Class.forName("org.h2.Driver");
			Connection connection = DriverManager.getConnection("jdbc:h2:~/test");
			
			System.out.println("m-am conectat bah tampitule. baga mare");
			
			try {
				Statement st = connection.createStatement();
				ResultSet rs;
				String query;
				
				// creaza tabela
				query = "create table ...";
				rs = st.executeQuery(query);
				while (rs.next()) {
				}
				rs.close();
				
				// insert some data
				query = "create table ...";
				rs = st.executeQuery(query);
				while (rs.next()) {
				}
				rs.close();

				// make some select
				query = "create table ...";
				rs = st.executeQuery(query);
				while (rs.next()) {
				}
				rs.close();

				st.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			connection.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		System.out.println("gata bah am plecat. te pup papa");
	}

}
