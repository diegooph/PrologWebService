package br.com.zalf.prolog.webservice.seguranca.gsd.pdv;

import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PdvDaoImpl extends DatabaseConnection implements PdvDao {

	@Override
	public List<Pdv> insertList(List<Pdv> pdvs) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO PDV (NOME) VALUES "
					+ "(?) RETURNING CODIGO;");
			for (Pdv pdv : pdvs) {
				Pdv p = selectPdvByNome(pdv.getNome());
				if (p != null) {
					// Já existe PDV com esse nome no banco, 
					// não precisa inserir, seta apenas o código dele
					pdv.setCodigo(p.getCodigo());
				} else {
					// Não existe na tabela de PDV então precisa inserir
					stmt.setString(1, pdv.getNome());
					rSet = stmt.executeQuery();
					if (rSet.next()) {
						pdv.setCodigo(rSet.getInt("CODIGO"));
					} else {
						throw new SQLException("Erro ao inserir PDV");
					}
				}
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return pdvs;
	}

	@Override
	public boolean updateList(List<Pdv> pdvs) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	private Pdv selectPdvByNome(String nomePdv) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Pdv pdv = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM PDV P WHERE "
					+ "LOWER(P.NOME) = LOWER(?)");
			stmt.setString(1, nomePdv);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				pdv = new Pdv();
				pdv.setNome(rSet.getString("NOME"));
				pdv.setCodigo(rSet.getInt("CODIGO"));
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return pdv;
	}
}
