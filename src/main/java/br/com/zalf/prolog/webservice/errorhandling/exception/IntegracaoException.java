package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import com.sun.istack.internal.NotNull;

/**
 * Created on 16/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class IntegracaoException extends ProLogException {

    public IntegracaoException(int httpStatusCode,
                               @NotNull String message,
                               @NotNull String developerMessage) {
        super(httpStatusCode, ProLogErrorCodes.INTEGRACAO.errorCode(), message, developerMessage);
    }
}
