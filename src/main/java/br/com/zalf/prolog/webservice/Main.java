package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.entrega.indicador.indicadores.item.Jornada;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.util.GsonUtils;
import br.com.zalf.prolog.webservice.util.L;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class Main {

	public static void main(String[] args)  throws SQLException {
		L.d("Main", "Testando");
		L.e("Main", "FUDEU", new Exception("Problema na main"));

		LocalDate dataInicial = LocalDate.of(2016, Month.SEPTEMBER, 01);
		Date datainicial = Date.valueOf(dataInicial);
		LocalDate dataFinal = LocalDate.of(2016, Month.SEPTEMBER, 01);
		Date datafinal = Date.valueOf(dataFinal);

		//ColaboradorDaoImpl baseDao = new ColaboradorDaoImpl();
		//long cpf = Long.parseLong("12345678987");
//
//		IndicadorDaoImpl indicadorDaoImpl = new IndicadorDaoImpl();
//		ProdutividadeDaoImpl produtividadeDaoImpl = new ProdutividadeDaoImpl();
//		RelatorioDaoImpl relatorioDaoImpl = new RelatorioDaoImpl();
//		RankingDaoImpl rankingDaoImpl = new RankingDaoImpl();
//		CalendarioDaoImpl calendarioDaoImpl = new CalendarioDaoImpl();
//		ChecklistDaoImpl checklistDaoImpl = new ChecklistDaoImpl();
//		ColaboradorDaoImpl colaboradorDaoImpl = new ColaboradorDaoImpl();
//		String equipe = "%";
//		long codUnidade = 1;
//		long offset = 0;
//		int limit = 10;

		/*
		Busca acumulado
		 */
		IndicadorDaoImpl teste = new IndicadorDaoImpl();
//		System.out.println(GsonUtils.getGson().toJson(teste.getAcumuladoIndicadoresIndividual(datainicial.getTime(),
//				datafinal.getTime(),1984679074L)));
		/*
		Busca extrato:
		 */
		System.out.println(GsonUtils.getGson().toJson(teste.getExtratoIndicador(datainicial.getTime(),
				datafinal.getTime(), "%", 2L, "3", "%", "%", Jornada.JORNADA)));

//		RelatoDaoImpl relatoDao = new RelatoDaoImpl();
//		System.out.println(relatoDao.getByColaborador(12345678987L, 10, 0, 23, 22, false, Relato.PENDENTE_CLASSIFICACAO));


//		Colaborador c = new Colaborador();
//		c.setCodUnidade(2);
//		c.setNome("Teste");
//		System.out.println(new Gson().toJson(c));

		//OrdemServicoDaoImpl dao = new OrdemServicoDaoImpl();
		//Connection conn = DatabaseConnection.getConnection();
		// placa, status, conn, unidade, tipoVeiculo
		/**
		 * Buscar as OS, usando como filtro o codUnidade, TipoVeiculo e Placa(opcional).
		 */
		//dao.getOs(placa, stauts, conn, codUnidade, tipoVeiculo, limit, offet)
		//L.d("main", dao.getOs("MLU4921","A", null, 2L, "%", 2, 0L).toString());
		//L.d("main", new Gson().toJson(dao.getOs("%","A", null, 3L, "%", 222, 0L)));
		//L.d("tag", dao.getItensOsManutencaoHolder(ItemOrdemServico.Status.PENDENTE.asString(), conn, 3L, 1, 0L).toString());
		//L.d("tag", dao.getManutencaoHolder(3L, 1, 0, ItemOrdemServico.Status.PENDENTE.asString()).toString());
		//L.d("main", dao.getItensOs("MLU4921", "%", "P", conn, null, null).toString());
		//L.d("main", new Gson().toJson(dao.getManutencaoHolder(2L, 0,0,"%")));
		//System.out.print(new Gson().toJson(dao.getItensOs("MLH4507", 1L, "P", conn)));
		//VeiculoDaoImpl daov  = new VeiculoDaoImpl();
		//L.d("main", daov.getVeiculoKm(4L, "%", "%").toString());


//		Long cpf = 12345678989L;
//		String token = "1khvje6fg1v57483shknlnodk1";
//		
//		Autenticacao aut = new Autenticacao();
//		aut.setCpf(cpf);
//		aut.setToken(token);
//		
//		System.out.println(new Gson().toJson(aut));
//		
//		System.out.println(datainicial.getTime());
//		System.out.println(datafinal.getTime());
//		
//	    
//		Metas metas = new Metas<>();
//		metas.setCodigo(1);
//		metas.setNome("teste");
//		metas.setValor(0.0147);
//		
//		Equipe eqp = new Equipe();
//		eqp.setCodigo(1);;
//		eqp.setNome("SALATESTE");
//		eqp.setCodUnidade(1);
//		
//		Veiculo veiculo = new Veiculo();
//		veiculo.setAtivo(true);
//		veiculo.setModelo("Corsa");
//		veiculo.setPlaca("MDZ9952");
//				
//		Request<Veiculo> request = new Request<Veiculo>(token, cpf);
//		request.setObject(veiculo);
//		request.setCodUnidade(1L);
//			
//		
//		//System.out.println(new Gson().toJson(checklistDaoImpl.getByColaborador(cpf, offset, limit)));
//		System.out.println(checklistDaoImpl.getByCod(20L));
//		
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
