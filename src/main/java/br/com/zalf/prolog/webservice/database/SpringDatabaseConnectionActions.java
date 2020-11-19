package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created on 2020-11-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class SpringDatabaseConnectionActions implements DatabaseConnectionActions {

    private static final String TAG = SpringDatabaseConnectionActions.class.getSimpleName();

    private final DataSource dataSource;

    @Autowired
    SpringDatabaseConnectionActions(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (final SQLException exception) {
            Log.e(TAG, "Erro ao tentar abrir conexão com o banco de dados.", exception);
        }
        return null;
    }

    @Override
    public void close(final AutoCloseable... closeables) {
        Arrays.stream(closeables)
                .filter(Objects::isNull)
                .forEach(this::close);
    }

    @Override
    public void close(final AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (final Exception e) {
            Log.e(TAG, "Não foi possivel finalizar AutoCloseable", e);
        }
    }

    @Override
    public void close(final Connection connection) {
        if(Objects.nonNull(connection)) {
            try {
                connection.close();
            } catch (final SQLException exception) {
                Log.e(TAG, "Não foi possivel finalizar conexão", exception);
            }
        }

    }

    @Override
    public void close(final Connection connection, final ResultSet rs) {
        close(connection);
        if(Objects.nonNull(rs)) {
            try {
                rs.close();
            } catch (final SQLException exception) {
                Log.e(TAG, "Não foi possivel finalizar ResultSet", exception);
            }
        }
    }

    @Override
    public void close(final Connection connection, final ResultSet rs, final PreparedStatement stmt) {
        close(connection, rs);
        if(Objects.nonNull(stmt)) {
            try {
                stmt.close();
            } catch (final SQLException exception) {
                Log.e(TAG, "Não foi possivel finalizar PreparedStatement", exception);
            }
        }
    }
}
