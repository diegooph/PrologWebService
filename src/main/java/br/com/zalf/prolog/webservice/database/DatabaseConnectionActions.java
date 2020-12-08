package br.com.zalf.prolog.webservice.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

/**
 * Created on 2020-11-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface DatabaseConnectionActions {

    @NotNull
    Connection getConnection();

    /**
     * Método responsável por fechar a conexão com o banco com uma lista de AutoCloseable.
     *
     * @param closeables conjunto de objetos para fechar a conexão.
     * @see AutoCloseable
     * @since 17 de nov de 2020
     */
    void close(@Nullable final AutoCloseable... closeables);
}
