package br.com.zalf.prolog.webservice.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class DatabaseConnectionProvider extends DatabaseConnection {

    @NotNull
    public Connection provideDatabaseConnection() {
        return getConnection();
    }

    public void closeResources(@Nullable final AutoCloseable... closeable) {
        close(closeable);
    }
}
