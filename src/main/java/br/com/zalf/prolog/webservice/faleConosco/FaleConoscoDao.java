package br.com.zalf.prolog.webservice.faleConosco;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.FaleConosco;
import br.com.zalf.prolog.models.Request;

/**
 * Contém os métodos para manipular os fale conosco
 */
public interface FaleConoscoDao {
	/**
	 * Insere um FaleConosco no banco de dados
	 * @param faleConosco objeto a ser inserido
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir no banco de dados
	 */
	boolean insert(FaleConosco faleConosco) throws SQLException;
	/**
	 * Atualiza/Edita um FaleConosco existente no banco de dados
	 * @param request contém o FaleConosco editado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar/editar
	 */
	boolean update(Request<FaleConosco> request) throws SQLException;
	/**
	 * Deleta um FaleConsco do banco de dados
	 * @param request contendo os dados do objeto a ser deletado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar
	 */
	boolean delete(Request<FaleConosco> request) throws SQLException;
	/**
	 * Busca um FaleConosco pelo código
	 * @param request contendo os dados do FaleConosco a ser buscado
	 * @return um FaleConosco
	 * @throws SQLException caso não seja possível buscar 
	 */
	FaleConosco getByCod(Request<FaleConosco> request) throws SQLException;
	/**
	 * Busca todos os FaleConosco do banco de dados
	 * @param request contendo os dados do solicitante
	 * @return lista de FaleConosco
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<FaleConosco> getAll(Request<?> request) throws SQLException;
	/**
	 * Busca os FaleConosco de um determinado colaborador
	 * @param cpf do colaborador a ser buscado os FaleConosco
	 * @return lista de FaleConosco
	 * @throws SQLException
	 */
	List<FaleConosco> getByColaborador(long cpf) throws SQLException;
}
