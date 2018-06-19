package br.com.zalf.prolog.webservice.errorhandling.exception;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Classe para erros específicos do ProLog
 * 
 * @author Luiz Felipe
 *
 */
public abstract class ProLogException extends Exception {
	private static final long serialVersionUID = 1L;

	/** contains the same HTTP Status code returned by the server */
	private int httpStatusCode;

	/** application specific error code */
	private int proLogErrorCode;

	/** message describing the error*/
	@NotNull
	private String message;

	/** link point to page where the error message is documented */
	@Nullable
	private String moreInfoLink;

	/** extra information that might useful for developers */
	@Nullable
	private String developerMessage;

	/** parent exception that are catch and mapped to a {@link ProLogException}*/
	@Nullable
	private Exception parentException;

	public ProLogException() {
		super();
	}

	public ProLogException(int httpStatusCode,
						   int proLogErrorCode,
						   @NotNull String message) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
	}

	public ProLogException(int httpStatusCode,
						   int proLogErrorCode,
						   @NotNull String message,
						   @NotNull String developerMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.developerMessage = developerMessage;
	}

	public ProLogException(int httpStatusCode,
						   int proLogErrorCode,
						   @NotNull String message,
						   @NotNull String developerMessage,
						   @NotNull Exception parentException) {
		super(parentException);
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.developerMessage = developerMessage;
		this.parentException = parentException;
	}

	public ProLogException(int httpStatusCode,
						   int proLogErrorCode,
						   @NotNull String message,
						   @NotNull String developerMessage,
						   @NotNull String moreInfoLink) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.proLogErrorCode = proLogErrorCode;
		this.message = message;
		this.developerMessage = developerMessage;
		this.moreInfoLink = moreInfoLink;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public int getProLogErrorCode() {
		return proLogErrorCode;
	}

	public void setProLogErrorCode(int proLogErrorCode) {
		this.proLogErrorCode = proLogErrorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMoreInfoLink() {
		return moreInfoLink;
	}

	public void setMoreInfoLink(String moreInfoLink) {
		this.moreInfoLink = moreInfoLink;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public Exception getParentException() {
		return parentException;
	}

	public void setParentException(final Exception parentException) {
		this.parentException = parentException;
	}
}