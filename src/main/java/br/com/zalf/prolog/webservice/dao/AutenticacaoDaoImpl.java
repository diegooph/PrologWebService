package br.com.zalf.prolog.webservice.dao;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.TokenAutenticacao;
import br.com.zalf.prolog.webservice.dao.interfaces.BaseDao;

public class AutenticacaoDaoImpl implements BaseDao<TokenAutenticacao>{

	@Override
	public boolean insert(TokenAutenticacao object) throws SQLException {
		return false;
	}

	@Override
	public boolean update(TokenAutenticacao object) throws SQLException {
		return false;
	}

	@Override
	public boolean delete(Long codigo) throws SQLException {
		return false;
	}

	@Override
	public TokenAutenticacao getByCod(Long codigo) throws SQLException {
		return null;
	}

	@Override
	public List<TokenAutenticacao> getAll() throws SQLException {
		return null;
	}

}
