package br.com.empresa.oprojeto.webservice.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloResource {

	@GET
	public String helloTextPlain() {
		return "Olá mundo Texto!";
	}
}
