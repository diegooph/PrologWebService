package br.com.zalf.prolog.webservice.errorhandling.sql;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SqlErrorCodes {
    UNIQUE_VIOLATION("23505"),
    CHECK_VIOLATION("23514"),
    FOREIGN_KEY_VIOLATION("23503"),
    NOT_NULL_VIOLATION("23502"),
    BD_GENERIC_ERROR_CODE("SA1A1");

    @NotNull
    private final String errorCode;

    SqlErrorCodes(@NotNull final String errorCode) {
        this.errorCode = errorCode;
    }

    @NotNull
    public String getErrorCode() {
        return errorCode;
    }

    @NotNull
    @Override
    public String toString() {
        return errorCode;
    }
}