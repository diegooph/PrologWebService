package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.ColaboradorDao;
	
public class ColaboradorDaoImpl extends DataBaseConnection implements 
		BaseDao<Colaborador>, ColaboradorDao {
	
	@Override
	public boolean insert(Colaborador colaborador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO COLABORADOR "
					+ "(CPF, MATRICULA_AMBEV, MATRICULA_TRANS, DATA_NASCIMENTO "
					+ "DATA_ADMISSAO, DATA_DEMISSAO, STATUS_ATIVO, NOME, EQUIPE "
					+ "SETOR, COD_FUNCAO, COD_UNIDADE) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?) ");
			setStatementItems(stmt, colaborador);
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

	@Override
	public boolean update(Colaborador colaborador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
					+ "CPF = ?, MATRICULA_AMBEV = ?, MATRICULA_TRANS = ?, "
					+ "DATA_NASCIMENTO = ? DATA_ADMISSAO = ?, DATA_DEMISSAO = ?, "
					+ "STATUS_ATIVO = ?, NOME = ?, EQUIPE = ?, SETOR = ?, "
					+ "COD_FUNCAO = ?, COD_UNIDADE = ? WHERE CPF = ?");
			setStatementItems(stmt, colaborador);
			stmt.setLong(13, colaborador.getCpf());
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
	public boolean delete(Long cpf) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM COLABORADOR WHERE CPF = ?");
			stmt.setLong(1, cpf);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	@Override
	public Colaborador getByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM COLABORADOR WHERE "
					+ "CPF = ?");
			stmt.setLong(1, codigo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				return c;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public List<Colaborador> getAll() throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM COLABORADOR");
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
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.EQUIPE FROM "
					+ "COLABORADOR C WHERE C.CPF = ? AND DATA_NASCIMENTO = ?)");
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
	
	private Colaborador createColaborador(ResultSet rSet) throws SQLException {
		Colaborador c = new Colaborador();
		c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));
		c.setCodFuncao(rSet.getLong("COD_FUNCAO"));
		c.setCpf(rSet.getLong("CPF"));
		c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
		c.setCodUnidade(rSet.getLong("COD_UNIDADE"));
		c.setNome(rSet.getString("NOME"));
		c.setMatriculaAmbev(rSet.getInt("MATRICULA_AMBEV"));
		c.setMatriculaTrans(rSet.getInt("MATRICULA_TRANS"));
		c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
		c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
		c.setEquipe(rSet.getString("EQUIPE"));
		c.setSetor(rSet.getString("SETOR"));
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
