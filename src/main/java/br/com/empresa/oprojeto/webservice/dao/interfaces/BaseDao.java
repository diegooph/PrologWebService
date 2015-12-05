package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.util.List;

public interface BaseDao {
	Object getByCod(long cod);
	List<Object> getAll();
	void insert(Object object);
	void update(Object object);
	void updateByCod(long cod);
	void save(Object object);
}
