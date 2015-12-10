package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.indicador.DevolucaoCxHolder;
import br.com.empresa.oprojeto.models.indicador.DevolucaoNfHolder;
import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;
import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoCx;
import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoNf;
import br.com.empresa.oprojeto.models.indicador.ItemJornadaLiquida;
import br.com.empresa.oprojeto.models.indicador.ItemTempoInterno;
import br.com.empresa.oprojeto.models.indicador.ItemTempoLargada;
import br.com.empresa.oprojeto.models.indicador.ItemTempoRota;
import br.com.empresa.oprojeto.models.indicador.JornadaLiquidaHolder;
import br.com.empresa.oprojeto.models.indicador.TempoInternoHolder;
import br.com.empresa.oprojeto.models.indicador.TempoLargadaHolder;
import br.com.empresa.oprojeto.models.indicador.TempoRotaHolder;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.models.util.TimeUtils;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends DataBaseConnection implements IndicadorDao {
	//Códigos das metas no BD


	private static final String BUSCA_INDICADORES = "SELECT M.DATA, M.CXCARREG, "
			+ "M.CXENTREG, M.QTNFCARREGADAS, M.QTNFENTREGUES, M.HRSAI, M.HRENTR,"
			+ " M.TEMPOINTERNO, M.HRMATINAL FROM MAPA_COLABORADOR MC JOIN "
			+ "COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND MC.COD_AMBEV "
			+ "= C.MATRICULA_AMBEV JOIN MAPA M ON M.MAPA = MC.MAPA WHERE C.CPF = "
			+ "? AND DATA BETWEEN ? AND ? ORDER BY M.DATA";

	private static final String BUSCA_METAS = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO"
			+ " JOIN COLABORADOR C ON C.COD_UNIDADE = MU.COD_UNIDADE "
			+ "WHERE C.CPF = ? ORDER BY M.CODIGO";

	private IndicadorHolder indicadorHolder = new IndicadorHolder();
	//TODO: IMPLEMENTAR METODO PARA METAS.
	private LocalTime metaTempoInternoHoras;
	private LocalTime metaTempoRotaHoras;
	private LocalTime metaTempoLargadaHoras;
	private LocalTime metaJornadaLiquidaHoras;
	private double metaTempoLargadaMapas;
	private double metaTempoInternoMapas;
	private double metaTempoRotaMapas;
	private double metaJornadaLiquidaMapas;
	private double metaDevCx;
	private double metaDevNf;




	@Override
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal, long cpf)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		preencheMetas(cpf);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();

			createDevCx(rSet);
			createDevNf(rSet);
			createTempoInterno(rSet);
			createTempoRota(rSet);
			createTempoLargada(rSet);
			createJornadaLiquida(rSet);

		}
		finally {
			closeConnection(conn, stmt, null);
		}	

		return indicadorHolder;
	}
	
	private void preencheMetas(long cpf) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();

			while(rSet.next()){
				if(rSet.getString("NOME").equals("DevCx")){
					metaDevCx = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("DevNf")){
					metaDevNf = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Jornada_liquida_mapas")){
					metaJornadaLiquidaMapas = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Jornada_liquida_hora")){
					metaJornadaLiquidaHoras = LocalTime.parse(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_interno_mapas")){
					metaTempoInternoMapas = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_interno_hora")){
					metaTempoInternoHoras = LocalTime.parse(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_rota_mapas")){
					metaTempoRotaMapas = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_rota_hora")){
					metaTempoRotaHoras = LocalTime.parse(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_largada_mapas")){
					metaTempoLargadaMapas = Double.parseDouble(rSet.getString("VALOR"));
				}else if(rSet.getString("NOME").equals("Tempo_largada_hora")){
					metaTempoLargadaHoras = LocalTime.parse(rSet.getString("VALOR"));
				}}}

		finally {
			closeConnection(conn, stmt, rSet);
		}	}



	public void createDevCx (ResultSet rSet) throws SQLException{
		DevolucaoCxHolder devCaixa = new DevolucaoCxHolder();
		List<ItemDevolucaoCx> listDevCx = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();

			itemDevolucaoCx.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
			itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
			itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
			itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
			listDevCx.add(itemDevolucaoCx);

			carregadasTotal = carregadasTotal + itemDevolucaoCx.getCarregadas();
			entreguesTotal = entreguesTotal + itemDevolucaoCx.getEntregues();
			devolvidasTotal = devolvidasTotal + itemDevolucaoCx.getDevolvidas();		
		}

		devCaixa.setListItemDevolucao(listDevCx);
		devCaixa.setCarregadasTotal(carregadasTotal);
		devCaixa.setDevolvidasTotal(devolvidasTotal);
		devCaixa.setEntreguesTotal(entreguesTotal);
		devCaixa.setResultadoTotal(devolvidasTotal / carregadasTotal);
		devCaixa.setMeta(metaDevCx);

		indicadorHolder.setDevCaixa(devCaixa);
		rSet.first();
	}

	public void createDevNf (ResultSet rSet) throws SQLException{
		DevolucaoNfHolder devNf = new DevolucaoNfHolder();
		List<ItemDevolucaoNf> listDevNf = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();

			itemDevolucaoNf.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemDevolucaoNf.setCarregadas(rSet.getDouble("CXCARREG"));
			itemDevolucaoNf.setEntregues(rSet.getDouble("CXENTREG"));
			itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
			itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
			listDevNf.add(itemDevolucaoNf);

			carregadasTotal = carregadasTotal + itemDevolucaoNf.getCarregadas();
			entreguesTotal = entreguesTotal + itemDevolucaoNf.getEntregues();
			devolvidasTotal = devolvidasTotal + itemDevolucaoNf.getDevolvidas();		
		}

		devNf.setListItemDevolucao(listDevNf);
		devNf.setCarregadasTotal(carregadasTotal);
		devNf.setDevolvidasTotal(devolvidasTotal);
		devNf.setEntreguesTotal(entreguesTotal);
		devNf.setResultadoTotal(devolvidasTotal / carregadasTotal);
		devNf.setMeta(metaDevNf);

		indicadorHolder.setDevNf(devNf);
		rSet.first();

	}

	public void createTempoInterno(ResultSet rSet) throws SQLException{

		TempoInternoHolder tempoInternoHolder = new TempoInternoHolder();
		List<ItemTempoInterno> listTempoInterno = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		LocalTime hrSaida;
		LocalTime tempoInterno;

		while(rSet.next()){
			ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
			itemTempoInterno.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemTempoInterno.setHrEntrada(TimeUtils.toLocalTime((rSet.getTime("HRENTR"))));
			hrSaida = TimeUtils.toLocalTime(rSet.getTimestamp("HRSAIDA"));
			tempoInterno = TimeUtils.toLocalTime(rSet.getTime("TEMPOINTERNO"));
			itemTempoInterno.setHrFechamento( TimeUtils.somaHoras(hrSaida, tempoInterno));
			itemTempoInterno.setResultado(TimeUtils.differenceBetween(itemTempoInterno.getHrEntrada(), itemTempoInterno.getHrFechamento()));
			listTempoInterno.add(itemTempoInterno);
			totalMapas = totalMapas + 1;
			if(bateuMeta(metaTempoInternoHoras, itemTempoInterno.getResultado())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoInternoHolder.setListItemTempoInterno(listTempoInterno);
		tempoInternoHolder.setResultado(tempoInternoHolder.getMapasOk() / tempoInternoHolder.getTotalMapas());
		tempoInternoHolder.setMeta(metaTempoInternoMapas);
		rSet.first();
	}

	public void createTempoRota(ResultSet rSet) throws SQLException{

		TempoRotaHolder tempoRotaHolder = new TempoRotaHolder();
		List<ItemTempoRota> listTempoRota = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;

		while(rSet.next()){
			ItemTempoRota itemTempoRota = new ItemTempoRota();
			itemTempoRota.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemTempoRota.setHrEntrada(TimeUtils.toLocalTime((rSet.getTime("HRENTR"))));
			itemTempoRota.setHrSaida(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAIDA")));
			// saber o tempo que o caminhão ficou na rua, porisso hora de entrada(volta da rota) = hora de saída( saída para rota)
			itemTempoRota.setResultado(TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
			listTempoRota.add(itemTempoRota);
			totalMapas = totalMapas + 1;
			if(bateuMeta(metaTempoRotaHoras, itemTempoRota.getResultado())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoRotaHolder.setListTempoRota(listTempoRota);
		tempoRotaHolder.setResultado(tempoRotaHolder.getMapasOk() / tempoRotaHolder.getTotalMapas());
		tempoRotaHolder.setMeta(metaTempoRotaMapas);
		rSet.first();

	}

	public void createTempoLargada(ResultSet rSet) throws SQLException{

		TempoLargadaHolder tempoLargadaHolder = new TempoLargadaHolder();
		List<ItemTempoLargada> listTempoLargada = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;

		while(rSet.next()){
			
			ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
			itemTempoLargada.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemTempoLargada.setHrMatinal(TimeUtils.toLocalTime((rSet.getTime("HRMATINAL"))));
			itemTempoLargada.setHrSaida(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAIDA")));
			itemTempoLargada.setResultado(calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
			listTempoLargada.add(itemTempoLargada);
			totalMapas = totalMapas + 1;
			if(bateuMeta(metaTempoLargadaHoras, itemTempoLargada.getResultado())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoLargadaHolder.setListTempoLargada(listTempoLargada);
		tempoLargadaHolder.setResultado(tempoLargadaHolder.getMapasOk() / tempoLargadaHolder.getTotalMapas());
		tempoLargadaHolder.setMeta(metaTempoLargadaMapas);
		rSet.first();
	}		


	public void createJornadaLiquida(ResultSet rSet) throws SQLException{

		JornadaLiquidaHolder jornadaLiquidaHolder = new JornadaLiquidaHolder();
		List<ItemJornadaLiquida> listJornadaLiquida = new ArrayList<>();
		LocalTime matinal;
		LocalTime rota;
		LocalTime tempoInterno;
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;

		while (rSet.next()){
			tempoInterno = TimeUtils.toLocalTime(rSet.getTime("TEMPOINTERNO"));
			rota = TimeUtils.differenceBetween(TimeUtils.toLocalTime(rSet.getTimestamp("HRENTR")),
					TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")));
			matinal = calculaTempoLargada(TimeUtils.toLocalTime(rSet.getTimestamp("HRSAI")),
					TimeUtils.toLocalTime(rSet.getTime("HRMATINAL")));
			ItemJornadaLiquida itemJornadaLiquida = new ItemJornadaLiquida();
			itemJornadaLiquida.setData(DateUtils.toLocalDate(rSet.getDate("DATA")));
			itemJornadaLiquida.setTempoInterno(tempoInterno);
			itemJornadaLiquida.setTempoRota(rota);
			itemJornadaLiquida.setTempoLargada(matinal);
			itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota),tempoInterno));
			listJornadaLiquida.add(itemJornadaLiquida);
			totalMapas = totalMapas + 1;
			if(bateuMeta(metaJornadaLiquidaHoras, itemJornadaLiquida.getResultado())){
				mapasOk = mapasOk + 1;
			}else{ mapasNok = mapasNok + 1;}
		}
		jornadaLiquidaHolder.setListJornadaLiquida(listJornadaLiquida);
		jornadaLiquidaHolder.setResultado(jornadaLiquidaHolder.getMapasOk() / jornadaLiquidaHolder.getTotalMapas());
		jornadaLiquidaHolder.setMeta(metaJornadaLiquidaMapas);
		rSet.first();
	}

	private boolean bateuMeta (LocalTime meta, LocalTime resultado){
		//TODO: IMPLEMENTAR!
		//if(resultado<=meta){
		//return true;
		//} else {
		return false;
	}


	private LocalTime calculaTempoLargada (LocalTime hrSaida, LocalTime hrMatinal){

		LocalTime matinal = LocalTime.of(00, 30, 00);

		if(hrMatinal.isAfter(hrSaida)){
			return matinal;
		} else {
			return hrSaida.minus(hrMatinal.getLong(ChronoField.MILLI_OF_DAY),ChronoUnit.MILLIS);
		}
	}


}

