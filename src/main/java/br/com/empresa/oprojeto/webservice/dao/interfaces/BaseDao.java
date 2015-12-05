package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.util.List;

public interface BaseDao {
	boolean insert(Object object);
	boolean update(Object object);
	boolean saveOrUpdate(Object object);
	boolean delete();
	Object getByCod(long cod);
	List<Object> getAll();
}
