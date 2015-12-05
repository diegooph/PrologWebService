package br.com.empresa.oprojeto.webservice.dao;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) {
		ColaboradorDao baseDao = new ColaboradorDao();
		long cpf = Long.parseLong("12345678987");
		try {
			baseDao.getCarroById(cpf);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
