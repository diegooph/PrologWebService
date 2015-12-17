package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
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
import br.com.empresa.oprojeto.models.indicador.Meta;
import br.com.empresa.oprojeto.models.indicador.TempoInternoHolder;
import br.com.empresa.oprojeto.models.indicador.TempoLargadaHolder;
import br.com.empresa.oprojeto.models.indicador.TempoRotaHolder;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.models.util.MetaUtils;
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

	
	private Meta meta;
	private IndicadorHolder indicadorHolder = new IndicadorHolder();
	
	@Override
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal, long cpf)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDao metasDao = new MetasDao();
		meta = metasDao.getMetas(cpf);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
	
	
	public void createDevCx (ResultSet rSet) throws SQLException{
		DevolucaoCxHolder devCaixa = new DevolucaoCxHolder();
		List<ItemDevolucaoCx> listDevCx = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();

			itemDevolucaoCx.setData(rSet.getDate("DATA"));
			itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
			itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
			itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
			itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
			itemDevolucaoCx.setMeta(meta.getMetaDevCx());
			itemDevolucaoCx.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoCx.getResultado(), meta.getMetaDevCx()));
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
		devCaixa.setMeta(meta.getMetaDevCx());
		devCaixa.setBateuMeta(MetaUtils.bateuMeta(devCaixa.getResultadoTotal(), meta.getMetaDevCx()));

		indicadorHolder.setDevCaixa(devCaixa);
		rSet.beforeFirst();
	}

	public void createDevNf (ResultSet rSet) throws SQLException{
		DevolucaoNfHolder devNf = new DevolucaoNfHolder();
		List<ItemDevolucaoNf> listDevNf = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();

			itemDevolucaoNf.setData(rSet.getDate("DATA"));
			itemDevolucaoNf.setCarregadas(rSet.getDouble("QTNFCARREGADAS"));
			itemDevolucaoNf.setEntregues(rSet.getDouble("QTNFENTREGUES"));
			itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
			itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
			itemDevolucaoNf.setMeta(meta.getMetaDevNf());
			itemDevolucaoNf.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoNf.getResultado(), meta.getMetaDevNf()));
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
		devNf.setMeta(meta.getMetaDevNf());
		devNf.setBateuMeta(MetaUtils.bateuMeta(devNf.getResultadoTotal(), meta.getMetaDevNf()));

		indicadorHolder.setDevNf(devNf);
		rSet.beforeFirst();

	}

	public void createTempoInterno(ResultSet rSet) throws SQLException{

		TempoInternoHolder tempoInternoHolder = new TempoInternoHolder();
		List<ItemTempoInterno> listTempoInterno = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		Time tempoInterno;

		while(rSet.next()){
			ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
			itemTempoInterno.setData(rSet.getDate("DATA"));
			itemTempoInterno.setHrEntrada((rSet.getTime("HRENTR")));
			tempoInterno = rSet.getTime("TEMPOINTERNO");
			// entrada + tempo interno = horario do fechamento
			itemTempoInterno.setHrFechamento(
					TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), tempoInterno));
			itemTempoInterno.setResultado(tempoInterno);
			itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
			itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(
					tempoInterno, meta.getMetaTempoInternoHoras()));
			listTempoInterno.add(itemTempoInterno);
			totalMapas = totalMapas + 1;
			if(MetaUtils.bateuMeta(itemTempoInterno.getResultado(),meta.getMetaTempoInternoHoras())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoInternoHolder.setTotalMapas(totalMapas);
		tempoInternoHolder.setMapasOk(mapasOk);
		tempoInternoHolder.setMapasNok(mapasNok);
		tempoInternoHolder.setListItemTempoInterno(listTempoInterno);
		tempoInternoHolder.setResultado((double)tempoInternoHolder.getMapasOk() / (double)tempoInternoHolder.getTotalMapas());
		tempoInternoHolder.setMeta(meta.getMetaTempoInternoMapas());
		tempoInternoHolder.setBateuMeta(MetaUtils.bateuMetaMapas(tempoInternoHolder.getResultado(), meta.getMetaTempoInternoMapas()));
		indicadorHolder.setTempoInterno(tempoInternoHolder);
		rSet.beforeFirst();
	}

	public void createTempoRota(ResultSet rSet) throws SQLException{
		TempoRotaHolder tempoRotaHolder = new TempoRotaHolder();
		List<ItemTempoRota> listTempoRota = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		while(rSet.next()){
			ItemTempoRota itemTempoRota = new ItemTempoRota();
			itemTempoRota.setData(rSet.getDate("DATA"));
			itemTempoRota.setHrEntrada(rSet.getTime("HRENTR"));
			itemTempoRota.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
			// saber o tempo que o caminhão ficou na rua, por isso hora de entrada(volta da rota) = hora de saída( saída para rota)
			itemTempoRota.setResultado(TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
			itemTempoRota.setMeta(meta.getMetaTempoRotaHoras());
			itemTempoRota.setBateuMeta(MetaUtils.bateuMeta(itemTempoRota.getResultado(), meta.getMetaTempoRotaHoras()));
			listTempoRota.add(itemTempoRota);
			totalMapas = totalMapas + 1;
			if(MetaUtils.bateuMeta(itemTempoRota.getResultado(),meta.getMetaTempoRotaHoras())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoRotaHolder.setTotalMapas(totalMapas);
		tempoRotaHolder.setMapasOk(mapasOk);
		tempoRotaHolder.setMapasNok(mapasNok);
		tempoRotaHolder.setListTempoRota(listTempoRota);
		tempoRotaHolder.setResultado((double)tempoRotaHolder.getMapasOk() / (double)tempoRotaHolder.getTotalMapas());
		tempoRotaHolder.setMeta(meta.getMetaTempoRotaMapas());
		tempoRotaHolder.setBateuMeta(MetaUtils.bateuMetaMapas(tempoRotaHolder.getResultado(), meta.getMetaTempoRotaMapas()));
		indicadorHolder.setTempoRota(tempoRotaHolder);
		rSet.beforeFirst();

	}

	public void createTempoLargada(ResultSet rSet) throws SQLException{

		TempoLargadaHolder tempoLargadaHolder = new TempoLargadaHolder();
		List<ItemTempoLargada> listTempoLargada = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;

		while(rSet.next()){
			
			ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
			itemTempoLargada.setData(rSet.getDate("DATA"));
			itemTempoLargada.setHrMatinal(rSet.getTime("HRMATINAL"));
			itemTempoLargada.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
			itemTempoLargada.setResultado(MetaUtils.calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
			itemTempoLargada.setMeta(meta.getMetaTempoLargadaHoras());
			itemTempoLargada.setBateuMeta(MetaUtils.bateuMeta(itemTempoLargada.getResultado(), meta.getMetaTempoLargadaHoras()));
			listTempoLargada.add(itemTempoLargada);
			totalMapas = totalMapas + 1;
			if(MetaUtils.bateuMeta(itemTempoLargada.getResultado(),meta.getMetaTempoLargadaHoras())){
				mapasOk = mapasOk + 1;
			}else{mapasNok = mapasNok + 1;}
		}
		tempoLargadaHolder.setTotalMapas(totalMapas);
		tempoLargadaHolder.setMapasOk(mapasOk);
		tempoLargadaHolder.setMapasNok(mapasNok);
		tempoLargadaHolder.setListTempoLargada(listTempoLargada);
		tempoLargadaHolder.setResultado((double)tempoLargadaHolder.getMapasOk() / (double)tempoLargadaHolder.getTotalMapas());
		tempoLargadaHolder.setMeta(meta.getMetaTempoLargadaMapas());
		tempoLargadaHolder.setBateuMeta(MetaUtils.bateuMetaMapas(tempoLargadaHolder.getResultado(), meta.getMetaTempoLargadaMapas()));
		indicadorHolder.setTempoLargada(tempoLargadaHolder);
		rSet.beforeFirst();
	}		


	public void createJornadaLiquida(ResultSet rSet) throws SQLException{

		JornadaLiquidaHolder jornadaLiquidaHolder = new JornadaLiquidaHolder();
		List<ItemJornadaLiquida> listJornadaLiquida = new ArrayList<>();
		Time matinal;
		Time rota;
		Time tempoInterno;
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;

		while (rSet.next()){
			tempoInterno = rSet.getTime("TEMPOINTERNO");
			rota = TimeUtils.differenceBetween(TimeUtils.toSqlTime(rSet.getTimestamp("HRENTR")),
					TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
			matinal = MetaUtils.calculaTempoLargada(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")),
					rSet.getTime("HRMATINAL"));
			ItemJornadaLiquida itemJornadaLiquida = new ItemJornadaLiquida();
			itemJornadaLiquida.setData(rSet.getDate("DATA"));
			itemJornadaLiquida.setTempoInterno(tempoInterno);
			itemJornadaLiquida.setTempoRota(rota);
			itemJornadaLiquida.setTempoLargada(matinal);
			itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota),tempoInterno));
			itemJornadaLiquida.setMeta(meta.getMetaJornadaLiquidaHoras());
			itemJornadaLiquida.setBateuMeta(MetaUtils.bateuMeta(itemJornadaLiquida.getResultado(), meta.getMetaJornadaLiquidaHoras()));
			listJornadaLiquida.add(itemJornadaLiquida);
			totalMapas = totalMapas + 1;
			if(MetaUtils.bateuMeta(itemJornadaLiquida.getResultado(),meta.getMetaJornadaLiquidaHoras())){
				mapasOk = mapasOk + 1;
			}else{ mapasNok = mapasNok + 1;}
		}
		jornadaLiquidaHolder.setTotalMapas(totalMapas);
		jornadaLiquidaHolder.setMapasOk(mapasOk);
		jornadaLiquidaHolder.setMapasNok(mapasNok);
		jornadaLiquidaHolder.setListJornadaLiquida(listJornadaLiquida);
		jornadaLiquidaHolder.setResultado((double)jornadaLiquidaHolder.getMapasOk() / (double)jornadaLiquidaHolder.getTotalMapas());
		jornadaLiquidaHolder.setMeta(meta.getMetaJornadaLiquidaMapas());
		jornadaLiquidaHolder.setBateuMeta(MetaUtils.bateuMetaMapas(jornadaLiquidaHolder.getResultado(), meta.getMetaJornadaLiquidaMapas()));
		indicadorHolder.setJornadaLiquida(jornadaLiquidaHolder);
		rSet.beforeFirst();
	}
}

