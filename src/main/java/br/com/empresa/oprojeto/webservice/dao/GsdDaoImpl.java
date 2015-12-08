package br.com.empresa.oprojeto.webservice.dao;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.gsd.Gsd;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;

public class GsdDaoImpl implements BaseDao<Gsd> {

	@Override
	public boolean insert(Gsd object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean update(Gsd object) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public Gsd getByCod(Long codigo) throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<Gsd> getAll() throws SQLException {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

}
