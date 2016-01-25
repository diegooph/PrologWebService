package br.com.zalf.prolog.webservice.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoCx;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoHl;
import br.com.zalf.prolog.models.indicador.ItemDevolucaoNf;
import br.com.zalf.prolog.models.indicador.ItemJornadaLiquida;
import br.com.zalf.prolog.models.indicador.ItemTempoInterno;
import br.com.zalf.prolog.models.indicador.ItemTempoLargada;
import br.com.zalf.prolog.models.indicador.ItemTempoRota;
import br.com.zalf.prolog.models.indicador.ItemTracking;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.ConsolidadoMapasDia;
import br.com.zalf.prolog.models.relatorios.Mapa;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.models.util.MetaUtils;
import br.com.zalf.prolog.models.util.TimeUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.RelatorioDao;
import br.com.zalf.prolog.webservice.imports.MapaImport;

public class RelatorioDaoImpl extends DatabaseConnection implements RelatorioDao {

	public static final String BUSCA_INDICADORES_EQUIPE = "SELECT M.DATA, M.PLACA, M.MAPA, C.NOME AS NOMEMOTORISTA, "
			+ "C1.NOME AS NOMEAJUD1, C2.NOME AS NOMEAJUD2, C.EQUIPE, "
			+ "M.CXCARREG, M.CXENTREG, (M.CXCARREG - M.CXENTREG) AS DEVCX, "
			+ "M.QTHLCARREGADOS, M.QTHLENTREGUES, (M.QTHLCARREGADOS - M.QTHLENTREGUES) AS DEVHL, "
			+ "M.QTNFCARREGADAS, M.QTNFENTREGUES, (M.QTNFCARREGADAS - M.QTNFENTREGUES) AS DEVNF, "
			+ "M.HRSAI, M.HRENTR, M.TEMPOINTERNO, M.HRMATINAL,TRACKING.TOTAL AS TOTAL_TRACKING, TRACKING.APONTAMENTO_OK "
			+ " FROM MAPA M join token_autenticacao ta on ? = ta.cpf_colaborador and ? = ta.token "
			+ "JOIN VEICULO V ON V.PLACA = M.PLACA "
			+ "JOIN COLABORADOR C ON M.MATRICMOTORISTA = C.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C1 ON M.MATRICAJUD1 = C1.MATRICULA_AMBEV "
			+ "JOIN COLABORADOR C2 ON M.MATRICAJUD2 = C2.MATRICULA_AMBEV "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK "
			+ "FROM tracking t join MAPA M on m.mapa = t.mapa "
			+ "JOIN (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) as apontamentos_ok "
			+ "FROM tracking t	WHERE t.disp_apont_cadastrado <= '0.3' "
			+ "GROUP BY t.mapa) as ok on mapa_ok = t.mapa "
			+ "JOIN (SELECT t.mapa as total_entregas, count(t.cod_cliente) as total from tracking t "
			+ "group by t.mapa) as total on total_entregas = t.mapa "
			+ "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA "
			+ "WHERE c.equipe = ? AND DATA BETWEEN ? AND ? "
			+ "ORDER BY M.DATA ";


	private Meta meta;
	private ConsolidadoHolder consolidadoHolder;


