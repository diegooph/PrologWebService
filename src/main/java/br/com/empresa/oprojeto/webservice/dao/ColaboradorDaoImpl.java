package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;

public class ColaboradorDaoImpl extends ConnectionFactory  implements BaseDao{
	
	public void getCarroById(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("select * from colaborador where cpf=?");
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				createColaborador(rs);
				rs.close();
			}
		} finally {
			closeConnection(conn, stmt, rs);
		}
	}
	
	public void createColaborador(ResultSet rs) throws SQLException {
		System.out.println(rs.getLong("cpf"));
		System.out.println(rs.getString("nome"));
		System.out.println(rs.getString("equipe"));
		System.out.println(rs.getString("setor"));
		System.out.println(rs.getString("cod_funcao"));
		System.out.println(rs.getString("cod_unidade"));
		System.out.println(rs.getString("matricula_ambev"));
		System.out.println(rs.getString("status_ativo"));
	}

	@Override
	public boolean insert(Object... objects) throws SQLException {
		return false;
	}

	@Override
	public boolean update(Object object) throws SQLException {
		return false;
	}

	@Override
	public boolean saveOrUpdate(Object object) throws SQLException {
		return false;
	}

	@Override
	public boolean delete() throws SQLException {
		return false;
	}

	@Override
	public Object getByCod(long cod) throws SQLException {
		return null;
	}

	@Override
	public List<Object> getAll() throws SQLException {
		return null;
	}
}
