package br.com.zalf.prolog.webservice.errorhandling.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Classe para erros específicos do ProLog
 *
 * @author Luiz Felipe
 */
public abstract class ProLogException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	/**
	 * contains the same HTTP Status code returned by the server
	 */
	private int httpStatusCode;
	/**
	 * application specific error code
	 */
	private int proLogErrorCode;
	/**
	 * Message describing the error
	 */
	@NotNull
	private String message;
	/**
	 * Message with extra information, without expose any delicate data
	 */
	@Nullable
	private String detailedMessage;
	/**
	 * Link point to page where the error message is documented
	 */
	@Nullable
	private String moreInfoLink;

	/**
	 * Extra information that might be useful for developers
	 */
	@Nullable
	private String developerMessage;

	/**
	 * Parent exception that are catch and mapped to a {@link ProLogException}
	 */
	@Nullable
	private Throwable parentException;

	public ProLogException() {
		super();
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message,
						   @Nullable final String detailedMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.detailedMessage = detailedMessage;
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message,
						   @Nullable final String detailedMessage,
						   @Nullable final String developerMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.detailedMessage = detailedMessage;
		this.developerMessage = developerMessage;
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message,
						   @Nullable final String detailedMessage,
						   @Nullable final String developerMessage,
						   @Nullable final Throwable parentException) {
		super(parentException);
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.detailedMessage = detailedMessage;
		this.developerMessage = developerMessage;
		this.parentException = parentException;
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message,
						   @Nullable final String developerMessage,
						   @Nullable final Throwable parentException) {
		super(parentException);
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.developerMessage = developerMessage;
		this.parentException = parentException;
	}

	public ProLogException(final int httpStatusCode,
						   final int proLogErrorCode,
						   @NotNull final String message,
						   @Nullable final String detailedMessage,
						   @Nullable final String developerMessage,
						   @NotNull final String moreInfoLink) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.detailedMessage = detailedMessage;
		this.developerMessage = developerMessage;
		this.moreInfoLink = moreInfoLink;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(final int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public int getProLogErrorCode() {
		return proLogErrorCode;
	}

	public void setProLogErrorCode(final int proLogErrorCode) {
		this.proLogErrorCode = proLogErrorCode;
	}

	@NotNull
	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(@NotNull final String message) {
		this.message = message;
	}

	@Nullable
	public String getMoreInfoLink() {
		return moreInfoLink;
	}

	public void setMoreInfoLink(@Nullable final String moreInfoLink) {
		this.moreInfoLink = moreInfoLink;
	}

	@Nullable
	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(@Nullable final String developerMessage) {
		this.developerMessage = developerMessage;
	}

	@Nullable
	public Throwable getParentException() {
		return parentException;
	}

	public void setParentException(@Nullable final Throwable parentException) {
		this.parentException = parentException;
	}

	@Nullable
	public String getDetailedMessage() {
		return detailedMessage;
	}
}