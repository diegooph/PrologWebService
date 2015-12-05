package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao {
	boolean save(Object object) throws SQLException;
	boolean delete() throws SQLException;
	Object getByCod(long cod) throws SQLException;
	List<Object> getAll() throws SQLException;
}
