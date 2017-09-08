package br.com.zalf.prolog.webservice.errorhandling.exception;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Classe para erros específicos do ProLog
 * 
 * @author Luiz Felipe
 *
 */
public abstract class ProLogException extends Exception {
	private static final long serialVersionUID = 1L;

	private final int httpStatusCode;
	private final int applicationErrorCode;
	@NotNull
	private final String message;
	@Nullable
	private final String developerMessage;
	@Nullable
	private final String moreInfoLink;

	public ProLogException() {
		super();
		this.httpStatusCode = getHttpStatusCode();
		this.applicationErrorCode = getApplicationErrorCode();
		this.message = getMessage();
		this.developerMessage = getDeveloperMessage();
		this.moreInfoLink = getMoreInfoLink();
	}

	public abstract int getHttpStatusCode();

	public abstract int getApplicationErrorCode();

	@Override
	public abstract String getMessage();

	public abstract String getDeveloperMessage();

	public abstract String getMoreInfoLink();
}