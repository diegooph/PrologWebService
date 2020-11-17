package br.com.zalf.prolog.webservice.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created on 2020-11-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface DatabaseConnectionActions {

    Connection getConnection();
    void close(AutoCloseable... closeables);
    void close(AutoCloseable closeable);
    void close(Connection connection);
    void close(Connection connection, ResultSet rs);
    void close(Connection connection, ResultSet rs, PreparedStatement stmt);
}
