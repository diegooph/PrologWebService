package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.AutenticacaoDao;
import br.com.zalf.prolog.webservice.dao.interfaces.ColaboradorDao;

public class ColaboradorDaoImpl extends DatabaseConnection implements ColaboradorDao {

	@Override
	public boolean insert(Request<Colaborador> request) throws SQLException {
		Autenticacao autenticacao = new Autenticacao("", request.getCpf(), 
				request.getToken());
		AutenticacaoDao autenticacaoDao = new AutenticacaoDaoImpl();
		if (autenticacaoDao.verifyIfExists(autenticacao)) {
			Connection conn = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();

				stmt = conn.prepareStatement("INSERT INTO COLABORADOR "
						+ "(CPF, MATRICULA_AMBEV, MATRICULA_TRANS, DATA_NASCIMENTO "
						+ "DATA_ADMISSAO, DATA_DEMISSAO, STATUS_ATIVO, NOME, EQUIPE "
						+ "SETOR, COD_FUNCAO, COD_UNIDADE) VALUES "
						+ "(?,?,?,?,?,?,?,?,?,?,?,?) ");
				setStatementItems(stmt, request.getObject());
				int count = stmt.executeUpdate();
				if(count == 0){
					throw new SQLException("Erro ao inserir o colaborador");
				}	
			}
			finally {
				closeConnection(conn, stmt, null);
			}		
			return true;
		}
		return false;
	}

	@Override
	public boolean update(Request<Colaborador> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
					+ "CPF = ?, MATRICULA_AMBEV = ?, MATRICULA_TRANS = ?, "
					+ "DATA_NASCIMENTO = ? DATA_ADMISSAO = ?, DATA_DEMISSAO = ?, "
					+ "STATUS_ATIVO = ?, NOME = ?, EQUIPE = ?, SETOR = ?, "
					+ "COD_FUNCAO = ?, COD_UNIDADE = ? "
					+ "FROM TOKEN_AUTENTICACAO TA WHERE CPF = ? AND "
					+ "TA.CPF_COLABORADOR = ? AND TA.TOKEN = ?");
			setStatementItems(stmt, request.getObject());
			stmt.setLong(13, request.getCpf());
			stmt.setLong(14, request.getCpf());
			stmt.setString(15, request.getToken());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar o colaborador");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	@Override
	public boolean delete(Request<Colaborador> request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
					+ "STATUS_ATIVO = FALSE"
					+ "FROM TOKEN_AUTENTICACAO TA WHERE CPF = ? AND "
					+ "TA.CPF_COLABORADOR = ? AND TA.TOKEN = ?");
			stmt.setLong(1, request.getObject().getCpf());
			stmt.setLong(2, request.getCpf());
			stmt.setString(3, request.getToken());
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	// TODO: mudar para receber um Request<?> request
	@Override
	public Colaborador getByCod(Long cpf, String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS EQUIPE, C.SETOR, C.COD_FUNCAO, C.COD_UNIDADE, "
					+ "F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO "
					+ "FROM COLABORADOR C JOIN TOKEN_AUTENTICACAO TA "
					+ "ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN FUNCAO F ON C.COD_FUNCAO = "
					+ "F.CODIGO "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ "WHERE CPF = ? AND C.STATUS_ATIVO = TRUE");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				System.out.println(c.getCodFuncao());
				System.out.println(c.getNomeFuncao());
				System.out.println(c);
				return c;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Colaborador> getAll(Request<?> request) throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, C.COD_PERMISSAO AS PERMISSAO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS EQUIPE, C.SETOR, C.COD_FUNCAO, C.COD_UNIDADE, "
					+ "F.NOME AS NOME_FUNCAO FROM COLABORADOR C JOIN TOKEN_AUTENTICACAO TA "
					+ "ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN FUNCAO F ON F.CODIGO = C.COD_UNIDADE "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ "WHERE C.COD_UNIDADE = ? ORDER BY C.NOME; ");
			stmt.setLong(1, request.getCpf());
			stmt.setString(2, request.getToken());
			stmt.setLong(3, request.getCodUnidade());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				list.add(c);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	@Override
	public List<Colaborador> getAtivosByUnidade(Long codUnidade, String token, Long cpf) throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, C.COD_PERMISSAO AS PERMISSAO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS EQUIPE, C.SETOR, C.COD_FUNCAO, C.COD_UNIDADE, "
					+ "F.NOME AS NOME_FUNCAO FROM COLABORADOR C JOIN TOKEN_AUTENTICACAO TA "
					+ "ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN FUNCAO F ON F.CODIGO = C.COD_UNIDADE "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ "WHERE C.COD_UNIDADE = ? AND C.STATUS_ATIVO = TRUE ORDER BY C.NOME; ");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				list.add(c);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	@Override
	public boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM "
					+ "COLABORADOR C WHERE C.CPF = ? AND DATA_NASCIMENTO = ? "
					+ "AND C.STATUS_ATIVO = TRUE)");
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtils.toSqlDate(dataNascimento));
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
	public Funcao getFuncaoByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FUNCAO F JOIN "
					+ "COLABORADOR C ON F.CODIGO = C.COD_FUNCAO "
					+ "WHERE C.CPF = ?");
			stmt.setLong(1, codigo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Funcao f = createFuncao(rSet);
				return f;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	private Funcao createFuncao(ResultSet rSet) throws SQLException {
		Funcao f = new Funcao();
		f.setCodigo(rSet.getLong("CODIGO"));
		System.out.println(rSet.getString("NOME"));
		f.setNome(rSet.getString("NOME"));
		return f;
	}

	private Colaborador createColaborador(ResultSet rSet) throws SQLException {
		Colaborador c = new Colaborador();
		c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
		c.setCodFuncao(rSet.getLong("COD_FUNCAO"));
		System.out.println(rSet.getLong("COD_FUNCAO"));
		c.setCpf(rSet.getLong("CPF"));
		c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
		c.setCodUnidade(rSet.getLong("COD_UNIDADE"));
		c.setNome(rSet.getString("NOME_COLABORADOR"));
		c.setMatriculaAmbev(rSet.getInt("MATRICULA_AMBEV"));
		c.setMatriculaTrans(rSet.getInt("MATRICULA_TRANS"));
		c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
		c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
		c.setEquipe(rSet.getString("EQUIPE"));
		c.setSetor(rSet.getString("SETOR"));
		c.setNomeFuncao(rSet.getString("NOME_FUNCAO"));
		System.out.println(rSet.getString("NOME_FUNCAO"));
		c.setCodPermissao(rSet.getLong("PERMISSAO"));
		return c;
	}

	private void setStatementItems(PreparedStatement stmt, Colaborador c) throws SQLException {
		stmt.setLong(1, c.getCpf());
		stmt.setInt(2, c.getMatriculaAmbev());
		stmt.setInt(3, c.getMatriculaTrans());
		stmt.setDate(4, DateUtils.toSqlDate(c.getDataNascimento()));
		stmt.setDate(5, DateUtils.toSqlDate(c.getDataAdmissao()));
		stmt.setDate(6, DateUtils.toSqlDate(c.getDataDemissao()));
		stmt.setBoolean(7, c.isAtivo());
		stmt.setString(8, c.getNome());
		stmt.setString(9, c.getEquipe());
		stmt.setString(10, c.getSetor());
		stmt.setLong(11, c.getCodFuncao());
		stmt.setLong(12, c.getCodUnidade());
	}
}