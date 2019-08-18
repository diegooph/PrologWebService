package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by didi on 9/16/16.
 */
@Deprecated
public interface DEPRECATED_ORDEM_SERVICO_DAO_2 {

    /**
     * Conserta um {@link ItemOrdemServico item}.
     *
     * @param item item consertado
     * @throws Throwable se ocorrer algum erro
     */
    void consertaItem(@NotNull final ItemOrdemServico item) throws Throwable;

    /**
     * Conserta múltiplos {@link ItemOrdemServico itens}.
     *
     * @param itensConserto os itens para conserto
     * @throws Throwable se ocorrer algum erro
     */
    void consertaItens(@NotNull final ConsertoMultiplosItensOs itensConserto) throws Throwable;

    /**
     * Busca as ordens de serviços.
     *
     * @param placa       placa do veículo
     * @param status      status
     * @param codUnidade  código da unidade
     * @param tipoVeiculo tipo de veículo
     * @param limit       limit de busca no banco
     * @param offset      offset de busca no banco
     * @return uma lista de Ordens de serviços
     * @throws SQLException caso ocorrer erro no banco
     */
    List<OrdemServico> getOs(String placa, String status, Long codUnidade,
                             String tipoVeiculo, Integer limit, Long offset) throws SQLException;

	/**
     * Busca os itens de Ordens de Serviços utilizando, opcionalmente, limit e offset.
     *
     * @param placa       Placa do veículo que será buscado os itens.
     * @param statusItens Status do item, {@link ItemOrdemServico.Status#RESOLVIDO}
     *                    ou {@link ItemOrdemServico.Status#PENDENTE}.
     * @param prioridade  Prioridade do Item: BAIXA, ALTA ou CRITICA.
     * @param limit       Limite da busca dos dados.
     * @param offset      Offset de busca dos dados.
     * @return Lista de Itens de Ordem de Serviço
     * @throws SQLException Se algum erro acontecer
     */
    @NotNull
    List<ItemOrdemServico> getItensOs(@NotNull final String placa,
                                      @NotNull final String statusItens,
									  @NotNull final String prioridade,
                                      @Nullable final Integer limit,
                                      @Nullable final Long offset) throws SQLException;/**
     * Busca os Itens de uma Ordem de Serviço específica.
     *
     * @param codOs        Código da OS que será buscado os itens.
     * @param codUnidade   Código da Unidade da OS.
     * @param statusItemOs Status dos itens a serem buscados.
     * @return Lista de Itens da Ordem de Serviço.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    List<ItemOrdemServico> getItensOs(@NotNull final Long codOs,
                                      @NotNull final Long codUnidade,
                                      @Nullable final String statusItemOs) throws Throwable;

    List<ItemOrdemServico> getItensOs(@NotNull final String placa,
                                      @NotNull final Date untilDate,
                                      @NotNull final ItemOrdemServico.Status statusItem,
                                      @NotNull final String prioridadeItem,
                                      final boolean itensCriticosRetroativos) throws SQLException;

    /**
     * Método chamado quando é recebido um checklist, verifica as premissas para criar uma nova OS ou add
     * o item com problema a uma OS existente.
     *
     * @param checklist  um checklist
     * @param conn       conexão do banco
     * @param codUnidade código da unidade
     * @throws SQLException se ocorrer erro no banco
     */
    void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException;

    /**
     * Busca a lista de itens agrupados por placa e criticidade.
     */
    @NotNull
    List<ManutencaoHolder> getResumoManutencaoHolder(@NotNull final Long codUnidade,
                                                     @Nullable final Long codTipoVeiculo,
                                                     @Nullable final String placaVeiculo,
                                                     final boolean itensEmAberto,
                                                     final int limit,
                                                     final int offset) throws Throwable;
}