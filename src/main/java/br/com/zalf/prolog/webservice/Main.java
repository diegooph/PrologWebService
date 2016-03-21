package br.com.zalf.prolog.webservice;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.dao.CalendarioDaoImpl;
import br.com.zalf.prolog.webservice.dao.ChecklistDaoImpl;
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
		ChecklistDaoImpl checklistDaoImpl = new ChecklistDaoImpl();
		String equipe = "%";
		long codUnidade = 1;
		long offset = 0;
		int limit = 10;

		LocalDate dataInicial = LocalDate.of(2016, Month.FEBRUARY, 18);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.of(2016, Month.MARCH, 20);
		Date datafinal = Date.valueOf(dataFinal);
		Long cpf = 12345678987L;
		String token = "b820oonp8l2pm1qo2s0skike1f";
		
		
	    
		Metas metas = new Metas<>();
		metas.setCodigo(1);
		metas.setNome("teste");
		metas.setValor(0.0147);
		
		Equipe eqp = new Equipe();
		eqp.setCodigo(1);;
		eqp.setNome("DevCX");
				
		Request<Equipe> request = new Request<Equipe>(token, cpf);
		request.setObject(eqp);
		request.setCodUnidade(1L);
			
		
		System.out.println(new Gson().toJson(request));
		
		
		//FrotaDaoImpl frotaDao = new FrotaDaoImpl();
		//frotaDao.getManutencaoHolder(cpf, token, codUnidade, 20, 0L, true);
		
		//checklistDaoImpl.getAllByCodUnidade(cpf, token, codUnidade, dataInicial, dataFinal, limit, offset);
		//RelatoDaoImpl relatoDaoImpl = new RelatoDaoImpl();
		//relatoDaoImpl.getAllByUnidade(dataInicial, dataFinal, equipe, codUnidade, cpf, token,10L, offset);
		//EmpresaDaoImpl empresaDaoImpl = new EmpresaDaoImpl();
		//empresaDaoImpl.getEquipesByCodUnidade(request);
		//System.out.print(relatorioDaoImpl.getFiltros(12345678987L, "a"));
		//System.out.print(rankingDaoImpl.getRanking(dataFinal, dataFinal, "%", 1L, 12345678987L, "8nkv0v78tlqdliaefdi8j07ob0"));
		//IndicadorHolder holder = relatorioDaoImpl.getIndicadoresEquipeByUnidade(dataInicial, dataFinal,2, cpf, "1234");
		//calendarioDaoImpl.getEventosByCpf(cpf, token);
		//System.out.println(produtividadeDaoImpl.getProdutividadeByPeriodo(dataInicial, dataFinal, cpf, "k6qd4tp5prsjt5tus9shjiudj9"));
		//relatorioDaoImpl.getRelatorioByPeriodo(dataInicial, dataFinal, "%",1L, cpf, "ut9mrb367jg072gn56mif4pu29");
		//relatorioDaoImpl.getFiltros(cpf, "smc9aksqlel92b0hn3s1settpl");
		//relatorioDaoImpl.getIndicadoresUnidadeByPeriodo(dataInicial, dataFinal,1, cpf, "7gtceldrvr49k6r86e5tbnjvi8");
		//MetasDaoImpl metasDao = new MetasDaoImpl();
		//metasDao.getByCpf(cpf, token);
		//metasDao.updateByCod(request);
		//List<Indicador> lista = indicadorDaoImpl.getDevCxByPeriod(cpf, datainicial, datafinal);

	}
	
	public static void setLongTempoRestante() {
		LocalDate dataocorrencia = LocalDate.of(2016, Month.FEBRUARY, 18);
		java.util.Date dataOcorrencia = Date.valueOf(dataocorrencia);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataOcorrencia);// data ocorrência
		dataOcorrencia = calendar.getTime();
		System.out.println(dataOcorrencia);
		
		
		LocalDate datamaxima = LocalDate.of(2016, Month.MARCH, 19);
		java.util.Date dataMaxima = Date.valueOf(datamaxima);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(dataMaxima);
		dataMaxima = calendar2.getTime();
		System.out.println(dataMaxima);
		
		
		long tempoRestante = dataMaxima.getTime() - dataOcorrencia.getTime();
		
		System.out.println(TimeUnit.MILLISECONDS.toHours(tempoRestante));
		
		
		
				
				
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(itemManutencao.getData());
//        calendar.add(Calendar.HOUR, itemManutencao.getPrazo());
//        java.util.Date dataMaxima = calendar.getTime();
//        long tempoRestante = dataMaxima.getTime() - System.currentTimeMillis();
//        System.out.println("tempoRestante long: " + tempoRestante);
//        System.out.println("\nItem: " + itemManutencao.getItem() + "\n Horas restantes: " + TimeUnit.MILLISECONDS.toHours(tempoRestante));
//        System.out.println(dataMaxima);
//
//        if (tempoRestante <= 0)
//            itemManutencao.setTempoRestanteResolucao(-1);
//
//        itemManutencao.setTempoRestanteResolucao(TimeUnit.MILLISECONDS.toHours(tempoRestante));
    }
	
}
