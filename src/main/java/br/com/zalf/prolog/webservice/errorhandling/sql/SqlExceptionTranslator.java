package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.errorhandling.sql.SqlErrorCodes.*;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SqlExceptionTranslator {

    // TODO: private constructor

    public static Exception doTranslate(@NotNull final SQLException sqlException) {
        if (sqlException.getErrorCode() == DUPLICATE_KEY.getErrorCode()) {
            return new DuplicateKeyException();
        }

        // TODO: Tratar aqui
        return  new GenericException("", "");
    }
}
