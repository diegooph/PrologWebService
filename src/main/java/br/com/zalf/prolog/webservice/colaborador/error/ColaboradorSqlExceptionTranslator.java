package br.com.zalf.prolog.webservice.colaborador.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ProLogSqlExceptionTranslator;
import br.com.zalf.prolog.webservice.errorhandling.sql.SqlErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Created on 19/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ColaboradorSqlExceptionTranslator extends ProLogSqlExceptionTranslator {

    @Nullable
    @Override
    protected ProLogException customTranslate(@NotNull final SQLException sqlException,
                                              @NotNull final String fallBackErrorMessage) {
        if (sqlException.getErrorCode() == SqlErrorCodes.DUPLICATE_KEY.getErrorCode()) {
            return new ColaboradorDuplicadoException();
        }

        return super.customTranslate(sqlException, fallBackErrorMessage);
    }
}