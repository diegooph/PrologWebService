package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.IntStream;

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

    /**
     * Com este método como sobrecarga evitamos de passar um array nulo para o
     * {@link #bind(PreparedStatement, Object...)}.
     */
    public static void bind(@NotNull final PreparedStatement stmt,
                            @Nullable final Object value) {
        bind(stmt, new Object[]{value});
    }

    /**
     * Não pode receber um array nulo, porém, declara como @Nullable para evitar warnings nas chamadas do método onde
     * vários parâmetros são passados e alguns podem ser nulos.
     * Assim, a nulidade é validada internamente.
     */
    public static void bind(@NotNull final PreparedStatement stmt,
                            @Nullable final Object... values) {
        Preconditions.checkNotNull(values);

        IntStream
                .range(1, values.length + 1)
                .forEach(i -> internalBind(stmt, values[i - 1], i));
    }

    private static void internalBind(@NotNull final PreparedStatement stmt,
                                     @Nullable final Object value,
                                     final int position) {
        try {
            stmt.setObject(position, value);
        } catch (final Throwable throwable) {
            throw Exceptions.rethrow(throwable);
        }
    }
}
