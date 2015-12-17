package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.empresa.oprojeto.models.indicador.Meta;
import br.com.empresa.oprojeto.models.util.TimeUtils;

public class MetasDao extends DataBaseConnection{
	
	private static final String BUSCA_METAS = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO"
			+ " JOIN COLABORADOR C ON C.COD_UNIDADE = MU.COD_UNIDADE "
			+ "WHERE C.CPF = ? ORDER BY M.CODIGO";
	
	public Meta getMetas(long cpf) throws SQLException{
		Meta meta = new Meta();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();

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
				}}}

		finally {
			closeConnection(conn, stmt, rSet);
		}	
		return meta;}

	
}
