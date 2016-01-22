package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.indicador.DevolucaoCxHolder;
import br.com.zalf.prolog.models.indicador.DevolucaoHlHolder;
import br.com.zalf.prolog.models.indicador.DevolucaoNfHolder;
import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoCx;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoHl;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoNf;
import br.com.zalf.prolog.models.indicador.ItemTempoInterno;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.indicador.TempoInternoHolder;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.webservice.DataBaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.RelatorioDao;

public class RelatorioDaoImpl extends DataBaseConnection implements RelatorioDao{

	private static final String BUSCA_INDICADORES_BY_EQUIPE = "";

	private static final String BUSCA_INDICADORES_BY_UNIDADE = "SELECT "
			+ " M.DATA, M.CXCARREG, M.CXENTREG, M.QTHLCARREGADOS, M.QTHLENTREGUES, "
			+ " M.QTNFCARREGADAS, M.QTNFENTREGUES, M.HRSAI, M.HRENTR, M.TEMPOINTERNO, "
			+ " M.HRMATINAL, TRACKING.TOTAL AS TOTAL_TRACKING, TRACKING.APONTAMENTO_OK "
			+ " FROM MAPA M join token_autenticacao ta on ? = ta.cpf_colaborador and ? = ta.token "
			+ " JOIN VEICULO V ON V.PLACA = M.PLACA "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK "
			+ "FROM tracking t join MAPA M on m.mapa = t.mapa "
			+ "join (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as apontamentos_ok	"
			+ "from tracking t where t.disp_apont_cadastrado <= '0.3'	"
			+ "group by t.mapa) as ok on mapa_ok = t.mapa "
			+ "join (SELECT t.mapa as total_entregas, count(t.cod_cliente) as total from tracking t	group by t.mapa) as total on total_entregas = t.mapa "
			+ "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA WHERE m.cod_unidade = ? AND DATA BETWEEN ? AND ? "
			+ " ORDER BY M.DATA";		



	private Meta meta;
	private IndicadorHolder indicadorHolder = new IndicadorHolder();



