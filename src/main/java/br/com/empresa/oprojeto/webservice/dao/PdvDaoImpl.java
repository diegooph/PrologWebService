package br.com.empresa.oprojeto.webservice.dao;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.gsd.Pdv;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;
import br.com.empresa.oprojeto.webservice.dao.interfaces.PdvDao;

public class PdvDaoImpl extends DataBaseConnection implements BaseDao<Pdv>, PdvDao {

	@Override
	public boolean insert(Pdv object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean update(Pdv object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public Pdv getByCod(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Pdv> getAll() throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean insertList(List<Pdv> pdvs) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean updateList(List<Pdv> pdvs) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

}
