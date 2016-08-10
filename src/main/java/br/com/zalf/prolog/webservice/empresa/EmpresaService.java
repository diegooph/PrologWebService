package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.*;
import br.com.zalf.prolog.models.imports.HolderMapaTracking;

import javax.ws.rs.core.NoContentException;

public class EmpresaService {

	private EmpresaDaoImpl dao = new EmpresaDaoImpl();
	
	public List<Equipe> getEquipesByCodUnidade(Long codUnidade){
		
		try{
			return dao.getEquipesByCodUnidade(codUnidade);
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

	public List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade){
		try{
			return dao.getResumoAtualizacaoDados(ano, mes, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}catch (NoContentException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
}
