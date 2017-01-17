package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.login.LoginHolder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
/**
 * Contém os métodos para manipular os usuários no banco de dados 
 */
public interface ColaboradorDao {

	/**
	 * Insere um colaborador no bando de dados
	 * @param colaborador dados do colaborador a ser inserido e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir no banco de dados
	 */
	boolean insert(Colaborador colaborador) throws SQLException;

	/**
	 * Atualiza os dados de um colaborador
	 * @param cpfAntigo cpf do colaborador a ser atualizado
	 * @param colaborador dados do colaborador a ser atualizados
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean update(Long cpfAntigo, Colaborador colaborador) throws SQLException;

	/**
	 * Deleta um colaborador
	 * @param cpf contém o cpf do colaborador a ser deletado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean delete(Long cpf) throws SQLException;

	/**
	 * Busca um colaborador pelo seu CPF
	 * @param cpf chave a ser buscada no banco de dados
	 * @return um colaborador
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	Colaborador getByCod(Long cpf) throws SQLException;

	/**
	 * Busca todos os colaboradores de uma unidade
	 * @param codUnidade código da unidade
	 * @return uma lista de colaboradores
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	List<Colaborador> getAll(Long codUnidade) throws SQLException;

	/**
	 * Verifica a existência de um CPF e data de nascimento
	 * @param cpf do colaborador a ser verificada a existência
	 * @param dataNascimento do colaborador a ser veerificada a existência
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível verificar a existência no banco de dados
	 */
	boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException;

	/**
	 * Busca os dados de uma função pelo seu código 
	 * @param codigo da função a ser buscada
	 * @return uma Função
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	Funcao getFuncaoByCod(Long codigo) throws SQLException;

	/**
	 * Busca os colaboradores ativos pelo código de uma determinada unidade
	 * @param codUnidade código da unidade a buscar os colaboradores
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @param cpf do solicitante, para verificar se esta devidamente logado
	 * @return lista de Colaborador
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Colaborador> getAtivosByUnidade(Long codUnidade, String token, Long cpf) throws SQLException;

	/**
	 * retorna o login do colaborador com o cpf marcado
	 * @param cpf cpf do usuario a se logar
	 * @return o login do colaborador com o cpf marcado
	 * @throws SQLException caso ocorrer erro no banco
	 */
	LoginHolder getLoginHolder(Long cpf) throws SQLException;

	/**
	 * Verifica se determinado CPF existe em determinada unidade
	 * @param cpf cpf a ser verificado
	 * @param codUnidade codigo da unidade ao qual o cpf deve pertencer
	 * @return
	 * @throws SQLException
     */
	public boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException;
}
