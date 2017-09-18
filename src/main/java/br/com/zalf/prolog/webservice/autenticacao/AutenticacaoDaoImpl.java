package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.SessionIdentifierGenerator;
import br.com.zalf.prolog.webservice.errorhandling.exception.ResourceAlreadyDeletedException;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável pela comunicação com o banco de dados da aplicação.
 */
public class AutenticacaoDaoImpl extends DatabaseConnection implements AutenticacaoDao {

	@Override
	public Autenticacao insertOrUpdate(Long cpf) throws SQLException {
		SessionIdentifierGenerator tokenGenerador = new SessionIdentifierGenerator();
		String token = tokenGenerador.nextSessionId();
		return insert(cpf, token);
	}

	@Override
	public boolean verifyIfTokenExists(String token, boolean apenasUsuariosAtivos) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE token_autenticacao SET " +
					"DATA_HORA = ? WHERE TOKEN = ? " +
					"AND (SELECT C.STATUS_ATIVO " +
						 "FROM COLABORADOR C " +
						 "JOIN TOKEN_AUTENTICACAO TA ON C.CPF = TA.CPF_COLABORADOR AND TA.TOKEN = ?)::TEXT = ?");
			stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			stmt.setString(2, token);
			stmt.setString(3, token);
			stmt.setString(4, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
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
	public boolean verifyIfUserExists(long cpf, long dataNascimento, boolean apenasUsuariosAtivos) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = ? " +
					"AND C.DATA_NASCIMENTO = ? AND C.STATUS_ATIVO::TEXT LIKE ?);");
			stmt.setLong(1, cpf);
			stmt.setDate(2, new Date(dataNascimento));
			stmt.setString(3, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				return rSet.getBoolean("EXISTS");
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}

	@Override
	public boolean userHasPermission(@NotNull String token, @NotNull int[] permissions,
									 boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			String query = "SELECT cfp.cod_funcao_prolog AS cod_permissao\n" +
					"FROM token_autenticacao TA\n" +
					"  JOIN colaborador C ON C.cpf = TA.cpf_colaborador\n" +
					"  JOIN cargo_funcao_prolog_v11 CFP ON CFP.cod_unidade = C.cod_unidade AND CFP.cod_funcao_colaborador = C.cod_funcao\n" +
					"WHERE TA.token = ? AND C.STATUS_ATIVO::TEXT LIKE ?;";
			conn = getConnection();
			stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setString(1, token);
			stmt.setString(2, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
			rSet = stmt.executeQuery();
			List<Integer> permissoes = Arrays.stream(permissions).boxed().collect(Collectors.toList());
			return verifyPermissions(needsToHaveAllPermissions, permissoes, rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public boolean userHasPermission(long cpf, long dataNascimento, @NotNull int[] permissions,
									 boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			String query = "SELECT cfp.cod_funcao_prolog AS cod_permissao\n" +
					"FROM colaborador C JOIN cargo_funcao_prolog_v11 CFP ON CFP.cod_unidade = C.cod_unidade AND CFP.cod_funcao_colaborador = C.cod_funcao\n" +
					"WHERE c.cpf = ? and c.data_nascimento = ? AND C.STATUS_ATIVO::TEXT LIKE ?;";
			conn = getConnection();
			stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, cpf);
			stmt.setDate(2, new Date(dataNascimento));
			stmt.setString(3, apenasUsuariosAtivos ? Boolean.toString(true) : "%");
			rSet = stmt.executeQuery();
			List<Integer> permissoes = Arrays.stream(permissions).boxed().collect(Collectors.toList());
			return verifyPermissions(needsToHaveAllPermissions, permissoes, rSet);
		}finally {
			closeConnection(conn, stmt, rSet);
		}
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

			if (stmt.executeUpdate() == 0) {
				throw new ResourceAlreadyDeletedException();
			}

			return true;
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

	private boolean verifyPermissions(boolean needsToHaveAll, List<Integer> permissoes, ResultSet rSet) throws SQLException{
		if (!rSet.next()) {
			return false;
		}
		rSet.beforeFirst();
		while (rSet.next()) {
			if (needsToHaveAll) {
				if (!permissoes.contains(rSet.getInt("cod_permissao"))) {
					return false;
				}
			} else {
				if (permissoes.contains(rSet.getInt("cod_permissao"))) {
					return true;
				}
			}
		}
		return needsToHaveAll;
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