package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao<T> {
    boolean insert(T object) throws SQLException;
    boolean update(T object) throws SQLException;
    boolean delete(Long codigo) throws SQLException;
    T getByCod(Long codigo, String token) throws SQLException;
    List<T> getAll() throws SQLException;
}