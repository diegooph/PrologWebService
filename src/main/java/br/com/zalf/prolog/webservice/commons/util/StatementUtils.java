package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.IntStream;

/**
 * Created on 13/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class StatementUtils {

    private StatementUtils() {
        throw new IllegalStateException(StatementUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void bindValueOrNull(@NotNull final PreparedStatement stmt,
                                       final int parameterIndex,
                                       @Nullable final Object object,
                                       @NotNull final SqlType sqlType) throws SQLException {
        if (object != null) {
            stmt.setObject(parameterIndex, object);
        } else {
            stmt.setNull(parameterIndex, sqlType.asIntTypeJava());
        }
    }

    public static void executeBatchAndValidate(@NotNull final PreparedStatement stmt,
                                               final int allMatchWith,
                                               @NotNull final String messageIfFailed) throws SQLException {
        // Executa o batch de operações.
        // Se o batch estiver vazio, um array vazio será retornado e não teremos problema com esse caso.
        final boolean allOk = IntStream
                .of(stmt.executeBatch())
                .allMatch(rowsAffectedCount -> rowsAffectedCount == allMatchWith);
        if (!allOk) {
            throw new IllegalStateException(messageIfFailed);
        }
    }
}