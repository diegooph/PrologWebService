package br.com.zalf.prolog.webservice.frota.pneu.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ProLogSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.errorhandling.sql.SqlErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Created on 21/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuSqlExceptionTranslator extends ProLogSqlExceptionTranslator {
    @Nullable
    @Override
    protected ProLogException customTranslate(@NotNull final SQLException sqlException,
                                              @NotNull final String fallBackErrorMessage) {
        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.UNIQUE_VIOLATION.getErrorCode())) {
            return new PneuDuplicadoException();
        }

        return super.customTranslate(sqlException, fallBackErrorMessage);
    }
}
