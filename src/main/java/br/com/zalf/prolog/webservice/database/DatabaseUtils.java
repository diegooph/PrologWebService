package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DatabaseUtils {

    public static void safeRollback(@Nullable final Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (final SQLException e) {
                throw Exceptions.rethrow(e);
            }
        }
    }

    public static void bind(@NotNull final PreparedStatement stmt,
                            @NotNull final List<Object> values) throws SQLException {
        for (int i = 1; i <= values.size(); i++) {
            stmt.setObject(i, values.get(i - 1));
        }
    }
}
