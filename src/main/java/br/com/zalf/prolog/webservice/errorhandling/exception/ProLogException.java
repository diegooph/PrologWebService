package br.com.zalf.prolog.webservice.errorhandling.exception;

/**
 * Classe para erros específicos do ProLog
 * 
 * @author Luiz Felipe
 *
 */
public abstract class ProLogException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProLogException() {
		super();
	}

	public abstract int getHttpStatusCode();

	public abstract int getProLogErrorCode();

	@Override
	public abstract String getMessage();

	public abstract String getDeveloperMessage();

	public abstract String getMoreInfoLink();
}