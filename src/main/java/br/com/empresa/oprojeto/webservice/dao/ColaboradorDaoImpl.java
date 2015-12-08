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
	
// TODO: Adicionar classes para encriptar o CPF na hora de enviar do Android para cá.
// Talvez seja melhor mudar a base dao para ter insert e update pois no caso
// do colaborador não podemos saber se é um insert ou um update já que não temos
// um código para verificar se é nulo. Nosso código será o cpf e ele nunca 
// será nulo
// Ou então, adicionar os métodos insert e update no dao colaborador.
public class ColaboradorDaoImpl extends DataBaseConnection implements 
		BaseDao<Colaborador>, ColaboradorDao {
	
	@Override
	public boolean save(Colaborador object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
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
}
