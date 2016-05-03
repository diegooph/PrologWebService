package br.com.zalf.prolog.webservice.relato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class RelatoDaoImpl extends DatabaseConnection implements RelatoDao {

	private static final int LIMIT = 10;

	@Override
	public boolean insert(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO RELATO "
					+ "(DATA_HORA_LOCAL, ASSUNTO, DESCRICAO, LATITUDE, LONGITUDE, "
					+ "URL_FOTO_1, URL_FOTO_2, URL_FOTO_3, CPF_COLABORADOR, DATA_HORA_DATABASE) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?)");						
			stmt.setTimestamp(1, DateUtils.toTimestamp(relato.getDataLocal()));
			stmt.setString(2, relato.getAssunto());
			stmt.setString(3, relato.getDescricao());
			stmt.setString(4, relato.getLatitude());
			stmt.setString(5, relato.getLongitude());
			stmt.setString(6, relato.getUrlFoto1());
			stmt.setString(7, relato.getUrlFoto2());
			stmt.setString(8, relato.getUrlFoto3());
			stmt.setLong(9, relato.getCpfColaborador());
			stmt.setTimestamp(10, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean update(Request<Relato> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE RELATO SET DATA_HORA_LOCAL = ?, "
					+ "ASSUNTO = ?, DESCRICAO = ?, LATITUDE = ?, LONGITUDE = ?, "
					+ "URL_FOTO_1 = ?, URL_FOTO_2 = ?, URL_FOTO_3 = ?, "
					+ "CPF_COLABORADOR = ? WHERE CODIGO = ?");
						stmt.setTimestamp(1, DateUtils.toTimestamp(request.getObject().getDataLocal()));
						stmt.setString(2, request.getObject().getAssunto());
						stmt.setString(3, request.getObject().getDescricao());
						stmt.setString(4, request.getObject().getLatitude());
						stmt.setString(5, request.getObject().getLongitude());
						stmt.setString(6, request.getObject().getUrlFoto1());
						stmt.setString(7, request.getObject().getUrlFoto2());
						stmt.setString(8, request.getObject().getUrlFoto3());
						stmt.setLong(9, request.getObject().getCpfColaborador());
						stmt.setLong(10, request.getObject().getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Request<Relato> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM RELATO WHERE CODIGO = ?");
						stmt.setLong(1, request.getObject().getCodigo());
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	// TODO: Fazer join token
	@Override
	public Relato getByCod(Request<Relato> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO "
					+ "JOIN TOKEN_AUTENTICACAO TA ON TA.CPF_COLABORADOR = ? AND"
					+ "TA.TOKEN = ? "
					+ "WHERE CODIGO = ?");
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
			stmt.setLong(3, request.getObject().getCodigo());
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Relato relato = createRelato(rSet);
				return relato;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Relato> getAll(Request<?> request) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM RELATO");
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	@Override
	public List<Relato> getByColaborador(Long cpf, String token, int limit, long offset, double latitude, double longitude, boolean isOrderByDate) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String query = "SELECT *, ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as distancia "
				+ " FROM RELATO R JOIN "
				+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF "
				+ "JOIN TOKEN_AUTENTICACAO TA ON ? = TA.CPF_COLABORADOR AND "
				+ "? = TA.TOKEN WHERE R.CPF_COLABORADOR = ? "
				+ "ORDER BY %s "
				+ "LIMIT ? OFFSET ? ";
				try {
					
			conn = getConnection();
			if(isOrderByDate){
				query = String.format(query, "DATA_HORA_DATABASE DESC");
			}else{
				query = String.format(query, "DISTANCIA ASC");
			}
			System.out.print(query);
			stmt = conn.prepareStatement(query);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			stmt.setLong(3, cpf);
			stmt.setString(4, token);
			stmt.setLong(5, cpf);
			System.out.println(isOrderByDate);
			stmt.setInt(6, limit);
			stmt.setLong(7, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	@Override
	public List<Relato> getAllExcetoColaborador(Long cpf, String token,int limit, long offset, double latitude, double longitude, boolean isOrderByDate) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		System.out.println("offset:  " + offset);
		String query = "SELECT *, ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as distancia "
				+ " FROM RELATO R JOIN "
				+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF JOIN "
				+ "TOKEN_AUTENTICACAO TA ON ? = TA.TOKEN AND "
				+ "? = TA.CPF_COLABORADOR WHERE "
				+ "R.CPF_COLABORADOR != ? "
				+ "ORDER BY %s "
				+ "LIMIT ? OFFSET ? ";
		try {
			conn = getConnection();
			if(isOrderByDate){
				query = String.format(query, "DATA_HORA_DATABASE DESC");
			}else{
				query = String.format(query, "DISTANCIA ASC");
			}
			System.out.print(query);
			stmt = conn.prepareStatement(query);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			stmt.setString(3, token);
			stmt.setLong(4, cpf);
			stmt.setLong(5, cpf);
			System.out.println(isOrderByDate);
			stmt.setInt(6, limit);
			stmt.setLong(7, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	@Override
	public List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token,long limit, long offset) throws SQLException{
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		//TODO NÃO ESTA FAZENDO A BUSCA CORRETA QUANDO MANDAMOS APENAS O COD UNIDADE E % NO CODEQUIPE - VERIFICAR
		System.out.println(dataInicial);
		System.out.println(dataFinal);
		System.out.println(equipe);
		System.out.println(codUnidade);
		System.out.println(cpf);
		System.out.println(token);
		System.out.println(limit);
		System.out.println(offset);

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT R.CODIGO, R.DATA_HORA_LOCAL, R.DATA_HORA_DATABASE, "
					+ "R.ASSUNTO, R.DESCRICAO, R.LATITUDE, R.LONGITUDE, R.URL_FOTO_1, "
					+ "R.URL_FOTO_2, R.URL_FOTO_3, C.NOME, R.CPF_COLABORADOR, NULL AS DISTANCIA "
					+ "FROM RELATO R JOIN COLABORADOR C ON R.CPF_COLABORADOR = C.CPF "
					+ "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ "JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? AND TA.CPF_COLABORADOR = ? "
					+ "WHERE E.NOME LIKE ? AND "
					+ "R.DATA_HORA_DATABASE::DATE BETWEEN ? AND ? "
					+ "ORDER BY DATA_HORA_LOCAL LIMIT ? OFFSET ?");

			stmt.setString(1, token);
			stmt.setLong(2, cpf);
			stmt.setString(3, equipe);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
			stmt.setLong(6, limit);
			stmt.setLong(7, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				System.out.println(relato);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	private Relato createRelato(ResultSet rSet) throws SQLException{
		Relato relato = new Relato();
		relato.setNomeColaborador(rSet.getString("NOME"));
		relato.setCodigo(rSet.getLong("CODIGO"));
		relato.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
		// A hora que será mostrada no android deve ser a Data_Hora_Database
		relato.setDataLocal(rSet.getTimestamp("DATA_HORA_LOCAL"));
		relato.setDataDatabase(rSet.getTimestamp("DATA_HORA_DATABASE"));
		relato.setAssunto(rSet.getString("ASSUNTO"));
		relato.setDescricao(rSet.getString("DESCRICAO"));
		relato.setLatitude(rSet.getString("LATITUDE"));
		relato.setLongitude(rSet.getString("LONGITUDE"));
		relato.setUrlFoto1(rSet.getString("URL_FOTO_1"));
		relato.setUrlFoto2(rSet.getString("URL_FOTO_2"));
		relato.setUrlFoto3(rSet.getString("URL_FOTO_3"));
		relato.setDistanciaColaborador(rSet.getDouble("DISTANCIA"));
		
		System.out.println(relato.getDataDatabase());
		System.out.println(relato.getDistanciaColaborador());
		return relato;
	}
}
