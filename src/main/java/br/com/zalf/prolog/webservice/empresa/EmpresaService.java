package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Request;

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
	
	public boolean updateEquipe (Request<Equipe> request){
		
		try{
			return dao.updateEquipe(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createEquipe (Request<Equipe> request){
		try{
			return dao.createEquipe(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
}
