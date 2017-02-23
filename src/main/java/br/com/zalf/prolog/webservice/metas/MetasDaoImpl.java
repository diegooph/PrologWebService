package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.entrega.indicador.Meta;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetasDaoImpl extends DatabaseConnection implements MetasDao{

	private static final String BUSCA_METAS_BY_UNIDADE = "SELECT M.CODIGO AS COD_META, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND "
			+ "TA.TOKEN = ? "
			+ "WHERE MU.COD_UNIDADE = ? ORDER BY M.CODIGO";

	@Override
	public Meta getByCodUnidade(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS_BY_UNIDADE);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				return createMeta(rSet);
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public boolean update(Meta meta, Long codUnidade) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE UNIDADE_METAS SET " +
					"  META_DEV_HL = ?," +
					"  META_DEV_PDV = ?," +
					"  META_DEV_NF = ?," +
					"  META_TRACKING = ?," +
					"  META_RAIO_TRACKING = ?," +
					"  META_TEMPO_LARGADA_MAPAS = ?," +
					"  META_TEMPO_ROTA_MAPAS = ?," +
					"  META_TEMPO_INTERNO_MAPAS = ?," +
					"  META_JORNADA_LIQUIDA_MAPAS = ?," +
					"  META_TEMPO_LARGADA_HORAS = ?," +
					"  META_TEMPO_ROTA_HORAS = ?," +
					"  META_TEMPO_INTERNO_HORAS = ?," +
					"  META_JORNADA_LIQUIDA_HORAS = ?," +
					"  META_CAIXA_VIAGEM = ?," +
					"  META_DISPERSAO_KM = ?," +
					"  META_DISPERSAO_TEMPO = ? WHERE COD_UNIDADE = ?");
			stmt.setDouble(1, meta.metaDevHl);
			stmt.setDouble(2, meta.metaDevPdv);
			stmt.setDouble(3, meta.metaDevNf);
			stmt.setDouble(4, meta.metaTracking);
			stmt.setInt(5, meta.metaRaioTracking);
			stmt.setDouble(6, meta.metaTempoLargadaMapas);
			stmt.setDouble(7, meta.metaTempoRotaMapas);
			stmt.setDouble(8, meta.metaTempoInternoMapas);
			stmt.setDouble(9, meta.metaJornadaLiquidaMapas);
			stmt.setTime(10, meta.metaTempoLargadaHoras);
			stmt.setTime(11, meta.metaTempoRotaHoras);
			stmt.setTime(12, meta.metaTempoInternoHoras);
			stmt.setTime(13, meta.metaJornadaLiquidaHoras);
			stmt.setInt(14, meta.metaCaixaViagem);
			stmt.setDouble(15, meta.metaDispersaoKm);
			stmt.setDouble(16, meta.metaDispersaoTempo);
			stmt.setLong(17, codUnidade);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar as metas");
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private Meta createMeta (ResultSet rSet) throws SQLException {
		Meta meta = new Meta();
		meta.metaDevHl = rSet.getDouble("META_DEV_HL");
		meta.metaDevPdv = rSet.getDouble("META_DEV_PDV");
		meta.metaTracking = rSet.getDouble("META_TRACKING");
		meta.metaRaioTracking = rSet.getInt("META_RAIO_TRACKING");
		meta.metaTempoLargadaMapas = rSet.getDouble("META_TEMPO_LARGADA_MAPAS");
		meta.metaTempoRotaMapas = rSet.getDouble("META_TEMPO_ROTA_MAPAS");
		meta.metaTempoInternoMapas = rSet.getDouble("META_TEMPO_INTERNO_MAPAS");
		meta.metaJornadaLiquidaMapas = rSet.getDouble("META_JORNADA_LIQUIDA_MAPAS");
		meta.metaTempoLargadaHoras = rSet.getTime("META_TEMPO_LARGADA_HORAS");
		meta.metaTempoRotaHoras = rSet.getTime("META_TEMPO_ROTA_HORAS");
		meta.metaTempoInternoHoras = rSet.getTime("META_TEMPO_INTERNO_HORAS");
		meta.metaJornadaLiquidaHoras = rSet.getTime("META_JORNADA_LIQUIDA_HORAS");
		meta.metaCaixaViagem = rSet.getInt("META_CAIXA_VIAGEM");
		meta.metaDispersaoKm = rSet.getDouble("META_DISPERSAO_KM");
		meta.metaDispersaoTempo = rSet.getDouble("META_DISPERSAO_TEMPO");
		meta.metaDevNf = rSet.getDouble("META_DEV_NF");
		return meta;
	}
}
