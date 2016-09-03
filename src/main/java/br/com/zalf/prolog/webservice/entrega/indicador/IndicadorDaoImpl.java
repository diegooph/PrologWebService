package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.models.indicador.indicadores.item.*;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndicadorDaoImpl extends DatabaseConnection{

	private static final String BUSCA_INDICADORES = "SELECT\n" +
			"  M.DATA,  M.mapa,M.cxcarreg,    M.QTHLCARREGADOS,  M.QTHLENTREGUES,  M.entregascompletas,  M.entregasnaorealizadas,\n" +
			"  M.HRSAI,  M.HRENTR,  M.TEMPOINTERNO,  M.HRMATINAL,  TRACKING.TOTAL AS TOTAL_TRACKING,  TRACKING.APONTAMENTO_OK\n" +
			"FROM\n" +
			"  MAPA_COLABORADOR MC\n" +
			"  JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND MC.COD_AMBEV = C.MATRICULA_AMBEV\n" +
			"  JOIN MAPA M ON M.MAPA = MC.MAPA\n" +
			"  JOIN UNIDADE U ON U.CODIGO = M.cod_unidade\n" +
			"  JOIN EMPRESA EM ON EM.codigo = U.cod_empresa\n" +
			"  JOIN regional R ON R.codigo = U.cod_regional\n" +
			"  JOIN unidade_metas um on um.cod_unidade = u.cod_unidade\n" +
			"  JOIN equipe E ON E.cod_unidade = U.codigo AND C.cod_equipe = E.codigo AND C.cod_unidade = E.cod_unidade\n" +
			"  LEFT JOIN (SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK\n" +
			"              FROM tracking t\n" +
			"                JOIN mapa_colaborador mc ON mc.mapa = t.mapa\n" +
			"                JOIN (SELECT t.mapa AS mapa_ok, count(t.disp_apont_cadastrado) AS apontamentos_ok\n" +
			"                FROM tracking t\n" +
			"                  JOIN unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
			"                WHERE t.disp_apont_cadastrado <= um.meta_raio_tracking\n" +
			"                GROUP BY t.mapa) AS ok ON mapa_ok = t.mapa\n" +
			"            JOIN (SELECT t.mapa AS total_entregas, count(t.cod_cliente) AS total\n" +
			"             FROM tracking t\n" +
			"             GROUP BY t.mapa) AS total ON total_entregas = t.mapa\n" +
			"             GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA\n" +
			"WHERE\n" +
			"  DATA BETWEEN ? AND ? AND\n" +
			"  R.codigo::TEXT LIKE ? AND\n" +
			"  U.codigo::TEXT LIKE ? AND\n" +
			"  E.codigo::TEXT LIKE ? AND\n" +
			"  C.CPF::TEXT LIKE ? \n" +
			"ORDER BY M.DATA;\n";


	public List<Indicador> getEstratoIndicador(Long dataInicial, Long dataFinal, String codRegional, String codEmpresa,
											   String codUnidade, String equipe, String cpf, String indicador) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Indicador> itens = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setString(3, codRegional);
			stmt.setString(4, codUnidade);
			stmt.setString(5, codEmpresa);
			stmt.setString(6, cpf);
			rSet = stmt.executeQuery();
			itens = createExtratoIndicador(rSet, indicador);

		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.println(itens);
		return itens;
	}

	private List<Indicador> createExtratoIndicador(ResultSet rSet, String indicador) throws SQLException{

		if (indicador.equals(CaixaViagem.CAIXA_VIAGEM)){
			return Converter.createExtratoCaixaViagem(rSet);
		}else if(indicador.equals(DevHl.DEVOLUCAO_HL)){
			//return createExtratoDevHl(rSet);
		}else if(indicador.equals(DevPdv.DEVOLUCAO_PDV)){
			//return createExtratoDevPdv(rSet);
		}else if(indicador.equals(DispersaoKm.DISPERSAO_KM)){
			//return createExtratoDispersaoKm(rSet);
		}else if(indicador.equals(Tracking.TRACKING)){
			//return createExtratoTracking(rSet);
		}else if(indicador.equals(DispersaoTempo.DISPERSAO_TEMPO)){
			//return createExtratoDispersaoTempo(rSet);
		}else if(indicador.equals(Jornada.JORNADA)){
			//return createExtratoJornada(rSet);
		}else if(indicador.equals(TempoInterno.TEMPO_INTERNO)){
			//return createExtratoTempoInterno(rSet);
		}else if(indicador.equals(TempoLargada.TEMPO_LARGADA)){
			//return createExtratoTempoLargada(rSet);
		}else if(indicador.equals(TempoRota.TEMPO_ROTA)) {
			//return createExtratoTempoRota(rSet);
		}
		return new ArrayList<>();
	}
}
