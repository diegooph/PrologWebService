package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.empresa.oprojeto.models.indicadores.Indicador;

public class Main {

	public static void main(String[] args)  throws SQLException{
		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
		
		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
		
		LocalDate dataInicial = LocalDate.of(2015, 06, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.of(2015, 06, 29);
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
