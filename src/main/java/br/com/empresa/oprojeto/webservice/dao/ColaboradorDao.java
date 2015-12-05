package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColaboradorDao extends ConnectionFactory {
	
	public void getCarroById(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("select * from colaborador where cpf=?");
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				createColaborador(rs);
				rs.close();
			}
		} finally {
			
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
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

}
