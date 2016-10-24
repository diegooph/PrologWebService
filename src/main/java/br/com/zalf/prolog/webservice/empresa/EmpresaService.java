package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.permissao.pilares.Pilar;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmpresaService responsavel por comunicar-se com a interface DAO
 */
public class EmpresaService {

	private EmpresaDao dao = new EmpresaDaoImpl();
	
	public List<Equipe> getEquipesByCodUnidade(Long codUnidade) {
		try{
			return dao.getEquipesByCodUnidade(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean updateEquipe (Request<Equipe> request) {
		try{
			return dao.updateEquipe(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createEquipe (Request<Equipe> request) {
		try{
			return dao.createEquipe(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Funcao> getFuncoesByCodUnidade (long codUnidade) {
		try{
			return dao.getFuncoesByCodUnidade(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Pilar> getPermissoes(Long codCargo, Long codUnidade){
		try{
			return dao.getPermissoes(codCargo, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Setor> getSetorByCodUnidade(Long codUnidade) {
		try{
			return dao.getSetorByCodUnidade(codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public AbstractResponse insertSetor(String nome, Long codUnidade) {
		try{
			return dao.insertSetor(nome,codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return Response.Error("Erro ao inserir o setor");
		}
	}

	public List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) {
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

	public List<Empresa> getFiltros(Long cpf) {
		try{
			return dao.getFiltros(cpf);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
}
