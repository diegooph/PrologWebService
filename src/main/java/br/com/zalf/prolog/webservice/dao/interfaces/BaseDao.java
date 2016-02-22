package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao<I, O> {
    boolean insert(O object) throws SQLException;
    boolean update(I object) throws SQLException;
    boolean delete(I codigo) throws SQLException;
    O getByCod(Long codigo, String token) throws SQLException;
    List<O> getAll(I object) throws SQLException;
}

// TODO: como deveria ser. I = Request; O = Tipo do retornor (e.g. Colaborador)

//public interface BaseDao<I,O> {
//    boolean insert(I object) throws SQLException;
//    boolean update(I object) throws SQLException;
//    boolean delete(I codigo) throws SQLException;
//    O getByCod(Long codigo, String token) throws SQLException;
//    List<O> getAll() throws SQLException;
//}