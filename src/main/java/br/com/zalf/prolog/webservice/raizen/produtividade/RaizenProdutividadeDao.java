package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
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
     * @param codEmpresa  - Código da empresa do colaborador.
     * @param raizenItens -
     * @throws SQLException - Erro ao executar consulta no Banco de Dados.
     */
    void insertOrUpdateProdutividadeRaizen(@NotNull final String token,
                                           @NotNull final Long codEmpresa,
                                           @NotNull final List<RaizenProdutividadeItemInsert> raizenItens) throws SQLException;

    /**
     * Insere uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token      - Token recebido no request.
     *                   Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param codEmpresa - Código da empresa do colaborador.
     * @param item       - Item que será inserido.
     * @throws SQLException - Erro na execução do insert.
     */
    void insertRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codEmpresa,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws SQLException;

    /**
     * Atualiza uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token      - Token recebido no request.
     *                   Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param codEmpresa - Código da empresa do colaborador.
     * @param item       - Item que será inserido.
     * @throws SQLException - Erro na execução do insert.
     */
    void updateRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codEmpresa,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws SQLException;

    /**
     * Busca as produtividades de um colaborador por um período de tempo.
     *
     * @param codEmpresa  - Código da empresa do colaborador.
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List<RaizenProdutividade>} contendo a
     * {@link RaizenProdutividadeItem} de cada dia dentro do período buscado.
     * @throws SQLException - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeColaborador(@NotNull final Long codEmpresa,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Busca as produtividades de um colaborador por um período de tempo.
     *
     * @param codEmpresa  - Código da empresa do colaborador.
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List<RaizenProdutividade>} contendo a
     * {@link RaizenProdutividadeItem} de cada dia dentro do período buscado.
     * @throws SQLException - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeData(@NotNull final Long codEmpresa,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Busca as produtividades de um colaborador individualmente.
     *
     * @param codColaborador - Código do colaborador.
     * @param mes            - mes para a busca.
     * @param ano            - ano para a busca.
     * @return - Um {@link List<RaizenProdutividade>} contendo a
     * {@link RaizenProdutividadeItem} do colaborador.
     * @throws SQLException - Erro na execução da busca dos dados no Banco.
     */
    @NotNull
    List<RaizenProdutividadeIndividualHolder> getRaizenProdutividade(Long codColaborador, int mes, int ano) throws SQLException;

    /**
     * Deleta uma {@link List<RaizenProdutividadeItem>}.
     *
     * @param codRaizenProdutividades - {@link List<Long>} contendo os códigos produtividades que deverão ser deletadas.
     * @throws SQLException - Erro na execução do delete.
     */
    void deleteRaizenProdutividadeItens(@NotNull final Long codEmpresa,
                                        @NotNull final List<Long> codRaizenProdutividades) throws SQLException;
}
