package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface RaizenProdutividadeDao {

    /**
     * Insere ou atualiza a produtividade RaizenProdutividadeItem.
     *
     * @param token       - Token recebido no request.
     *                    Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param codUnidade  - Código da unidade do colaborador.
     * @param raizenItens -
     * @throws Throwable - Erro ao executar consulta no Banco de Dados.
     */
    void insertOrUpdateProdutividadeRaizen(
            @NotNull final String token,
            @NotNull final Long codUnidade,
            @NotNull final List<RaizenProdutividadeItemInsert> raizenItens) throws Throwable;

    /**
     * Insere uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token      - Token recebido no request.
     *                   Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param codUnidade - Código da unidade do colaborador.
     * @param item       - Item que será inserido.
     * @throws Throwable - Erro na execução do insert.
     */
    void insertRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws Throwable;

    /**
     * Atualiza uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token      - Token recebido no request.
     *                   Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param codUnidade - Código da unidade do colaborador.
     * @param item       - Item que será inserido.
     * @throws Throwable - Erro na execução do insert.
     */
    void updateRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws Throwable;

    /**
     * Busca as produtividades de um colaborador por um período de tempo.
     *
     * @param codUnidade  - Código da unidade do colaborador.
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List<RaizenProdutividade>} contendo a
     * {@link RaizenProdutividadeItem} de cada dia dentro do período buscado.
     * @throws Throwable - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeColaborador(@NotNull final Long codUnidade,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Busca as produtividades de um colaborador por um período de tempo.
     *
     * @param codUnidade  - Código da unidade do colaborador.
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List<RaizenProdutividade>} contendo a
     * {@link RaizenProdutividadeItem} de cada dia dentro do período buscado.
     * @throws Throwable - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeData(@NotNull final Long codUnidade,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItemVisualizacao(
            @NotNull final Long codUnidade,
            @NotNull final Long codItem) throws Throwable;

    /**
     * Busca as produtividades de um colaborador individualmente.
     *
     * @param codColaborador - Código do colaborador.
     * @param mes            - mes para a busca.
     * @param ano            - ano para a busca.
     * @return - A produtividade do colaborador.
     * @throws Throwable - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(@NotNull final Long codColaborador,
                                                                         final int mes,
                                                                         final int ano) throws Throwable;

    /**
     * Deleta uma {@link List<RaizenProdutividadeItem>}.
     *
     * @param codRaizenProdutividades - {@link List<Long>} contendo os códigos produtividades que deverão ser deletadas.
     * @throws Throwable - Erro na execução do delete.
     */
    void deleteRaizenProdutividadeItens(@NotNull final Long codUnidade,
                                        @NotNull final List<Long> codRaizenProdutividades) throws Throwable;
}
