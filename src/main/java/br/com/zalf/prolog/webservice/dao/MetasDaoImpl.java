package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.indicador.Meta;
import br.com.zalf.prolog.models.util.TimeUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.MetasDao;

public class MetasDaoImpl extends DatabaseConnection implements MetasDao{

	private static final String BUSCA_METAS = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO"
			+ " JOIN COLABORADOR C ON C.COD_UNIDADE = MU.COD_UNIDADE "
			+ "WHERE C.CPF = ? ORDER BY M.CODIGO";

	private static final String BUSCA_METAS_UNIDADE = "SELECT M.CODIGO, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO "
			+ "WHERE MU.COD_UNIDADE = ? ORDER BY M.CODIGO";

	private static final String BUSCA_METAS_BY_CPF = "SELECT M.CODIGO AS COD_META, M.NOME, MU.VALOR "
			+ "FROM META M JOIN META_UNIDADE MU ON MU.COD_META = M.CODIGO"
			+ " JOIN COLABORADOR C ON C.COD_UNIDADE = MU.COD_UNIDADE "
			+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND "
			+ "TA.TOKEN = ? "
			+ "WHERE C.CPF = ? ORDER BY M.CODIGO";

	private static final String DEV_CX = "DevCX";
	private static final String DEV_NF = "DevNF";
	private static final String DEV_HL = "DevHL";
	private static final String TRACKING = "Tracking";
	private static final String LARGADA_MAPAS = "Tempo_largada_mapas";
	private static final String LARGADA_TEMPO = "Tempo_largada_hora";
	private static final String ROTA_MAPAS = "Tempo_rota_mapas";
	private static final String ROTA_TEMPO = "Tempo_rota_hora";
	private static final String INTERNO_MAPAS = "Tempo_interno_mapas";
	private static final String INTERNO_TEMPO = "Tempo_interno_hora";
	private static final String JORNADA_MAPAS = "Jornada_liquida_mapas";
	private static final String JORNADA_TEMPO = "Jornada_liquida_hora";


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

	public List<Metas> getByCpf(Long cpf, String token) throws SQLException{

		System.out.println(cpf);
		System.out.println(token);

		List<Metas> listMetas = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_METAS_BY_CPF);
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listMetas.add(createMetas(rSet));
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}	
		return listMetas;

	}

	public Metas createMetas(ResultSet rSet) throws SQLException{

		Metas meta = new Metas(); 

		if(rSet.getString("NOME").equals(DEV_CX)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(DEV_CX);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(DEV_NF)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
					meta.setNome(DEV_NF);
					meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(JORNADA_MAPAS)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(JORNADA_MAPAS);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(JORNADA_TEMPO)){
			meta = new Metas<Time>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(JORNADA_TEMPO);
			meta.setValor(TimeUtils.toSqlTime(rSet.getString("VALOR")));

		}else if(rSet.getString("NOME").equals(INTERNO_MAPAS)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(INTERNO_MAPAS);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(INTERNO_TEMPO)){
			meta = new Metas<Time>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(INTERNO_TEMPO);
			meta.setValor(TimeUtils.toSqlTime(rSet.getString("VALOR")));

		}else if(rSet.getString("NOME").equals(ROTA_MAPAS)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(ROTA_MAPAS);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(ROTA_TEMPO)){
			meta = new Metas<Time>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(ROTA_TEMPO);
			meta.setValor(TimeUtils.toSqlTime(rSet.getString("VALOR")));

		}else if(rSet.getString("NOME").equals(LARGADA_MAPAS)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(LARGADA_MAPAS);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(LARGADA_TEMPO)){
			meta = new Metas<Time>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(LARGADA_TEMPO);
			meta.setValor(TimeUtils.toSqlTime(rSet.getString("VALOR")));

		}else if(rSet.getString("NOME").equals(TRACKING)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(TRACKING);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));

		}else if(rSet.getString("NOME").equals(DEV_HL)){
			meta = new Metas<Double>();
			meta.setCodigo(rSet.getInt("COD_META"));
			meta.setNome(DEV_HL);
			meta.setValor((Double.parseDouble(rSet.getString("VALOR"))));
		}
		System.out.println(meta.getCodigo());
		System.out.println(meta.getNome());
		System.out.println(meta.getValor());
		return meta;
	}

	public Meta createMeta(ResultSet rSet) throws NumberFormatException, SQLException{
		Meta meta = new Meta();
		while(rSet.next()){

			if(rSet.getString("NOME").equals(DEV_CX)){
				meta.setMetaDevCx(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(DEV_NF)){
				meta.setMetaDevNf(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(JORNADA_MAPAS)){
				meta.setMetaJornadaLiquidaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(JORNADA_TEMPO)){
				meta.setMetaJornadaLiquidaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(INTERNO_MAPAS)){
				meta.setMetaTempoInternoMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(INTERNO_TEMPO)){
				meta.setMetaTempoInternoHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(ROTA_MAPAS)){
				meta.setMetaTempoRotaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(ROTA_TEMPO)){
				meta.setMetaTempoRotaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(LARGADA_MAPAS)){
				meta.setMetaTempoLargadaMapas(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(LARGADA_TEMPO)){
				meta.setMetaTempoLargadaHoras(TimeUtils.toSqlTime(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(TRACKING)){
				meta.setMetaTracking(Double.parseDouble(rSet.getString("VALOR")));
			}else if(rSet.getString("NOME").equals(DEV_HL)){
				meta.setMetaDevHl(Double.parseDouble(rSet.getString("VALOR")));
			}
		}
		return meta;
	}

	@Override
	public boolean updateByCod(Request request) throws SQLException {
		Metas metas = (Metas) request.getObject();

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE META_UNIDADE SET VALOR = (?) "
					+ "FROM TOKEN_AUTENTICACAO TA WHERE COD_UNIDADE = ? AND COD_META = ? "
					+ "AND TA.CPF_COLABORADOR=? AND TA.TOKEN=?");

			if(metas.getValor() instanceof Double){
				System.out.println("Tipo double");
				stmt.setDouble(1, (Double) metas.getValor());
			}else{
				stmt.setTime(1,(Time) metas.getValor());
				System.out.println("Tipo time");
			}
			stmt.setLong(2, request.getCodUnidade());
			System.out.println("Cod unidade: " + request.getCodUnidade());
			stmt.setInt(3, metas.getCodigo());
			System.out.println("Cod meta: " + metas.getCodigo());
			stmt.setLong(4, request.getCpf());
			System.out.println("cpf" + request.getCpf());
			stmt.setString(5, request.getToken());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar a meta");
			}	
		}finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}
}
