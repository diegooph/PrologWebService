package br.com.zalf.prolog.webservice.errorhandling.sql;

/**
 * Created on 18/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum SqlErrorCodes {
    DUPLICATE_KEY(23505);

    private final int errorCode;

    SqlErrorCodes(final int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
