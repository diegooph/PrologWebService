package br.com.zalf.oprojeto.webservice.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;

public class Main {

	public static void main(String[] args)  throws SQLException{
		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
		
		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
		ProdutividadeDaoImpl produtividadeDaoImpl = new ProdutividadeDaoImpl();
		
		LocalDate dataInicial = LocalDate.of(2015, Month.MARCH, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.now();
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		
		IndicadorDaoImpl impl = new IndicadorDaoImpl();
		IndicadorHolder holder = impl.getIndicadoresByPeriodo(dataInicial, dataFinal, cpf);
		
		//System.out.println(produtividadeDaoImpl.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf));
		
		System.out.println(holder);
	
		
	
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
