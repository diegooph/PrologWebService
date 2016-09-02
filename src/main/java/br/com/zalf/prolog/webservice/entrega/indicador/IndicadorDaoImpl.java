package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicadorOlder.IndicadorDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class IndicadorDaoImpl extends DatabaseConnection implements IndicadorDao {

	private static final String BUSCA_INDICADORES = "SELECT M.DATA, M.CXCARREG, "
			+ "M.CXENTREG,M.QTHLCARREGADOS, M.QTHLENTREGUES, M.QTNFCARREGADAS, "
			+ "M.QTNFENTREGUES, M.HRSAI, M.HRENTR,M.TEMPOINTERNO, M.HRMATINAL, "
			+ "TRACKING.TOTAL as TOTAL_TRACKING, TRACKING.APONTAMENTO_OK FROM "
			+ "MAPA_COLABORADOR MC JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE "
			+ "AND MC.COD_AMBEV = C.MATRICULA_AMBEV JOIN MAPA M ON M.MAPA = MC.MAPA JOIN "
			+ "TOKEN_AUTENTICACAO TA ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN "
			+ "LEFT JOIN( SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, "
			+ "ok.APONTAMENTOS_OK AS APONTAMENTO_OK from tracking t join mapa_colaborador mc "
			+ "on mc.mapa = t.mapa join (SELECT t.mapa as mapa_ok, count(t.disp_apont_cadastrado) "
			+ "as apontamentos_ok from tracking t where t.disp_apont_cadastrado <= '0.3' group by t.mapa) "
			+ "as ok on mapa_ok = t.mapa join (SELECT t.mapa as total_entregas, count(t.cod_cliente) as "
			+ "total from tracking t group by t.mapa) as total on total_entregas = t.mapa join colaborador "
			+ "c on c.matricula_ambev = mc.cod_ambev GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS "
			+ "TRACKING ON TRACKING_MAPA = M.MAPA WHERE C.CPF = ? AND DATA BETWEEN ? AND ? ORDER BY M.DATA;";

	private IndicadorHolder indicadorHolder = new IndicadorHolder();

	@Override
	public IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal, Long cpf, String token)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);

			rSet = stmt.executeQuery();
			if (rSet.next()) {
			}

		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.println(indicadorHolder);
		return indicadorHolder;
	}
}