	@Override
	public IndicadorHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe, Long cpf, 
			String token) throws SQLException {
		return indicadorHolder;

	}


	@Override
	public IndicadorHolder getIndicadoresEquipeByUnidade(LocalDate dataInicial, LocalDate dataFinal,
			int codUnidade, Long cpf, String token) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDao metasDao = new MetasDao();
		meta = metasDao.getMetas(cpf);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES_BY_UNIDADE, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			// Token autenticação
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, codUnidade);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));

			rSet = stmt.executeQuery();
			// ok createDevCx(rSet);
			// ok createDevNf(rSet);
			// ok createDevHl(rSet);
			createTempoInterno(rSet);
			/*createTempoRota(rSet);
			createTempoLargada(rSet);
			createJornadaLiquida(rSet);
			createTracking(rSet);*/

		} finally {
			closeConnection(conn, stmt, rSet);
		}

		return indicadorHolder;
	}


	private void createDevCx(ResultSet rSet) throws SQLException {

		DevolucaoCxHolder devCaixa = new DevolucaoCxHolder();
		List<ItemDevolucaoCx> listDevCx = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;
		int ultimaPosicao = -1;
		ItemDevolucaoCx itemTemp = new ItemDevolucaoCx();
		while (rSet.next()) {
			double entregues = rSet.getDouble("CXENTREG");
			double carregadas = rSet.getDouble("CXCARREG");
			if(listDevCx.size() > 0 && listDevCx.get(ultimaPosicao).getData().getTime() == rSet.getDate("DATA").getTime()){
				itemTemp = listDevCx.get(ultimaPosicao);
				itemTemp.setCarregadas(itemTemp.getCarregadas() + carregadas);
				itemTemp.setEntregues(itemTemp.getEntregues() + entregues);
				itemTemp.setDevolvidas(itemTemp.getCarregadas() - itemTemp.getEntregues());
				itemTemp.setResultado(itemTemp.getDevolvidas() / itemTemp.getCarregadas() );
			}else{
				ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();
				itemDevolucaoCx.setData(rSet.getDate("DATA"));
				itemDevolucaoCx.setCarregadas(carregadas);
				itemDevolucaoCx.setEntregues(entregues);
				itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
				itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
				itemDevolucaoCx.setMeta(meta.getMetaDevCx());
				itemDevolucaoCx.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoCx.getResultado(), meta.getMetaDevCx()));
				listDevCx.add(itemDevolucaoCx);
				ultimaPosicao++;
			}
			carregadasTotal = carregadasTotal + carregadas;
			entreguesTotal = entreguesTotal + entregues;
			devolvidasTotal = devolvidasTotal + (carregadas - entregues);
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
	
	
	private void createDevNf(ResultSet rSet) throws SQLException {

		DevolucaoNfHolder devNf = new DevolucaoNfHolder();
		List<ItemDevolucaoNf> listDevNf = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;
		int ultimaPosicao = -1;
		ItemDevolucaoNf itemTemp = new ItemDevolucaoNf();
		while (rSet.next()) {
			double entregues = rSet.getDouble("QTNFENTREGUES");
			double carregadas = rSet.getDouble("QTNFCARREGADAS");
			if(listDevNf.size() > 0 && listDevNf.get(ultimaPosicao).getData().getTime() == rSet.getDate("DATA").getTime()){
				itemTemp = listDevNf.get(ultimaPosicao);
				itemTemp.setCarregadas(itemTemp.getCarregadas() + carregadas);
				itemTemp.setEntregues(itemTemp.getEntregues() + entregues);
				itemTemp.setDevolvidas(itemTemp.getCarregadas() - itemTemp.getEntregues());
				itemTemp.setResultado(itemTemp.getDevolvidas() / itemTemp.getCarregadas() );
			}else{
				ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();
				itemDevolucaoNf.setData(rSet.getDate("DATA"));
				itemDevolucaoNf.setCarregadas(carregadas);
				itemDevolucaoNf.setEntregues(entregues);
				itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
				itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
				itemDevolucaoNf.setMeta(meta.getMetaDevNf());
				itemDevolucaoNf.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoNf.getResultado(), meta.getMetaDevNf()));
				listDevNf.add(itemDevolucaoNf);
				ultimaPosicao++;
			}
			carregadasTotal = carregadasTotal + carregadas;
			entreguesTotal = entreguesTotal + entregues;
			devolvidasTotal = devolvidasTotal + (carregadas - entregues);
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
	
	private void createDevHl(ResultSet rSet) throws SQLException {

		DevolucaoHlHolder devHl = new DevolucaoHlHolder();
		List<ItemDevolucaoHl> listDevHl = new ArrayList<>();
		double carregadasTotal = 0;
		double entreguesTotal = 0;
		double devolvidasTotal = 0;
		int ultimaPosicao = -1;
		ItemDevolucaoHl itemTemp = new ItemDevolucaoHl();
		while (rSet.next()) {
			double entregues = rSet.getDouble("QTHLENTREGUES");
			double carregadas = rSet.getDouble("QTHLCARREGADOS");
			if(listDevHl.size() > 0 && listDevHl.get(ultimaPosicao).getData().getTime() == rSet.getDate("DATA").getTime()){
				itemTemp = listDevHl.get(ultimaPosicao);
				itemTemp.setCarregadas(itemTemp.getCarregadas() + carregadas);
				itemTemp.setEntregues(itemTemp.getEntregues() + entregues);
				itemTemp.setDevolvidas(itemTemp.getCarregadas() - itemTemp.getEntregues());
				itemTemp.setResultado(itemTemp.getDevolvidas() / itemTemp.getCarregadas() );
			}else{
				ItemDevolucaoHl itemDevolucaoHl = new ItemDevolucaoHl();
				itemDevolucaoHl.setData(rSet.getDate("DATA"));
				itemDevolucaoHl.setCarregadas(carregadas);
				itemDevolucaoHl.setEntregues(entregues);
				itemDevolucaoHl.setDevolvidas(itemDevolucaoHl.getCarregadas() - itemDevolucaoHl.getEntregues());
				itemDevolucaoHl.setResultado(itemDevolucaoHl.getDevolvidas() / itemDevolucaoHl.getCarregadas());
				itemDevolucaoHl.setMeta(meta.getMetaDevHl());
				itemDevolucaoHl.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoHl.getResultado(), meta.getMetaDevNf()));
				listDevHl.add(itemDevolucaoHl);
				ultimaPosicao++;
			}
			carregadasTotal = carregadasTotal + carregadas;
			entreguesTotal = entreguesTotal + entregues;
			devolvidasTotal = devolvidasTotal + (carregadas - entregues);
		}
		devHl.setListItemDevolucao(listDevHl);
		devHl.setCarregadasTotal(carregadasTotal);
		devHl.setDevolvidasTotal(devolvidasTotal);
		devHl.setEntreguesTotal(entreguesTotal);
		devHl.setResultadoTotal(devolvidasTotal / carregadasTotal);
		devHl.setMeta(meta.getMetaDevHl());
		devHl.setBateuMeta(MetaUtils.bateuMeta(devHl.getResultadoTotal(), meta.getMetaDevHl()));
		indicadorHolder.setDevHl(devHl);
		rSet.beforeFirst();
	}
	
	public void createTempoInterno(ResultSet rSet) throws SQLException {

		TempoInternoHolder tempoInternoHolder = new TempoInternoHolder();
		List<ItemTempoInterno> listTempoInterno = new ArrayList<>();
		int totalMapas = 0;
		int mapasOk = 0;
		int mapasNok = 0;
		Time tempoInterno;
		int ultimaPosicao = -1;
		ItemTempoInterno itemTemp = new ItemTempoInterno();
		while (rSet.next()) {
			if(listTempoInterno.size() > 0 && rSet.getDate("DATA").getTime() == listTempoInterno.get(ultimaPosicao).getData().getTime()){
				itemTemp = listTempoInterno.get(ultimaPosicao);
				itemTemp.setHrEntrada(new Time((itemTemp.getHrEntrada().getTime() + rSet.getTime("HRENTR").getTime())/2));
				
		}
			
			ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
			itemTempoInterno.setData(rSet.getDate("DATA"));
			itemTempoInterno.setHrEntrada((rSet.getTime("HRENTR")));
			tempoInterno = rSet.getTime("TEMPOINTERNO");
			// entrada + tempo interno = horario do fechamento
			itemTempoInterno.setHrFechamento(TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), tempoInterno));
			itemTempoInterno.setResultado(tempoInterno);
			itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
			itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(tempoInterno, meta.getMetaTempoInternoHoras()));
			listTempoInterno.add(itemTempoInterno);
			totalMapas = totalMapas + 1;
			if (MetaUtils.bateuMeta(itemTempoInterno.getResultado(), meta.getMetaTempoInternoHoras())) {
				mapasOk = mapasOk + 1;
			} else {
				mapasNok = mapasNok + 1;
			} 
		}
		tempoInternoHolder.setTotalMapas(totalMapas);
		tempoInternoHolder.setMapasOk(mapasOk);
		tempoInternoHolder.setMapasNok(mapasNok);
		tempoInternoHolder.setListItemTempoInterno(listTempoInterno);
		tempoInternoHolder
				.setResultado((double) tempoInternoHolder.getMapasOk() / (double) tempoInternoHolder.getTotalMapas());
		tempoInternoHolder.setMeta(meta.getMetaTempoInternoMapas());
		tempoInternoHolder.setBateuMeta(
				MetaUtils.bateuMetaMapas(tempoInternoHolder.getResultado(), meta.getMetaTempoInternoMapas()));
		indicadorHolder.setTempoInterno(tempoInternoHolder);
		rSet.beforeFirst();
	}


















}
