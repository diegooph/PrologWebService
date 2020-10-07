package br.com.zalf.prolog.webservice.commons.network;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;

/**
 * Objeto utilizado pelo WS para dar feedback das requisições.
 */
public class Response extends AbstractResponse {

	public Response() {
	}

	public static Response ok(String string) {
		Response r = new Response();
		r.setStatus(OK);
		r.setMsg(string);
		return r;
	}

	public static Response error(String string) {
		Response r = new Response();
		r.setStatus(ERROR);
		r.setMsg(string);
		return r;
	}
}
