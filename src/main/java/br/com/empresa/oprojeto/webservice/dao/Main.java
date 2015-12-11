package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

public class Main {

	public static void main(String[] args)  throws SQLException{
		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
		
		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
		
		LocalDate dataInicial = LocalDate.of(2000, Month.MARCH, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.now();
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		
		IndicadorDaoImpl impl = new IndicadorDaoImpl();
		impl.getIndicadoresByPeriodo(dataInicial, dataFinal, cpf);
		
	
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
