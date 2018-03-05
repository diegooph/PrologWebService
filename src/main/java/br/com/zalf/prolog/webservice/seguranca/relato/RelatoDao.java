package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém métodos para manipular os Relatos 
 */
public interface RelatoDao {

	/**
	 * Insere um relato no banco de dados
	 * @param relato um Relato
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir
	 */
	boolean insert(Relato relato) throws SQLException;

	/**
	 * Deleta um Relato do banco de dados
	 * @param codRelato contém código do Relato a ser deletado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar o Relato
	 */
	boolean delete(Long codRelato) throws SQLException ;

	/**
	 * Busca um relato pelo seu código
	 * @param codRelato contém o código do Relato a ser buscado
	 * @return um Relato
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	Relato getByCod(@NotNull final Long codRelato, @NotNull final String userToken) throws SQLException;

	//TODO - completar os javadoc

	List<Relato> getAll(Long codUnidade, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) throws SQLException;

	List<Relato> getRealizadosByColaborador(Long cpf, int limit, long offset, double latitude,
											double longitude, boolean isOrderByDate, String status, String campoFiltro) throws SQLException;

	List<Relato> getAllExcetoColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) throws SQLException;

	List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
								 Long codUnidade,long limit, long offset, String status) throws SQLException;

	boolean classificaRelato(Relato relato) throws SQLException;

	boolean fechaRelato(Relato relato) throws SQLException;

	List<Alternativa> getAlternativas(Long codUnidade, Long codSetor) throws SQLException;

}
