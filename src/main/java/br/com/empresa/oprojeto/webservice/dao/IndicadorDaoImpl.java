package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.indicador.DevolucaoCxHolder;
import br.com.empresa.oprojeto.models.indicador.DevolucaoNfHolder;
import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;
import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoCx;
import br.com.empresa.oprojeto.models.indicador.ItemDevolucaoNf;
import br.com.empresa.oprojeto.models.indicador.ItemTempoInterno;
import br.com.empresa.oprojeto.models.indicador.TempoInternoHolder;
import br.com.empresa.oprojeto.models.util.DateUtil;
import br.com.empresa.oprojeto.models.util.TimeUtils;
import br.com.empresa.oprojeto.webservice.dao.interfaces.IndicadorDao;

public class IndicadorDaoImpl extends DataBaseConnection implements IndicadorDao {
	//Códigos das metas no BD
	private static final int COD_DEVCX = 1;
	private static final int COD_DEVNF = 2;
	private static final int COD_JORNADA = 3;
	private static final int COD_TEMPO_INTERNO = 4;
	private static final int COD_TEMPO_EM_ROTA = 5;
	private static final int COD_TEMPO_LARGADA = 6;
	private static final int COD_TRACKING = 7;

	private static final String BUSCA_INDICADORES = "SELECT M.DATA, M.CXCARREG, "
			+ "M.CXENTREG, M.QTNFCARREGADAS, M.QTNFENTREGUES, M.HRSAI, M.HRENTR,"
			+ " M.TEMPOINTERNO, M.HRMATINAL FROM MAPA_COLABORADOR MC JOIN "
			+ "COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND MC.COD_AMBEV "
			+ "= C.MATRICULA_AMBEV JOIN MAPA M ON M.MAPA = MC.MAPA WHERE C.CPF = "
			+ "? AND DATA BETWEEN ? AND ? ORDER BY M.DATA";

	private IndicadorHolder indicadorHolder = new IndicadorHolder();

	private LocalTime metaTempoInternoHoras = LocalTime.now();

	@Override
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal, long cpf)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtil.toSqlDate(dataInicial));
			stmt.setDate(3, DateUtil.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();

			createDevCx(rSet);
			createDevNf(rSet);
			createTempoInterno(rSet);

		}
		finally {
			closeConnection(conn, stmt, null);
		}	

		return null;
	}



	public void createDevCx (ResultSet rSet) throws SQLException{
		DevolucaoCxHolder devCaixa = new DevolucaoCxHolder();
		List<ItemDevolucaoCx> listDevCx = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();

			itemDevolucaoCx.setData(DateUtil.toLocalDate(rSet.getDate("DATA")));
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

		indicadorHolder.setDevCaixa(devCaixa);

	}

	public void createDevNf (ResultSet rSet) throws SQLException{
		DevolucaoNfHolder devNf = new DevolucaoNfHolder();
		List<ItemDevolucaoNf> listDevNf = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;

		while(rSet.next()){

			ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();

			itemDevolucaoNf.setData(DateUtil.toLocalDate(rSet.getDate("DATA")));
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

		indicadorHolder.setDevNf(devNf);

	}

	public void createTempoInterno(ResultSet rSet) throws SQLException{

		TempoInternoHolder tempoInternoHolder = new TempoInternoHolder();
		List<ItemTempoInterno> listTempoInterno = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		double resultado = 0;
		LocalTime hrSaida;
		LocalTime tempoInterno;

		while(rSet.next()){
			ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
			itemTempoInterno.setData(DateUtil.toLocalDate(rSet.getDate("DATA")));
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


	}



private boolean bateuMeta (LocalTime meta, LocalTime resultado){
	//TODO: IMPLEMENTAR!
	//if(resultado<meta){
	//return true;
	//} else {
	return false;


}
}

