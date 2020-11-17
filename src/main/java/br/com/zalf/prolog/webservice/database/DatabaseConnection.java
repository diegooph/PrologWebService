package br.com.zalf.prolog.webservice.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe responsável por conter os métodos de criar e fechar a conexão com o banco de dados.
 *
 * @author Luiz Felipe <luiz.felipe_95@hotmail.com>
 * @author Guilherme Steinert (https://github.com/steinert999)
 * @version 2.0
 * @apiNote Esta classe está sendo instanciada pelos DAO's atualmente, posteriormente para refatoração
 * deve-se alterar todos os DAO's corretamente.
 * @since 17 de nov de 2020
 */
@Component
public class DatabaseConnection {

    @Autowired
    private DatabaseConnectionActions actions;


    public void close(@Nullable final AutoCloseable... closeable) {
        this.actions.close(closeable);
    }

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 5 de dez de 2015 11:42:22
     * @deprecated
     */
    @Deprecated
    public void closeConnection(@Nullable final Connection conn) {
        this.actions.close(conn);
    }

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 5 de dez de 2015 11:42:22
     */
    @Deprecated
    public void closeConnection(@Nullable final Connection conn,
                                @Nullable final PreparedStatement stmt,
                                @Nullable final ResultSet rSet) {
        this.actions.close(conn, rSet, stmt);
    }

    /**
     * Método responsável por criar conexão com o banco.
     *
     * @return Connection
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 5 de dez de 2015 11:42:04
     */
    @NotNull
    public Connection getConnection() {
        return this.actions.getConnection();
    }

    /**
     * Método responsável por fechar o PreparedStatement.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 5 de dez de 2015 11:42:22
     * @deprecated
     */
    @Deprecated
    public void closeStatement(@Nullable final PreparedStatement stmt) {
        this.actions.close(stmt);
    }

    /**
     * Método responsável por fechar o ResultSet.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 5 de dez de 2015 11:42:22
     * @deprecated
     */
    @Deprecated
    public void closeResultSet(@Nullable final ResultSet rSet) {
        this.actions.close(rSet);
    }
}