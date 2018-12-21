package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
/**
 * Contém os métodos para manipular os usuários no banco de dados.
 */
public interface ColaboradorDao {

	/**
	 * Insere um {@link Colaborador colaborador} no bando de dados.
	 *
	 * @param colaborador dados do colaborador a ser inserido e dados do solicitante
	 * @param listener para repassarmos o evento de que um colaborador está sendo inativado.
	 * @throws Throwable caso não seja possível inserir no banco de dados
	 */
	void insert(Colaborador colaborador, DadosIntervaloChangedListener listener) throws Throwable;

	/**
	 * Atualiza os dados de um {@link Colaborador colaborador}.
	 *
	 * @param cpfAntigo CPF do colaborador a ser atualizado.
	 * @param colaborador dados do colaborador a ser atualizados.
	 * @throws Throwable caso não seja possível atualizar o colaborador.
	 */
	void update(Long cpfAntigo, Colaborador colaborador, DadosIntervaloChangedListener listener) throws Throwable;

	void updateStatus(Long cpf, Colaborador colaborador) throws SQLException;

	/**
	 * Para manter histórico no banco de dados, não é feita exclusão de colaborador,
	 * setamos o status para inativo.
	 *
	 * @param cpf CPF do colaborador a ser inativado.
	 * @param listener para repassarmos o evento de que um colaborador está sendo inativado.
	 * @throws Throwable caso não seja possível inativar o colaborador.
	 */
	void delete(Long cpf, DadosIntervaloChangedListener listener) throws Throwable;

	/**
	 * Busca um colaborador por seu CPF.
	 *
	 * @param cpf CPF do {@link Colaborador} que queremos buscar.
	 * @param apenasAtivos indica se queremos buscar considerando apenas os colaboradores ativos.
	 * @return um {@link Colaborador}.
	 * @throws SQLException caso aconteça algum erro na consulta ao banco.
	 */
	Colaborador getByCpf(Long cpf, boolean apenasAtivos) throws SQLException;

	/**
	 * Busca todos os colaboradores de uma unidade.
	 *
	 * @param codUnidade código da unidade.
	 * @param apenasAtivos indica se queremos buscar apenas os colaboradores que estão ativos.
	 * @return uma lista de colaboradores.
	 * @throws SQLException caso não seja possível buscar os dados.
	 */
	@NotNull
	List<Colaborador> getAllByUnidade(@NotNull final Long codUnidade, final boolean apenasAtivos) throws Throwable;

	/**
	 * Busca todos os colaboradores de uma empresa.
	 *
	 * @param codEmpresa código da empresa.
	 * @param apenasAtivos indica se queremos buscar apenas os colaboradores que estão ativos.
	 * @return uma lista de colaboradores.
	 * @throws SQLException caso não seja possível buscar os dados.
	 */
	@NotNull
	List<Colaborador> getAllByEmpresa(@NotNull final Long codEmpresa, final boolean apenasAtivos) throws Throwable;

	/**
	 * Busca apenas os motoristas e ajudantes de uma unidade
	 * @param codUnidade código da unidade
	 * @return uma lista de colaboradores
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException;

	/**
	 * Verifica se determinado CPF existe em determinada unidade.
	 *
	 * @param cpf cpf a ser verificado
	 * @param codUnidade codigo da unidade ao qual o cpf deve pertencer
	 * @return verdadeiro caso CPF exista, falso caso contrário
	 * @throws SQLException caso não seja possível realizar a operação
     */
	boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException;

	/**
	 * Busca um colaborador por seu token.
	 *
	 * @param token um token.
	 * @return um {@link Colaborador}.
	 * @throws SQLException caso ocorrer erro no banco
	 */
	@NotNull
    Colaborador getByToken(@NotNull final String token) throws SQLException;

	/**
	 * Método que busca uma {@link List<Colaborador>} que possuem acesso à um {@code codFuncaoProLog} específico.
	 *
	 * @param codUnidade      Código da {@link Unidade} que será buscado os colaboradores.
	 * @param codFuncaoProLog Código da {@link FuncaoProLog} que estamos filtrando.
	 * @return Uma {@link List<Colaborador>} que possuem a {@link FuncaoProLog} em questão.
	 * @throws SQLException Caso algum erro na busca acontecer.
	 */
	@NotNull
	List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(@NotNull final Long codUnidade,
															   final int codFuncaoProLog) throws SQLException;

	Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException;


	/**
	 * Verifica se um colaborador tem acesso a uma funcionalidade específica do ProLog. A verificação acontece
	 * estando o colaborador ativo ou não.
	 *
	 * @param cpf CPF do colaborador.
	 * @param codPilar código do pilar do qual a função pertence.
	 * @param codFuncaoProLog código único da função no ProLog.
	 * @return {@code true} se o colaborador tem acesso; {@code false} caso contrário.
	 * @throws SQLException caso aconteça algum erro na consulta.
	 */
	boolean colaboradorTemAcessoFuncao(@NotNull final Long cpf, final int codPilar, final int codFuncaoProLog) throws SQLException;
}
