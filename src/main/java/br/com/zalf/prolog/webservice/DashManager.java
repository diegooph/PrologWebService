package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.dao.DashSegurancaDaoImpl;

public class DashManager {
	
		
	public DashSegurancaDaoImpl getDashSegurancaDaoImpl(){
		return new DashSegurancaDaoImpl();
	}
	
}
