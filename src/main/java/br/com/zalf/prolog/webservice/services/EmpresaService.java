package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.dao.EmpresaDaoImpl;

public class EmpresaService {

	private EmpresaDaoImpl dao = new EmpresaDaoImpl();
	
	public List<Equipe> getEquipesByCodUnidade(Request<?> request){
		
		try{
			return dao.getEquipesByCodUnidade(request);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
}
