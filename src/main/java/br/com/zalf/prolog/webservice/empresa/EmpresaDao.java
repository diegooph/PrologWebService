package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;

import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.List;

public interface EmpresaDao {

	List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) throws SQLException, NoContentException;

	AbstractResponse insertSetor(String nome, Long codUnidade)throws SQLException;

	List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException;

	List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException;
	
	boolean updateEquipe (Request<Equipe> request) throws SQLException;
	
	boolean createEquipe (Request<Equipe> request) throws SQLException;
	
	List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException;

	/**
	 * Busca os itens do Filtro (empresa, unidade, equipe)
	 * @param cpf do solicitante, busca a partir das permissões
	 * @return list de Empresa, contendo os itens do filtro
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Empresa> getFiltros(Long cpf) throws SQLException;
	
}
