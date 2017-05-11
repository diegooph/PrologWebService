package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.webservice.colaborador.Empresa;
import br.com.zalf.prolog.webservice.colaborador.Equipe;
import br.com.zalf.prolog.webservice.colaborador.Funcao;
import br.com.zalf.prolog.webservice.colaborador.Setor;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Request;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmpresaService responsavel por comunicar-se com a interface DAO
 */
public class EmpresaService {

	private EmpresaDao dao = new EmpresaDaoImpl();

	public boolean insertEquipe(Long codUnidade, Equipe equipe) {
		try {
			return dao.insertEquipe(codUnidade, equipe);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateEquipe(Long codEquipe, Equipe equipe) {
		try {
			return dao.updateEquipe(codEquipe, equipe);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public AbstractResponse insertSetor(Long codUnidade, Setor setor) {
		try {
			return dao.insertSetor(codUnidade, setor);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.Error("Erro ao inserir o setor");
		}
	}

	public List<Equipe> getEquipesByCodUnidade(Long codUnidade) {
		try{
			return dao.getEquipesByCodUnidade(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public List<Funcao> getFuncoesByCodUnidade(long codUnidade) {
		try{
			return dao.getFuncoesByCodUnidade(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public Visao getVisaoCargo(Long codCargo, Long codUnidade){
		try{
			return dao.getVisaoCargo(codCargo, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public Visao getVisaoUnidade(Long codUnidade){
		try{
			return dao.getVisaoUnidade(codUnidade);
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

	public boolean alterarVisaoCargo(Visao visao, Long codUnidade, Long codCargo){
		try{
			return dao.alterarVisaoCargo(visao, codUnidade, codCargo);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	@Deprecated
	public AbstractResponse insertSetor(String nome, Long codUnidade) {
		try {
			return dao.insertSetor(nome,codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.Error("Erro ao inserir o setor");
		}
	}

	@Deprecated
	public boolean createEquipe(Request<Equipe> request) {
		try {
			return dao.createEquipe(request);
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Deprecated
	public boolean updateEquipe(Request<Equipe> request) {
		try{
			return dao.updateEquipe(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}