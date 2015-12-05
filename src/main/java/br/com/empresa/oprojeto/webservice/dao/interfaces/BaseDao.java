package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao {
	boolean save(Object object) throws SQLException;
	boolean delete(Long codigo) throws SQLException;
	Object getByCod(Long codigo) throws SQLException;
	List<Object> getAll() throws SQLException;
}
