package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contém os métodos para manipular os checklists no banco de dados 
 */
public interface ChecklistDao {

	/**
	 * Insere um checklist no banco de dados
	 * @param checklist um checklist
	 * @return boolean com o resultado da operação
	 * @throws SQLException caso não seja possível inserir o checklist no banco de dados
	 */
	boolean insert(Checklist checklist) throws SQLException;

	/**
	 * Busca um checklist pelo seu código único
	 * @param codChecklist codigo do checklist a ser buscado
	 * @return um checklist
	 * @throws SQLException caso não consiga buscar o checklist no banco de dados
	 */
	Checklist getByCod(long codChecklist) throws SQLException;

	/**
	 * Busca todos os checklists, respeitando os filtros aplicados (recebidos por parâmetro)
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param equipe string contendo o nome da equipe ou '%' para o caso de buscar os checklists de todas
	 * @param codUnidade código da unidade
	 * @param limit quantidade de checks buscados no banco
	 * @param offset a partir de qual check será  abusca
	 * @return lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Checklist> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
						   Long codUnidade, String placa, long limit, long offset) throws SQLException;

	/**
	 * Busca os checklists realizados por um colaborador
	 * @param cpf um cpf
	 * @param limit quantidade de checks buscados no banco
	 * @param offset a partir de qual check será  abusca
	 * @return lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getByColaborador(Long cpf, int limit, long offset) throws SQLException;

	/**
	 * busca a url das imagens das perguntas
	 * @param codUnidade código da unidade
	 * @param codFuncao código da função
	 * @return retorna uma lista de Strings contendo as URLs
	 * @throws SQLException caso der erro no banco
	 */
	List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao) throws SQLException;

	/**
	 * busca um novo checklist de perguntas
	 * @param codUnidade código da unidade
	 * @param codModelo código do modelo
	 * @param placa placa do veículo
	 * @return retorno um novo checklist
	 * @throws SQLException caso ocorrer erro no banco
	 */
	NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa) throws SQLException;

	//TODO - adicionar comentário javadoc
	Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao) throws SQLException;


	@NotNull
	FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
									 @NotNull final Date dataInicial,
									 @NotNull final Date dataFinal,
									 final boolean itensCriticosRetroativos) throws SQLException;

	/**
	 * busca o status de liberação do veículo
	 * @param codUnidade código da unidade
	 * @return lista de veiculos com liberação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	@Deprecated
	List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) throws SQLException;
}