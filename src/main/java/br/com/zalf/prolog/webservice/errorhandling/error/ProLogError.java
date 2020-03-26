package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProLogError {
	/** contains the same HTTP Status code returned by the server */
	private final int httpStatusCode;

	/** application specific error code */
	private final int proLogErrorCode;

	/** message describing the error*/
	@NotNull
	private final String message;

	/** link point to page where the error message is documented */
	@Nullable
	private final String moreInfoLink;

	/** extra information that might useful for developers */
	@Nullable
	private final String developerMessage;

	private ProLogError(final int httpStatusCode,
						final int proLogErrorCode,
						@NotNull final String message,
						@Nullable final String moreInfoLink,
						@Nullable final String developerMessage) {
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.moreInfoLink = moreInfoLink;
		this.developerMessage = developerMessage;
	}

	@NotNull
	public static ProLogError createFrom(@NotNull final ProLogException ex) {
		return new ProLogError(
				ex.getHttpStatusCode(),
				ex.getProLogErrorCode(),
				ex.getMessage(),
				ex.getMoreInfoLink(),
				/* Só setamos essa mensagem se estivermos em modo DEBUG. Assim evitamos de vazar alguma informação
				* sensitiva. */
				BuildConfig.DEBUG ? ex.getDeveloperMessage() : null);
	}

    @NotNull
    public static ProLogError generateFromString(@NotNull final String jsonError) {
        return GsonUtils.getGson().fromJson(jsonError, ProLogError.class);
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

	public int getProLogErrorCode() {
		return proLogErrorCode;
	}

	public String getMessage() {
		return message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public String getMoreInfoLink() {
		return moreInfoLink;
	}

	@Override
	public String toString() {
		return "ErrorMessage [httpStatusCode=" + httpStatusCode + ", proLogErrorCode=" + proLogErrorCode
				+ ", message=" + message + ", moreInfoLink=" + moreInfoLink + ", developerMessage=" + developerMessage
				+ "]";
	}
}