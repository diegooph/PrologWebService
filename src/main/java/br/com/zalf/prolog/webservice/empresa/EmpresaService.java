package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.*;

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
	
	public List<Funcao> getFuncoesByCodUnidade (long codUnidade){
		try{
			return dao.getFuncoesByCodUnidade(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Setor> getSetorByCodUnidade(Long codUnidade){
		try{
			return dao.getSetorByCodUnidade(codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public AbstractResponse insertSetor(String nome, Long codUnidade){
		try{
			return dao.insertSetor(nome,codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return Response.Error("Erro ao inserir o setor");
		}
	}
	
}
