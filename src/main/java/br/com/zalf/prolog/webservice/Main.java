package br.com.zalf.prolog.webservice;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

import br.com.zalf.prolog.webservice.dao.CalendarioDaoImpl;
import br.com.zalf.prolog.webservice.dao.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.dao.ProdutividadeDaoImpl;
import br.com.zalf.prolog.webservice.dao.RankingDaoImpl;
import br.com.zalf.prolog.webservice.dao.RelatorioDaoImpl;

public class Main {

	public static void main(String[] args)  throws SQLException{
		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
		
		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
		ProdutividadeDaoImpl produtividadeDaoImpl = new ProdutividadeDaoImpl();
		RelatorioDaoImpl relatorioDaoImpl = new RelatorioDaoImpl();
		RankingDaoImpl rankingDaoImpl = new RankingDaoImpl();
		CalendarioDaoImpl calendarioDaoImpl = new CalendarioDaoImpl();
				
		LocalDate dataInicial = LocalDate.of(2016, Month.FEBRUARY, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.of(2016, Month.FEBRUARY, 28);
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		String token = "ut9mrb367jg072gn56mif4pu29";
		
		//System.out.print(relatorioDaoImpl.getFiltros(12345678987L, "a"));
		//System.out.print(rankingDaoImpl.getRanking(dataFinal, dataFinal, "%", 1L, 12345678987L, "8nkv0v78tlqdliaefdi8j07ob0"));
		//IndicadorHolder holder = relatorioDaoImpl.getIndicadoresEquipeByUnidade(dataInicial, dataFinal,2, cpf, "1234");
		calendarioDaoImpl.getEventosByCpf(cpf, token);
		//System.out.println(produtividadeDaoImpl.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf, "k6qd4tp5prsjt5tus9shjiudj9"));
		//relatorioDaoImpl.getRelatorioByPeriodo(dataInicial, dataFinal, "%",1L, cpf, "ut9mrb367jg072gn56mif4pu29");
		//relatorioDaoImpl.getFiltros(cpf, "smc9aksqlel92b0hn3s1settpl");
		//relatorioDaoImpl.getIndicadoresUnidadeByPeriodo(dataInicial, dataFinal,1, cpf, "7gtceldrvr49k6r86e5tbnjvi8");
			
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
