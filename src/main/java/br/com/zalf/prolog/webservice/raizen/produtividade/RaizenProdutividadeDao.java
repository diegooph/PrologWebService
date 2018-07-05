package br.com.zalf.prolog.webservice.raizen.produtividade;

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
     * @param raizenItens -
     * @throws SQLException - Erro ao executar consulta no Banco de Dados.
     */
    void insertOrUpdateProdutividadeRaizen(@NotNull final String token,
                                           @NotNull final List<RaizenProdutividadeItem> raizenItens) throws SQLException;

    /**
     * Insere uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token - Token recebido no request.
     *              Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param item  - Item que será inserido.
     * @throws SQLException - Erro na execução do insert.
     */
    void insertRaizenItem(@NotNull final String token,
                          @NotNull final RaizenProdutividadeItem item) throws SQLException;

    /**
     * Atualiza uma {@link RaizenProdutividadeItem} específica.
     *
     * @param token - Token recebido no request.
     *              Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param item  - Item que será inserido.
     * @throws SQLException - Erro na execução do insert.
     */
    void updateRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codUnidade,
                                       @NotNull final RaizenProdutividadeItem item) throws SQLException;

    /**
     * Busca as produtividades de um colaborador por um período de tempo.
     *
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List< RaizenProdutividade >} contendo a
     * {@link RaizenProdutividadeItem} de cada dia dentro do período buscado.
     * @throws SQLException - Erro na execução da busca dos dados no Banco.
     */
    List<RaizenProdutividade> getRaizenProdutividade(@NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Deleta uma {@link List<RaizenProdutividadeItem>}.
     *
     * @param codRaizenProdutividade - {@link List<Long>} contendo os códigos das escalas que deverão ser deletaddas.
     * @throws SQLException - Erro na execução do delete.
     */
    void deleteRaizenProdutividadeItens(@NotNull final List<Long> codRaizenProdutividade) throws SQLException;
}
