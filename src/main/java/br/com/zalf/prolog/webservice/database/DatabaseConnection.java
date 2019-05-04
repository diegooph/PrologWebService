package br.com.zalf.prolog.webservice.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe responsável por conter os métodos de criar e fechar a conexão com o banco de dados.
 *
 * @author Luiz Felipe <luiz.felipe_95@hotmail.com>
 * @version 1.0
 * @since 5 de dez de 2015 11:42:13
 */
public class DatabaseConnection {

    /**
     * Método responsável por criar conexão com o banco.
     *
     * @return Connection
     * @since 5 de dez de 2015 11:42:04
     */
    @NotNull
    public static Connection getConnection() {
        try {
            return DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao abrir conexão com o banco", e);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void close(@Nullable final AutoCloseable... closeable) {
        if (closeable != null) {
            for (int i = 0; i < closeable.length; i++) {
                try {
                    final AutoCloseable autoCloseable = closeable[i];
                    if (autoCloseable != null) {
                        autoCloseable.close();
                    }
                } catch (final Exception ignore) {}
            }
        }
    }

    /**
     * Método responsável por fechar a conexão com o banco.
     *
     * @since 5 de dez de 2015 11:42:22
     */
    @Deprecated
    public static void closeConnection(@Nullable final Connection conn,
                                       @Nullable final PreparedStatement stmt,
                                       @Nullable final ResultSet rSet) {
        if (rSet != null) {
            try {
                rSet.close();
            } catch (Exception ignore) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ignore) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignore) {}
        }
    }

    @Deprecated
    public static void closeConnection(@Nullable final Connection conn) {
        closeConnection(conn, null, null);
    }

    @Deprecated
    public void closeStatement(@Nullable final PreparedStatement stmt) {
        closeConnection(null, stmt, null);
    }

    @Deprecated
    public void closeResultSet(@Nullable final ResultSet rSet) {
        closeConnection(null, null, rSet);
    }
}