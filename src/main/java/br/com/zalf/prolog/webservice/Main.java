package br.com.zalf.prolog.webservice;

import java.sql.SQLException;


public class Main {

	public static void main(String[] args) throws SQLException {
		// funciona
		System.out.println(System.getenv("PROLOG_RDS_DB_NAME"));
		// não funciona
		System.out.println(System.getProperty("PROLOG_RDS_DB_NAME"));
	}
}