package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.errorhandling.sql.SqlErrorCodes.UNIQUE_VIOLATION;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ProLogSqlExceptionTranslator implements SqlExceptionTranslator {

    @NotNull
    @Override
    public final ProLogException doTranslate(@NotNull final SQLException sqlException,
                                             @NotNull final String fallBackErrorMessage) {
        // Primeiro tentamos a tradução personalizada.
        final ProLogException proLogException = customTranslate(sqlException, fallBackErrorMessage);
        if (proLogException != null) {
            return proLogException;
        }

        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.UNIQUE_VIOLATION.getErrorCode())) {
            return new DuplicateKeyException("Este recurso já existe no banco de dados");
        }

        return new DataAccessException(fallBackErrorMessage);
    }

    @Nullable
    protected ProLogException customTranslate(@NotNull final SQLException sqlException,
                                              @NotNull final String fallBackErrorMessage) {
        return null;
    }
}