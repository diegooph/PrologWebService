package br.com.zalf.prolog.webservice.cors;

import org.glassfish.jersey.server.ResourceConfig;

public class Teste extends ResourceConfig{
	
	public Teste() {
		// TODO Auto-generated constructor stub
		register(CORSResponseFilter.class);
		packages("br.com.zalf.prolog.webservice");
	}
	
	

}
