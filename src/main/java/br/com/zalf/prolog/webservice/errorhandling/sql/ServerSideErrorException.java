package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.error.StatusEnum;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class ServerSideErrorException extends ProLogException {
    public ServerSideErrorException(@NotNull final String message) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.SERVER_SIDE_ERROR.errorCode(),
                message);
    }

    public ServerSideErrorException(@NotNull final String message, @NotNull final String detailedMessage) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.SERVER_SIDE_ERROR.errorCode(),
                message,
                detailedMessage);
    }

    public ServerSideErrorException(@NotNull final String message,
                                    @NotNull final String detailedMessage,
                                    @NotNull final String developerMessage,
                                    final boolean loggableOnErrorReportSystem) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.SERVER_SIDE_ERROR.errorCode(),
                message,
                detailedMessage,
                developerMessage,
                loggableOnErrorReportSystem);
    }
}