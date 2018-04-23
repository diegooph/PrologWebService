package br.com.zalf.prolog.webservice.errorhandling.exception;

import com.sun.istack.internal.NotNull;

/**
 * Created on 04/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TipoAfericaoNotSupported extends ProLogException {

    public TipoAfericaoNotSupported(int httpStatusCode,
                                    @NotNull String message,
                                    @NotNull String developerMessage) {
        super(httpStatusCode, ProLogErrorCodes.TIPO_AFERICAO_NAO_SUPORTADO.errorCode(), message, developerMessage);
    }
}
