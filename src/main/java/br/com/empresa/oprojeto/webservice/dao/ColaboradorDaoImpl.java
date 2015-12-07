package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.ColaboradorDao;
import br.com.empresa.oprojeto.webservice.util.DateUtil;

public class ColaboradorDaoImpl extends DataBaseConnection implements 
		BaseDao<Colaborador>, ColaboradorDao {
	
	@Override
	public boolean save(Colaborador object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
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
			stmt.setDate(2, DateUtil.toSqlDate(dataNascimento));
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				return rSet.getBoolean("EXISTS");
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}
}
