package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import com.sun.istack.internal.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contém os métodos para manipular os checklists no banco de dados 
 */
public interface ChecklistDao {

	/**
	 * Insere um checklist no BD salvando na tabela CHECKLIST e chamando métodos
	 * especificos que salvam as respostas do map na tabela CHECKLIST_RESPOSTAS.
	 *
	 * @param checklist um checklist
	 * @return código do checklist recém inserido
	 * @throws SQLException caso não seja possível inserir o checklist no banco de dados
	 */
	@NotNull
	Long insert(Checklist checklist) throws SQLException;

	/**
	 * Busca um checklist pelo seu código único
	 * @param codChecklist codigo do checklist a ser buscado
	 * @return um checklist
	 * @throws SQLException caso não consiga buscar o checklist no banco de dados
	 */
	Checklist getByCod(long codChecklist) throws SQLException;

	/**
	 * Busca todos os checklists, respeitando os filtros aplicados (recebidos por parâmetro).
	 *
	 * @return uma {@link List<Checklist> lista de checklists}.
	 * @throws SQLException caso não seja possível realizar a busca.
	 */
	@Nonnull
	List<Checklist> getAll(@Nonnull final Long codUnidade,
						   @Nullable final Long codEquipe,
						   @Nullable final Long codTipoVeiculo,
						   @Nullable final String placaVeiculo,
						   long dataInicial,
						   long dataFinal,
						   int limit,
						   long offset,
						   boolean resumido) throws SQLException;

	/**
	 * Busca os checklists realizados por um colaborador
	 * @param cpf um cpf
	 * @param limit quantidade de checks buscados no banco
	 * @param offset a partir de qual check será  abusca
	 * @return lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getByColaborador(Long cpf, int limit, long offset, boolean resumido) throws SQLException;

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
	 * @param tipoChecklist o tipo do {@link Checklist checklist} sendo realizado
	 * @return retorno um novo checklist
	 * @throws SQLException caso ocorrer erro no banco
	 */
	NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa, char tipoChecklist) throws SQLException;

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