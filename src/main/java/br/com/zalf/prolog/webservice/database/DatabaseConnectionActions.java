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

    /**
     * Método responsável por fechar a conexão com o banco com uma lista de AutoCloseable
     *
     * @see AutoCloseable
     * @since 17 de nov de 2020
     */
    void close(AutoCloseable... closeables);

    /**
     * Método responsável por fechar a conexão com o banco com AutoCloseable
     *
     * @see AutoCloseable
     * @since 17 de nov de 2020
     */
    void close(AutoCloseable closeable);

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @see Connection
     * @since 17 de nov de 2020
     */
    void close(Connection connection);

    /**
     * Método responsável por fechar a conexão com o banco e o ResultSet.
     *
     * @see Connection
     * @see ResultSet
     * @since 17 de nov de 2020
     */
    void close(Connection connection, ResultSet rs);

    /**
     * Método responsável por fechar a conexão com o banco, ResultSet e PreparedStatement
     *
     * @see Connection
     * @see ResultSet
     * @see PreparedStatement
     * @since 17 de nov de 2020
     */
    void close(Connection connection, ResultSet rs, PreparedStatement stmt);
}
