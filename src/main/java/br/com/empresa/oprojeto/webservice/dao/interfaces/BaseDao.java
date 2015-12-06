package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao<T> {
    boolean save(T object) throws SQLException;
    boolean delete(Long codigo) throws SQLException;
    T getByCod(Long codigo) throws SQLException;
    List<T> getAll() throws SQLException;
}
