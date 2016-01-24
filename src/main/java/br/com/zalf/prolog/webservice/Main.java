package br.com.zalf.prolog.webservice;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

import br.com.zalf.prolog.webservice.dao.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.dao.ProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.dao.RelatorioDaoImpl;

public class Main {

	public static void main(String[] args)  throws SQLException{
		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
		
		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
		ProdutividadeDaoImpl produtividadeDaoImpl = new ProdutividadeDaoImpl();
		RelatorioDaoImpl relatorioDaoImpl = new RelatorioDaoImpl();
				
		LocalDate dataInicial = LocalDate.of(2015, Month.MARCH, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.now();
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		
		//IndicadorHolder holder = relatorioDaoImpl.getIndicadoresEquipeByUnidade(dataInicial, dataFinal,2, cpf, "1234");
		
		//System.out.println(produtividadeDaoImpl.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf));
		relatorioDaoImpl.getIndicadoresEquipeByPeriodo(dataInicial, dataFinal, "Sala1", cpf, "1234");
			
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
