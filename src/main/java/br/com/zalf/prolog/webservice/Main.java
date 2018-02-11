package br.com.zalf.prolog.webservice;

import java.sql.SQLException;
import java.util.Random;


public class Main {

	public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static Random RANDOM = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
		}

		return sb.toString();
	}

	public static void main(String[] args) throws SQLException {


		System.out.println(randomString(10));
	}
}