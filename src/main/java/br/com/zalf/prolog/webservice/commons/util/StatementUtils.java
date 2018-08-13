package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}