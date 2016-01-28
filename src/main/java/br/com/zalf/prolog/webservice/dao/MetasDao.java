package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.util.TimeUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class MetasDao extends DatabaseConnection{

	private static final String BUSCA_METAS = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO"
			+ " JOIN COLABORADOR C ON C.COD_UNIDADE = MU.COD_UNIDADE "
			+ "WHERE C.CPF = ? ORDER BY M.CODIGO";
	
	private static final String BUSCA_METAS_UNIDADE = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO "
			+ "WHERE MU.COD_UNIDADE = ? ORDER BY M.CODIGO";

	public Meta getMetasByCpf(long cpf) throws SQLException{
		Meta meta = new Meta();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			meta = createMeta(rSet);
		}finally {
				closeConnection(conn, stmt, rSet);
			}	
		return meta;
	}
	
	public Meta getMetasByUnidade(Long codUnidade) throws SQLException{
		Meta meta = new Meta();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS_UNIDADE);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			meta = createMeta(rSet);
		}finally {
				closeConnection(conn, stmt, rSet);
			}	
		return meta;
	}

	public Meta createMeta(ResultSet rSet) throws NumberFormatException, SQLException{
		Meta meta = new Meta();
		while(rSet.next()){

			if(rSet.getString("NOME").equals("DevCX")){
				meta.setMetaDevCx(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("DevNF")){
				meta.setMetaDevNf(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Jornada_liquida_mapas")){
				meta.setMetaJornadaLiquidaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Jornada_liquida_hora")){
				meta.setMetaJornadaLiquidaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_interno_mapas")){
				meta.setMetaTempoInternoMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_interno_hora")){
				meta.setMetaTempoInternoHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_rota_mapas")){
				meta.setMetaTempoRotaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_rota_hora")){
				meta.setMetaTempoRotaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_largada_mapas")){
				meta.setMetaTempoLargadaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tempo_largada_hora")){
				meta.setMetaTempoLargadaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("Tracking")){
				meta.setMetaTracking(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals("DevHL")){
				meta.setMetaDevHl(Double.parseDouble(rSet.getString("VALOR")));
			}
		}
		return meta;
	}

}
