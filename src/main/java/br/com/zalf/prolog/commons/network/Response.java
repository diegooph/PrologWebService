package br.com.zalf.prolog.commons.network;

/**
 * Objeto utilizado pelo WS para dar feedback das requisições.
 */
public class Response extends AbstractResponse{

	public Response() {
	}

	public static Response Ok(String string) {
		Response r = new Response();
		r.setStatus(OK);
		r.setMsg(string);
		return r;
	}

	public static Response Error(String string) {
		Response r = new Response();
		r.setStatus(ERROR);
		r.setMsg(string);
		return r;
	}
}
