package br.com.empresa.oprojeto.webservice.dao;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) {
		ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		long cpf = Long.parseLong("12345678987");
		try {
			baseDao.getCarroById(cpf);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
