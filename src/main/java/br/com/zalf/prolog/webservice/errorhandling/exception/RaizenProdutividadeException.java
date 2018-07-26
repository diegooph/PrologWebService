package br.com.zalf.prolog.webservice.errorhandling.exception;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeException extends ProLogException {

    public RaizenProdutividadeException(@NotNull final String message,
                                        @Nullable final String developerMessage,
                                        @NotNull final Throwable parentException) {
        super(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode(),
                ProLogErrorCodes.RAIZEN_PRODUTIVIDADE.errorCode(),
                message,
                developerMessage,
                parentException);
    }

}

