package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.permissao.Visao;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.NoContentException;
import java.sql.SQLException;
import java.util.List;

public interface EmpresaDao {

	/**
	 * Insere uma equipe
	 *
	 * @param codUnidade código da unidade onde a equipe será inserida
	 * @param equipe equipe que deve ser inserida
	 * @return true se o insert deu certo; caso contrário false
	 * @throws SQLException caso ocorrer algum erro no banco
	 */
	boolean insertEquipe(@NotNull Long codUnidade, @NotNull Equipe equipe) throws SQLException;

	/**
	 * Atualiza uma equipe
	 *
	 * @param codEquipe código da equipe que desejamos atualizar
	 * @param equipe equipe para ser atualizada
	 * @return true se a atualização deu certo; caso contrário false
	 * @throws SQLException caso ocorrer algum erro no banco
	 */
	boolean updateEquipe(@NotNull Long codEquipe, @NotNull Equipe equipe) throws SQLException;

	/**
	 * Cria uma equipe
	 * @param request objeto que encapsula uma equipe
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	@Deprecated
	boolean createEquipe(Request<Equipe> request) throws SQLException;

	/**
	 * Atualiza uma equipe
	 * @param request objeto que encapsula uma equipe
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	@Deprecated
	boolean updateEquipe(Request<Equipe> request) throws SQLException;

	/**
	 * Cadastra um setor no banco de dados
	 * @param setor o setor para ser inserido
	 * @param codUnidade código da unidade na qual esse setor será inserido
	 * @return objeto que encapsula um resposta OK ou NOK
	 * @throws SQLException caso ocorrer erro no banco
	 */
	AbstractResponse insertSetor(@NotNull Long codUnidade, @NotNull Setor setor) throws SQLException;

	/**
	 * Cadastra um setor no banco de dados
	 * @param nome nome do setor
	 * @param codUnidade código da unidade referente ao setor
	 * @return objeto que encapsula um resposta OK ou NOK
	 * @throws SQLException caso ocorrer erro no banco
	 */
	@Deprecated
	AbstractResponse insertSetor(String nome, Long codUnidade) throws SQLException;

	/**
	 * Lista as equipes de uma unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de equipes da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException;

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
	 * Lista os setores referentes ao código da unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de setores da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException;

	/**
	 * lista as funções de uma unidade
	 * @param codUnidade código de uma unidade
	 * @return lista de funções da unidade
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<Funcao> getFuncoesByCodUnidade(Long codUnidade) throws SQLException;

	/**
	 * Busca os itens do Filtro (empresa, unidade, equipe)
	 * @param cpf do solicitante, busca a partir das permissões
	 * @return list de Empresa, contendo os itens do filtro
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Empresa> getFiltros(Long cpf) throws SQLException;

	/**
	 * Busca as funções do prolog de um determinado cargo
	 * @param codCargo codigo do cargo
	 * @param codUnidade codigo da unidade
	 * @return {@link Visao} de uma {@link Funcao}
	 * @throws SQLException
	 */
	Visao getVisaoCargo(Long codCargo, Long codUnidade) throws SQLException;

	/**
	 * Busca as funções do prolog a partir do codigo da unidade
	 * @param codUnidade codigo da unidade
	 * @return {@link Visao} da {@link br.com.zalf.prolog.commons.colaborador.Unidade}
	 * @throws SQLException
	 */
	Visao getVisaoUnidade(Long codUnidade) throws SQLException;

	/**
	 * Insere ou atualiza as funções do prolog cadastradas para determinado cargo
	 * @param visao {@link Visao} de uma {@link Funcao}
	 * @param codUnidade codigo da unidade
	 * @param codCargo codigo do cargo
	 * @return boolean com o resultado da operação
	 * @throws SQLException caso não seja possível realizar a operação
	 */
	boolean alterarVisaoCargo(Visao visao, Long codUnidade, Long codCargo) throws SQLException;
}