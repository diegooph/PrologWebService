package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
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
    @NotNull
    private static final String TAG = SpringDatabaseConnectionActions.class.getSimpleName();

    @NotNull
    private final DataSource dataSource;

    @Autowired
    SpringDatabaseConnectionActions(@NotNull final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @NotNull
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (final SQLException exception) {
            Log.e(TAG, "Erro ao tentar abrir conexão com o banco de dados.", exception);
            throw new RuntimeException("Erro ao tentar abrir conexão com o banco de dados.", exception);
        }
    }

    @Override
    public void close(@Nullable final AutoCloseable... closeables) {
        Arrays.stream(closeables)
                .filter(Objects::nonNull)
                .forEach(this::internalClose);
    }

    private void internalClose(@NotNull final AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
        } catch (final Exception ignore) {
        }
    }
}
