package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.sql.SqlExceptionTranslator;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

/**
 * Created on 04/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ProLogExceptionHandler {
    @NotNull
    private final SqlExceptionTranslator sqlTranslator;

    public ProLogExceptionHandler(@NotNull final SqlExceptionTranslator sqlTranslator) {
        this.sqlTranslator = sqlTranslator;
    }

    public ProLogException map(@NotNull final Throwable throwable,
                               @NotNull final String fallBackErrorMessage) {

        if (throwable instanceof DataAccessException) {
            final PSQLException psqlException = (PSQLException) throwable.getCause().getCause();
            return sqlTranslator.doTranslate(psqlException, fallBackErrorMessage);
        }

        if (throwable instanceof SQLException) {
            return sqlTranslator.doTranslate((SQLException) throwable, fallBackErrorMessage);
        }

        if (throwable instanceof ProLogException) {
            return (ProLogException) throwable;
        } else {
            return new GenericException(fallBackErrorMessage, "Erro genérico não mapeado", throwable);
        }
    }
}