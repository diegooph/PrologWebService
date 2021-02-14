package br.com.zalf.prolog.webservice.database.transaction;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseUtils;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 2020-11-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(setterPrefix = "with")
public class DatabaseTransaction {
    @NotNull
    private final Connection connection;
    private final boolean closeConnectionOnFinish;

    public <T> T runInTransaction(@NotNull final DatabaseTransactionRunner<T> runner) {
        try {
            connection.setAutoCommit(false);
            final T response = runner.run(connection);
            connection.commit();
            return response;
        } catch (final Throwable e) {
            DatabaseUtils.safeRollback(connection);
            throw new IllegalStateException("Error to run database transaction.", e);
        } finally {
            if (closeConnectionOnFinish) {
                DatabaseConnection.close(connection);
            }
        }
    }
}
