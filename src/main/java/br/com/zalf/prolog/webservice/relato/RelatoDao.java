package br.com.zalf.prolog.webservice.relato;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.models.Request;

/**
 * Contém métodos para manipular os Relatos 
 */
public interface RelatoDao {
	/**
	 * INsere um relato no banco de dados
	 * @param relato um Relato
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir
	 */
	boolean insert(Relato relato) throws SQLException;
	/**
	 * Atualiza/Edita um Relato no banco de dados
	 * @param request contém o Relato a ser atualizado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o update
	 */
	boolean update(Request<Relato> request) throws SQLException;
	/**
	 * Deleta um Relato do banco de dados
	 * @param request contém o Relato a ser deletado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar o Relato
	 */
	boolean delete(Request<Relato> request) throws SQLException;
	/**
	 * Busca um relato pelo seu código
	 * @param request contém o Relato a ser buscado e dados do solicitante
	 * @return um Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	Relato getByCod(Request<Relato> request) throws SQLException;
	/**
	 * Busca todos os Relatos
	 * @param request contém os dados do solicitante
	 * @return lista de Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Relato> getAll(Request<?> request) throws SQLException;
	/**
	 * Busca os relatos de um colaborador
	 * @param cpf ao qual serão buscados os relatos
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @param limit máximo de resultados retornados
	 * @param offset a partir de qual resultado será enviado
	 * @param latitude para calcular a distância do solicitante até o relato
	 * @param longitude para calcular a distância do solicitante até o relato
	 * @param isOrderByDate flag de ordenamento, true ordena por data, false ordena por distância
	 * @return lista de Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Relato> getByColaborador(Long cpf, String token, int limit, long offset, double latitude, double longitude, boolean isOrderByDate) throws SQLException;
	/**
	 * Busca todos os relatos exceto os realizados pelo solicitante
	 * @param cpf ao qual os relatos não aparecerão na lista
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @param limit máximo de resultados retornados
	 * @param offset a partir de qual resultado será enviado
	 * @param latitude para calcular a distância do solicitante até o relato
	 * @param longitude para calcular a distância do solicitante até o relato
	 * @param isOrderByDate flag de ordenamento, true ordena por data, false ordena por distância
	 * @return lista de Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Relato> getAllExcetoColaborador(Long cpf, String token, int limit, long offset, double latitude, double longitude, boolean isOrderByDate) throws SQLException;
	/**
	 * Busca todos os relatos de uma determinada unidade, respeitando o período selecionado e a equipe
	 * @param dataInicial uma Data
	 * @param dataFinal uma Data 
	 * @param equipe uma Equipe
	 * @param codUnidade código da unidade
	 * @param cpf do solicitante
	 * @param token para verificar se o solicitante esta devidamente logado
	  *@param limit máximo de resultados retornados
	 * @param offset a partir de qual resultado será enviado
	 * @return lista de Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token,long limit, long offset) throws SQLException;
}
