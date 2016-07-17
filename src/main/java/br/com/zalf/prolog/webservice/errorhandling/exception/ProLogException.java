package br.com.zalf.prolog.webservice.errorhandling.exception;

/**
 * Classe para erros específicos do ProLog
 * 
 * @author Luiz Felipe
 *
 */
public abstract class ProLogException extends Exception {

	private static final long serialVersionUID = 1L;
		
	/** detailed error description for developers*/
	private final String developerMessage;
	private final ApplicationExceptions type;
	
	public ProLogException() {
		this(null, null);
	}
	
	public ProLogException(String message, String developerMessage) {
		super(message);
		this.developerMessage = developerMessage;
		this.type = whatIsYourType();
	}
	
	public String getDeveloperMessage() {
		return developerMessage;
	}
	
	public ApplicationExceptions getType() {
		return type;
	}
	
	protected abstract ApplicationExceptions whatIsYourType();
}