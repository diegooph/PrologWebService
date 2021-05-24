package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

public final class ProLogError {
    /**
     * Contains the same HTTP Status code returned by the server
     */
    private final int httpStatusCode;

    /**
     * Application specific error code
     */
    private final int proLogErrorCode;

    /**
     * Message describing the error
     */
    @NotNull
    private final String message;

    /**
     * Message with extra information, without expose any delicate data
     */
    @Nullable
    private final String detailedMessage;

    /**
     * Link point to page where the error message is documented
     */
    @Nullable
    private final String moreInfoLink;

    /**
     * Extra information that might be useful for developers
     */
    @Nullable
    private final String developerMessage;

    private ProLogError(final int httpStatusCode,
                        final int proLogErrorCode,
                        @NotNull final String message,
                        @Nullable final String detailedMessage,
                        @Nullable final String moreInfoLink,
                        @Nullable final String developerMessage) {
        this.httpStatusCode = httpStatusCode;
        this.proLogErrorCode = proLogErrorCode;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.moreInfoLink = moreInfoLink;
        this.developerMessage = developerMessage;
    }

    @NotNull
    public static ProLogError createFrom(@NotNull final ProLogException ex) {
        return new ProLogError(
                ex.getHttpStatusCode(),
                ex.getProLogErrorCode(),
                ex.getMessage(),
                ex.getDetailedMessage(),
                ex.getMoreInfoLink(),
                // Só setamos essa mensagem se estivermos em modo DEBUG. Assim evitamos de vazar alguma informação
                // sensitiva.
                BuildConfig.DEBUG ? ex.getDeveloperMessage() : null);
    }

    @NotNull
    public static ProLogError createFrom(@NotNull final NotAuthorizedException ex) {
        return new ProLogError(
                Response.Status.UNAUTHORIZED.getStatusCode(),
                ProLogErrorCodes.NOT_AUTHORIZED.errorCode(),
                ex.getMessage(),
                "Sem autorização para acessar a API.",
                null,
                // Só setamos essa mensagem se estivermos em modo DEBUG. Assim evitamos de vazar alguma informação
                // sensitiva.
                null);
    }

    @NotNull
    public static ProLogError createFrom(@NotNull final ForbiddenException ex) {
        return new ProLogError(
                Response.Status.FORBIDDEN.getStatusCode(),
                ProLogErrorCodes.FORBIDDEN.errorCode(),
                ex.getMessage(),
                "Usuário sem permissão.",
                null,
                // Só setamos essa mensagem se estivermos em modo DEBUG. Assim evitamos de vazar alguma informação
                // sensitiva.
                null);
    }

    @NotNull
    public static ProLogError createFrom(@NotNull final NotFoundException ex) {
        final String msg = "A url buscada não existe na API.";
        return new ProLogError(
                ex.getResponse().getStatus(),
                ProLogErrorCodes.NOT_FOUND.errorCode(),
                msg,
                msg,
                null,
                BuildConfig.DEBUG ? msg : null);
    }

    @NotNull
    public static ProLogError generateFromString(@NotNull final String jsonError) {
        return GsonUtils.getGson().fromJson(jsonError, ProLogError.class);
    }

    @Override
    public String toString() {
        return "ErrorMessage [httpStatusCode=" + httpStatusCode + ", proLogErrorCode=" + proLogErrorCode
                + ", message=" + message + ", moreInfoLink=" + moreInfoLink + ", developerMessage=" + developerMessage
                + "]";
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getProLogErrorCode() {
        return proLogErrorCode;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getDeveloperMessage() {
        return developerMessage;
    }

    public String getMoreInfoLink() {
        return moreInfoLink;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}