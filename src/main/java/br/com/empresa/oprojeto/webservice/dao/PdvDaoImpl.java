package br.com.empresa.oprojeto.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.gsd.Pdv;
import br.com.empresa.oprojeto.webservice.dao.interfaces.PdvDao;

public class PdvDaoImpl extends DataBaseConnection implements PdvDao {

	@Override
	public boolean insertList(List<Pdv> pdvs) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		 try {
			 conn = getConnection();
			 stmt = conn.prepareStatement("INSERT INTO PDV_GSD (COD_GSD, "
			 		+ "COD_PDV) VALUES (?, ?)");
			 for (Pdv pdv : pdvs) {
				 stmt.setLong(1, pdv.getCodigo());
			 }
			
		} finally {
			closeConnection(conn, stmt, null);
		}
		 return false;
	}

	@Override
	public boolean updateList(List<Pdv> pdvs) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

}