	@Override
	public IndicadorHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long cpf, String token) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		MetasDao metasDao = new MetasDao();
		meta = metasDao.getMetas(cpf);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES_EQUIPE, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf); 
			stmt.setString(2, token); 
			stmt.setString(3, equipe);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
			rSet = stmt.executeQuery();
			
			List<ConsolidadoMapasDia> listConsolidadoMapasDia = new ArrayList<>();
			ConsolidadoMapasDia consolidadoMapasDia = new ConsolidadoMapasDia();
			List<MapaImport> listMapas = new ArrayList<>();
			Mapa mapa = new Mapa();
			mapa = createMapa(rSet);
			
			
			
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return  new IndicadorHolder();
	}
	
	private Mapa createMapa(ResultSet rSet) throws SQLException{
		Mapa mapa = new Mapa();
		mapa.setNumeroMapa(rSet.getInt("MAPA"));
		mapa.setData(rSet.getDate("DATA"));
		mapa.setEquipe(rSet.getString("EQUIPE"));
		mapa.setMotorista(rSet.getString("NOMEMOTORISTA"));
		mapa.setAjudante1(rSet.getString("NOMEAJUD1"));
		mapa.setAjudante2(rSet.getString("NOMEAJUD2"));
		mapa.setPlaca(rSet.getString("PLACA"));
		mapa.setDevCx(createDevCx(rSet));
		mapa.setDevNf(createDevNf(rSet));
		mapa.setDevHl(createDevHl(rSet));
	    mapa.setTempoInterno(createTempoInterno(rSet));
	    mapa.setTempoRota(createTempoRota(rSet));
	    mapa.setTempoLargada(createTempoLargada(rSet));
	    mapa.setJornadaLiquida(createJornadaLiquida(rSet));
		mapa.setTracking(createTracking(rSet));
		
		return mapa;
	}
	
	private ItemDevolucaoCx createDevCx(ResultSet rSet) throws SQLException{
		ItemDevolucaoCx itemDevolucaoCx = new ItemDevolucaoCx();
		itemDevolucaoCx.setData(rSet.getDate("DATA"));
		itemDevolucaoCx.setCarregadas(rSet.getDouble("CXCARREG"));
		itemDevolucaoCx.setEntregues(rSet.getDouble("CXENTREG"));
		itemDevolucaoCx.setDevolvidas(itemDevolucaoCx.getCarregadas() - itemDevolucaoCx.getEntregues());
		itemDevolucaoCx.setResultado(itemDevolucaoCx.getDevolvidas() / itemDevolucaoCx.getCarregadas());
		itemDevolucaoCx.setMeta(meta.getMetaDevCx());
		itemDevolucaoCx.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoCx.getResultado(), meta.getMetaDevCx()));
		return itemDevolucaoCx;
	}
	
	private ItemDevolucaoNf createDevNf(ResultSet rSet) throws SQLException{
		ItemDevolucaoNf itemDevolucaoNf = new ItemDevolucaoNf();
		itemDevolucaoNf.setData(rSet.getDate("DATA"));
		itemDevolucaoNf.setCarregadas(rSet.getDouble("QTNFCARREGADAS"));
		itemDevolucaoNf.setEntregues(rSet.getDouble("QTNFENTREGUES"));
		itemDevolucaoNf.setDevolvidas(itemDevolucaoNf.getCarregadas() - itemDevolucaoNf.getEntregues());
		itemDevolucaoNf.setResultado(itemDevolucaoNf.getDevolvidas() / itemDevolucaoNf.getCarregadas());
		itemDevolucaoNf.setMeta(meta.getMetaDevNf());
		itemDevolucaoNf.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoNf.getResultado(), meta.getMetaDevNf()));
		return itemDevolucaoNf;
	}
	
	private ItemDevolucaoHl createDevHl(ResultSet rSet) throws SQLException{
		ItemDevolucaoHl itemDevolucaoHl = new ItemDevolucaoHl();
		itemDevolucaoHl.setData(rSet.getDate("DATA"));
		itemDevolucaoHl.setCarregadas(rSet.getDouble("QTHLCARREGADOS"));
		itemDevolucaoHl.setEntregues(rSet.getDouble("QTHLENTREGUES"));
		itemDevolucaoHl.setDevolvidas(itemDevolucaoHl.getCarregadas() - itemDevolucaoHl.getEntregues());
		itemDevolucaoHl.setResultado(itemDevolucaoHl.getDevolvidas() / itemDevolucaoHl.getCarregadas());
		itemDevolucaoHl.setMeta(meta.getMetaDevHl());
		itemDevolucaoHl.setBateuMeta(MetaUtils.bateuMeta(itemDevolucaoHl.getResultado(), meta.getMetaDevHl()));
		return itemDevolucaoHl;
	}
	
	private ItemTempoInterno createTempoInterno(ResultSet rSet) throws SQLException{
		ItemTempoInterno itemTempoInterno = new ItemTempoInterno();
		Time tempoInterno;
		itemTempoInterno.setData(rSet.getDate("DATA"));
		itemTempoInterno.setHrEntrada((rSet.getTime("HRENTR")));
		tempoInterno = rSet.getTime("TEMPOINTERNO");
		// entrada + tempo interno = horario do fechamento
		itemTempoInterno.setHrFechamento(TimeUtils.somaHoras(itemTempoInterno.getHrEntrada(), tempoInterno));
		itemTempoInterno.setResultado(tempoInterno);
		itemTempoInterno.setMeta(meta.getMetaTempoInternoHoras());
		itemTempoInterno.setBateuMeta(MetaUtils.bateuMeta(tempoInterno, meta.getMetaTempoInternoHoras()));
		return itemTempoInterno;
	}
	
	private ItemTempoRota createTempoRota(ResultSet rSet) throws SQLException{
		ItemTempoRota itemTempoRota = new ItemTempoRota();
		itemTempoRota.setData(rSet.getDate("DATA"));
		itemTempoRota.setHrEntrada(rSet.getTime("HRENTR"));
		itemTempoRota.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		// saber o tempo que o caminhão ficou na rua, por isso hora de
		// entrada(volta da rota) - hora de saída( saída para rota)
		itemTempoRota.setResultado(
				TimeUtils.differenceBetween(itemTempoRota.getHrEntrada(), itemTempoRota.getHrSaida()));
		itemTempoRota.setMeta(meta.getMetaTempoRotaHoras());
		itemTempoRota.setBateuMeta(MetaUtils.bateuMeta(itemTempoRota.getResultado(), meta.getMetaTempoRotaHoras()));
		return itemTempoRota;
	}
	
	private ItemTempoLargada createTempoLargada(ResultSet rSet) throws SQLException{
		ItemTempoLargada itemTempoLargada = new ItemTempoLargada();
		itemTempoLargada.setData(rSet.getDate("DATA"));
		itemTempoLargada.setHrMatinal(rSet.getTime("HRMATINAL"));
		itemTempoLargada.setHrSaida(TimeUtils.toSqlTime(rSet.getTimestamp("HRSAI")));
		itemTempoLargada.setResultado(
				MetaUtils.calculaTempoLargada(itemTempoLargada.getHrSaida(), itemTempoLargada.getHrMatinal()));
		itemTempoLargada.setMeta(meta.getMetaTempoLargadaHoras());
		itemTempoLargada.setBateuMeta(
				MetaUtils.bateuMeta(itemTempoLargada.getResultado(), meta.getMetaTempoLargadaHoras()));
		return itemTempoLargada;
	}
	
	private ItemJornadaLiquida createJornadaLiquida(ResultSet rSet) throws SQLException{
		Time matinal;
		Time rota;
		Time tempoInterno;
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
		itemJornadaLiquida.setResultado(TimeUtils.somaHoras(TimeUtils.somaHoras(matinal, rota), tempoInterno));
		itemJornadaLiquida.setMeta(meta.getMetaJornadaLiquidaHoras());
		itemJornadaLiquida.setBateuMeta(
				MetaUtils.bateuMeta(itemJornadaLiquida.getResultado(), meta.getMetaJornadaLiquidaHoras()));
		return itemJornadaLiquida;
	}
	
	private ItemTracking createTracking (ResultSet rSet) throws SQLException{
		
	}
	
	
	
	

	
}
