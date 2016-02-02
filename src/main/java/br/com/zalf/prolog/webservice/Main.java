package br.com.zalf.prolog.webservice;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

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
				
		LocalDate dataInicial = LocalDate.of(2015, Month.DECEMBER, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.now();
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		
		System.out.print(relatorioDaoImpl.getFiltros(12345678987L, "a"));
		//System.out.print(rankingDaoImpl.getRanking(dataInicial, dataFinal, "%", 1L, 12345678987L, "gsmta0im38ancb30qkikeqp36o"));
		//IndicadorHolder holder = relatorioDaoImpl.getIndicadoresEquipeByUnidade(dataInicial, dataFinal,2, cpf, "1234");
		
		//System.out.println(produtividadeDaoImpl.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf));
		//System.out.print(relatorioDaoImpl.getRelatorioByPeriodo(dataInicial, dataInicial, "%",1L, cpf, "pa64t2q07hlbg9ed93kh3fd660"));
		//relatorioDaoImpl.getFiltros(cpf, "smc9aksqlel92b0hn3s1settpl");
		//relatorioDaoImpl.getIndicadoresUnidadeByPeriodo(dataInicial, dataFinal,1, cpf, "7gtceldrvr49k6r86e5tbnjvi8");
			
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);
		
	}
}
