package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.BuildConfig;

public class ErrorMessage {
	/** contains the same HTTP Status code returned by the server */
	private Integer httpStatusCode;
	
	/** application specific error code */
	private Integer applicationErrorCode;
	
	/** message describing the error*/
	private String message;
		
	/** link point to page where the error message is documented */
	private String moreInfoLink;
	
	/** extra information that might useful for developers */
	private String developerMessage;
	
	public ErrorMessage() {
		
	}

	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(Integer httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public int getApplicationErrorCode() {
		return applicationErrorCode;
	}

	public void setApplicationErrorCode(int code) {
		this.applicationErrorCode = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		if (BuildConfig.DEBUG)
			this.developerMessage = developerMessage;
	}

	public String getMoreInfoLink() {
		return moreInfoLink;
	}

	public void setMoreInfoLink(String link) {
		this.moreInfoLink = link;
	}

	@Override
	public String toString() {
		return "ErrorMessage [httpStatusCode=" + httpStatusCode + ", applicationErrorCode=" + applicationErrorCode
				+ ", message=" + message + ", moreInfoLink=" + moreInfoLink + ", developerMessage=" + developerMessage
				+ "]";
	}
}