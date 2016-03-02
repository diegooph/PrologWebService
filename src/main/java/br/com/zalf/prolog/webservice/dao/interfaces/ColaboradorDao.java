package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;
/**
 * Contém os métodos para manipular os usuários no banco de dados 
 */
public interface ColaboradorDao {
	/**
	 * Insere um colaborador no bando de dados
	 * @param request dados do colaborador a ser inserido e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir no banco de dados
	 */
	boolean insert(Request<Colaborador> request) throws SQLException;
	/**
	 * Atualiza os dados de um colaborador
	 * @param request com os dados a serem atualizados e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean update(Request<Colaborador> request) throws SQLException;
	/**
	 * Deleta um colaborador
	 * @param request contém o colaborador a ser deletado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar os dados
	 */
	boolean delete(Request<Colaborador> request) throws SQLException;
	/**
	 * Busca um colaborador pelo seu CPF
	 * @param cpf chave a ser buscada no banco de dados
	 * @param token para verificar se o usuário solicitante está logado
	 * @return um colaborador
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	Colaborador getByCod(Long cpf, String token) throws SQLException;
	/**
	 * Busca todos os colaboradores de uma unidade
	 * @param request contém os dados do solicitante e código da unidade a ser filtrada
	 * @return uma lista de colaboradores
	 * @throws SQLException caso não seja possível buscar os dados
	 */
	List<Colaborador> getAll(Request<?> request) throws SQLException;
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
}
