package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface OrdemServicoDao {

    /**
     * Método chamado quando é recebido um checklist, verifica as premissas para criar uma nova OS ou add
     * o item com problema a uma OS existente.
     *
     * @param checklist  um checklist
     * @param conn       conexão do banco
     * @param codUnidade código da unidade
     * @throws Throwable se ocorrer algum erro.
     */
    void criarItemOrdemServico(@NotNull final Connection conn,
                               @NotNull final Long codUnidade,
                               @NotNull final Checklist checklist) throws Throwable;

    /**
     * Busca as ordens de serviços.
     *
     * @throws Throwable se ocorrer algum erro.
     **/
    @NotNull
    List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                       @Nullable final Long tipoVeiculo,
                                                       @Nullable final String placa,
                                                       @Nullable final StatusOrdemServico statusOrdemServico,
                                                       final int limit,
                                                       final int offset) throws Throwable;

    /**
     * Busca a lista de itens agrupados por placa e criticidade.
     *
     * @throws Throwable se ocorrer algum erro.
     */
    @NotNull
    List<QtdItensPlacaListagem> getQtdItensPlacaListagem(@NotNull final Long codUnidade,
                                                         @Nullable final Long codTipoVeiculo,
                                                         @Nullable final String placaVeiculo,
                                                         @Nullable final StatusItemOrdemServico statusItemOrdemServico,
                                                         final int limit,
                                                         final int offset) throws Throwable;

    /**
     * Busca os itens de Ordens de Serviços utilizando, opcionalmente, limit e offset.
     *
     * @throws Throwable se ocorrer algum erro.
     */
    @NotNull
    HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(@NotNull final Long codUnidade,
                                                               @NotNull final Long codOrdemServico) throws Throwable;

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @NotNull final PrioridadeAlternativa prioridade) throws Throwable;

    /**
     * Resolve um item.
     *
     * @param item item resolvido.
     * @throws Throwable se ocorrer algum erro.
     */
    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    /**
     * Resolve múltiplos itens de uma Ordem de Serviço.
     *
     * @param itensResolucao os itens para resolução.
     * @throws Throwable se ocorrer algum erro.
     */
    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;
}