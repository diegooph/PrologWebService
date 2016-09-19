package br.com.zalf.prolog.webservice.errorhandling.exception;

public enum ApplicationExceptions {

	TEST1(1, 1, "message 1", "link teste 1", "developer message teste 1"),
	TEST2(2, 2, "message 2", "link teste 2", "developer message teste 2");
	
	public Integer httpStatusCode;
	public int applicationErrorCode;
	public String message;
	public String moreInfoLink;	
	public String developerMessage;
	
	ApplicationExceptions(
			Integer httpStatusCode, 
			int applicationErrorCode,
			String message,
			String moreInfoLink,
			String developerMessage) {
		this.httpStatusCode = httpStatusCode;
		this.applicationErrorCode = applicationErrorCode;
		this.message = message;
		this.moreInfoLink = moreInfoLink;
		this.developerMessage = developerMessage;
	}
}