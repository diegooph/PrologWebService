package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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


    public static void close(@Nullable final AutoCloseable... closeable) {
        PrologApplication.getActions().close(closeable);
    }

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 17 de nov de 2020
     * @deprecated
     */
    @Deprecated
    public static void closeConnection(@Nullable final Connection conn) {
        PrologApplication.getActions().close(conn);
    }

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 17 de nov de 2020
     */
    @Deprecated
    public static void closeConnection(@Nullable final Connection conn,
                                       @Nullable final PreparedStatement stmt,
                                       @Nullable final ResultSet rSet) {
        PrologApplication.getActions().close(conn, rSet, stmt);
    }

    /**
     * Método responsável por criar conexão com o banco.
     *
     * @return Connection
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 17 de nov de 2020
     */
    @NotNull
    public static Connection getConnection() {
        return PrologApplication.getActions().getConnection();
    }

    /**
     * Método responsável por fechar o PreparedStatement.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 17 de nov de 2020
     * @deprecated
     */
    @Deprecated
    public static void closeStatement(@Nullable final PreparedStatement stmt) {
        PrologApplication.getActions().close(stmt);
    }

    /**
     * Método responsável por fechar o ResultSet.
     *
     * @apiNote O Método atualmente é só um intermédio para a classe DatabaseConnectionActions
     * @see DatabaseConnectionActions
     * @since 17 de nov de 2020
     * @deprecated
     */
    @Deprecated
    public static void closeResultSet(@Nullable final ResultSet rSet) {
        PrologApplication.getActions().close(rSet);
    }
}