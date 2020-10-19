package br.com.zalf.prolog.webservice.errorhandling.sql;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;
import br.com.zalf.prolog.webservice.errorhandling.error.StatusEnum;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class ForeignKeyException extends DataAccessException {

    public ForeignKeyException(@NotNull final String message) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
                message);
    }

    public ForeignKeyException(@NotNull final String message, @NotNull final String detailedMessage) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
                message,
                detailedMessage);
    }

    public ForeignKeyException(@NotNull final String message,
                               @NotNull final String detailedMessage,
                               @NotNull final String developerMessage) {
        super(StatusEnum.UNPROCESSABLE_ENTITY.getAsInteger(),
                ProLogErrorCodes.CHAVE_NAO_EXISTENTE.errorCode(),
                message,
                detailedMessage,
                developerMessage);
    }
}
