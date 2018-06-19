package br.com.zalf.prolog.webservice.errorhandling.sql;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SqlErrorCodes {
    UNIQUE_VIOLATION("23505");

    private final String errorCode;

    SqlErrorCodes(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return errorCode;
    }
}