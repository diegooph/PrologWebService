package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.SessionIdentifierGenerator;
import br.com.zalf.prolog.webservice.dao.interfaces.AutenticacaoDao;

public class AutenticacaoDaoImpl extends DatabaseConnection implements AutenticacaoDao {

	@Override
	public Autenticacao insertOrUpdate(Long cpf) throws SQLException {
		SessionIdentifierGenerator tokenGenerador = new SessionIdentifierGenerator();
		String token = tokenGenerador.nextSessionId();
		if (update(cpf, token)) {
			// Já existia e atualizou, não precisa inserir
			Autenticacao autenticacao = new Autenticacao();
			autenticacao.setToken(token);
			autenticacao.setCpf(cpf);
			autenticacao.setStatus(Autenticacao.OK);
			return autenticacao;
		} else {
			// Deve inserir, retorna se foi sucesso ou não
			return insert(cpf, token);
		}
	}

	@Override
	public boolean verifyIfExists(Autenticacao autenticacao) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT TA.CPF_COLABORADOR FROM "
					+ "TOKEN_AUTENTICACAO TA WHERE C.CPF = ? AND TOKEN = ?)");
			stmt.setLong(1, autenticacao.getCpf());
			stmt.setString(2, autenticacao.getToken());
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				return rSet.getBoolean("EXISTS");
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}
	
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
}
