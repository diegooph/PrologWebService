package br.com.zalf.prolog.webservice.errorhandling.sql;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {

    }

    public static String create(@NotNull final Throwable throwable,
                                @NotNull final String fallBackMessage) {
        if (throwable instanceof DuplicateKeyException) {
            return "Este recurso já existe no banco de dados";
        }

        return fallBackMessage;
    }
}
