package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.List;
/**
 * Contém os métodos para manipular os usuários no banco de dados.
 */
public interface ColaboradorDao {

	/**
	 * Insere um colaborador no bando de dados.
	 *
	 * @param colaborador dados do colaborador a ser inserido e dados do solicitante
	 * @return verdadeiro caso operação realizada com sucesso, falso caso contrário
	 * @throws SQLException caso não seja possível inserir no banco de dados
	 */
	boolean insert(Colaborador colaborador) throws SQLException;

	/**
	 * Atualiza os dados de um colaborador.
	 *
	 * @param cpfAntigo cpf do colaborador a ser atualizado
	 * @param colaborador dados do colaborador a ser atualizados
	 * @return verdadeiro caso operação realizada com sucesso, falso caso contrário
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean update(Long cpfAntigo, Colaborador colaborador) throws SQLException;

	/**
	 * Deleta um colaborador.
	 *
	 * @param cpf contém o cpf do colaborador a ser deletado e dados do solicitante
	 * @return verdadeiro caso operação realizada com sucesso, falso caso contrário
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean delete(Long cpf) throws SQLException;

	/**
	 * Busca um colaborador pelo seu CPF.
	 *
	 * @param cpf chave a ser buscada no banco de dados
	 * @return um colaborador
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	Colaborador getByCpf(Long cpf) throws SQLException;

	/**
	 * Busca todos os colaboradores de uma unidade.
	 *
	 * @param codUnidade código da unidade
	 * @return uma lista de colaboradores
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	List<Colaborador> getAll(Long codUnidade) throws SQLException;

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

	List<Colaborador> getColaboradoresComAcessoFuncaoByUnidade(final int codFuncaoProLog,
															   @NotNull final Long codUnidade) throws SQLException;

	Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException;
}
