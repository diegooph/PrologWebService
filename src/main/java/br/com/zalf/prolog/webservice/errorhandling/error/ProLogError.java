package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.BuildConfig;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public class ProLogError {
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
	
	public ProLogError() {
		
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

	public void setProLogErrorCode(int code) {
		this.proLogErrorCode = code;
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
		return "ErrorMessage [httpStatusCode=" + httpStatusCode + ", proLogErrorCode=" + proLogErrorCode
				+ ", message=" + message + ", moreInfoLink=" + moreInfoLink + ", developerMessage=" + developerMessage
				+ "]";
	}
}