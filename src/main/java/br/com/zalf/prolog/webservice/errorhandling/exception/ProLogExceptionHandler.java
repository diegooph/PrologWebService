package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.sql.ErrorMessageFactory;
import br.com.zalf.prolog.webservice.errorhandling.sql.SqlExceptionTranslator;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Created on 04/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogExceptionHandler {

    private ProLogExceptionHandler() {
        throw new IllegalStateException(ProLogExceptionHandler.class.getSimpleName() + " cannot be instantiated!");
    }

    public static ProLogException map(@NotNull Throwable throwable,
                                      @NotNull final String fallBackErrorMessage) {
        if (throwable instanceof SQLException) {
            throwable = SqlExceptionTranslator.doTranslate((SQLException) throwable);
        }

        final String errorMessage = ErrorMessageFactory.create(throwable, fallBackErrorMessage);
        if (throwable instanceof ProLogException) {
            return (ProLogException) throwable;
        } else {
            return new GenericException(errorMessage, null);
        }
    }
}