package br.com.zalf.prolog.webservice.errorhandling.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

/**
 * Created on 04/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ProLogExceptionHandler {

    public ProLogExceptionHandler() {
    }

    public ProLogException map(@NotNull final Throwable throwable,
                               @NotNull final String fallBackErrorMessage) {
        if (throwable instanceof SQLException || throwable instanceof DataAccessException) {
            return new SqlExceptionV2Wrapper(throwable, fallBackErrorMessage);
        }
        if (throwable instanceof ProLogException) {
            return (ProLogException) throwable;
        }

        return new GenericException(fallBackErrorMessage, "Erro genérico não mapeado", throwable);
    }
}