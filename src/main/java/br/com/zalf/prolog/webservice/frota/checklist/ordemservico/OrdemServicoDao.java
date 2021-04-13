package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface OrdemServicoDao {

    @Nullable
    Long processaChecklistRealizado(@NotNull final Connection conn,
                                    @NotNull final Long codChecklistInserido,
                                    @NotNull final ChecklistInsercao checklist) throws Throwable;

    @NotNull
    List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                       @Nullable final Long codTipoVeiculo,
                                                       @Nullable final Long codVeiculo,
                                                       @Nullable final StatusOrdemServico statusOrdemServico,
                                                       final int limit,
                                                       final int offset) throws Throwable;

    @NotNull
    List<QtdItensPlacaListagem> getQtdItensPlacaListagem(@NotNull final Long codUnidade,
                                                         @Nullable final Long codTipoVeiculo,
                                                         @Nullable final Long codVeiculo,
                                                         @Nullable final StatusItemOrdemServico statusItens,
                                                         final int limit,
                                                         final int offset) throws Throwable;

    @NotNull
    HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(@NotNull final Long codUnidade,
                                                               @NotNull final Long codOrdemServico) throws Throwable;

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @Nullable final PrioridadeAlternativa prioridade,
            @Nullable final StatusItemOrdemServico statusItens,
            final int limit,
            final int offset) throws Throwable;

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable;

    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;

    void incrementaQtdApontamentos(
            @NotNull final Connection conn,
            @NotNull final Long codChecklistInserido,
            @NotNull final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos)
            throws Throwable;

    /**
     * Método usado apenas pela integrção
     *
     * @deprecated at 2020-03-15. Use {@link OrdemServicoDao#getItensStatus(Connection, Long, Long, Long)}.
     * Será retirado em tarefa própria de integração que irá remover placa do checklist - PL.
     */
    @Deprecated
    @NotNull
    Map<Long, List<InfosAlternativaAberturaOrdemServico>> getItensStatus(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final String placaVeiculo) throws Throwable;

    @NotNull
    Map<Long, List<InfosAlternativaAberturaOrdemServico>> getItensStatus(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final Long codVeiculo) throws Throwable;
}