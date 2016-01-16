package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.gsd.Pdv;
import br.com.zalf.prolog.webservice.dao.interfaces.BaseDao;
import br.com.zalf.prolog.webservice.dao.interfaces.PdvDao;

public class PdvDaoImpl extends DataBaseConnection implements PdvDao, BaseDao<Pdv> {

	@Override
	public boolean insert(Pdv object) throws SQLException {
		return false;
	}

	@Override
	public boolean update(Pdv object) throws SQLException {
		return false;
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		return false;
	}

	@Override
	public Pdv getByCod(Long codigo) throws SQLException {
		return null;
	}

	@Override
	public List<Pdv> getAll() throws SQLException {
		return null;
	}
	
	@Override
	public boolean insertList(List<Pdv> pdvs, Long codigoGsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		 try {
			 conn = getConnection();
			 stmt = conn.prepareStatement("INSERT INTO PDV_GSD (COD_GSD, "
			 		+ "COD_PDV) VALUES (?, ?)");
			 for (Pdv pdv : pdvs) {
				 stmt.setLong(1, codigoGsd);
				 stmt.setLong(2, pdv.getCodigo());
				 stmt.executeUpdate();
			 }
		} finally {
			closeConnection(conn, stmt, null);
		}
		 return true;
	}

	@Override
	public boolean updateList(List<Pdv> pdvs, Long codigoGsd) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		 try {
			 conn = getConnection();
			 stmt = conn.prepareStatement("UPDATE PDV_GSD SET COD_PDV = ? WHERE "
			 		+ "COD_GSD = ?");
			 for (Pdv pdv : pdvs) {
				 stmt.setLong(1, pdv.getCodigo());
				 stmt.setLong(2, codigoGsd);
				 stmt.executeUpdate();
			 }
		} finally {
			closeConnection(conn, stmt, null);
		}
		 return false;
	}
}
