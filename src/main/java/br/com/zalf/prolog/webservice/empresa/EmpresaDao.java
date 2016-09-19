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

	/**
	 * @param ano ano da busca
	 * @param mes mes da busca
	 * @param codUnidade unidade que deseja-se buscar
	 * @return
	 * @throws SQLException caso ocorrer erro no banco
	 * @throws NoContentException se não tiver conteudo
	 */
	List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) throws SQLException, NoContentException;

	/**
	 * Cadastra um setor no banco de dados
	 * @param nome nome do setor
	 * @param codUnidade código da unidade referente ao setor
	 * @return objeto que encapsula um resposta OK ou NOK
	 * @throws SQLException caso ocorrer erro no banco
	 */
	AbstractResponse insertSetor(String nome, Long codUnidade) throws SQLException;

	/**
	 * Lista os setores referentes ao código da unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de setores da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException;

	/**
	 * Lista as equipes de uma unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de equipes da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException;

	/**
	 * Atualiza uma equipe
	 * @param request objeto que encapsula uma equipe
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean updateEquipe (Request<Equipe> request) throws SQLException;

	/**
	 * Cria uma equipe
	 * @param request objeto que encapsula uma equipe
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean createEquipe (Request<Equipe> request) throws SQLException;

	/**
	 * lista as funções de uma unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de funções da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException;

	/**
	 * Busca os itens do Filtro (empresa, unidade, equipe)
	 * @param cpf do solicitante, busca a partir das permissões
	 * @return list de Empresa, contendo os itens do filtro
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Empresa> getFiltros(Long cpf) throws SQLException;
	
}
