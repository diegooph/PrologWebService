package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.commons.login.Autenticacao;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.SessionIdentifierGenerator;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AutenticacaoDaoImpl extends DatabaseConnection implements AutenticacaoDao {

	@Override
	public Autenticacao insertOrUpdate(Long cpf) throws SQLException {
		SessionIdentifierGenerator tokenGenerador = new SessionIdentifierGenerator();
		String token = tokenGenerador.nextSessionId();
		return insert(cpf, token);
	}

	@Override
	public boolean verifyIfTokenExists(String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE token_autenticacao SET " +
					"DATA_HORA = ? WHERE TOKEN = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(2, token);
			int count =  stmt.executeUpdate();
			if (count > 0) {
				return true;
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
		return false;
	}

	@Override
	public boolean delete(String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM TOKEN_AUTENTICACAO TA "
					+ "WHERE TA.TOKEN = ?");
			stmt.setString(1, token);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	private Autenticacao insert(Long cpf, String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Autenticacao autenticacao = new Autenticacao();
		autenticacao.setCpf(cpf);
		autenticacao.setToken(token);
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO TOKEN_AUTENTICACAO"
					+ "(CPF_COLABORADOR, TOKEN) VALUES (?, ?);");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			int count = stmt.executeUpdate();
			if(count == 0){
				autenticacao.setStatus(Autenticacao.ERROR);
				return autenticacao;
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
		autenticacao.setStatus(Autenticacao.OK);
		return autenticacao;
	}

	@Deprecated
	private boolean update(Long cpf, String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE TOKEN_AUTENTICACAO SET "
					+ "TOKEN = ? WHERE CPF_COLABORADOR = ?;");
			stmt.setString(1, token);
			stmt.setLong(2, cpf);
			int count = stmt.executeUpdate();
			if(count == 0){
				return false;
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}
}
