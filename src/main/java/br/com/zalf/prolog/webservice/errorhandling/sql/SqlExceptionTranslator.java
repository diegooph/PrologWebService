package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Created on 19/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface SqlExceptionTranslator {
    @NotNull
    ProLogException doTranslate(@NotNull final SQLException sqlException);

    @NotNull
    ProLogException doTranslate(@NotNull final SQLException sqlException, @NotNull final String fallBackErrorMessage);
}