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
public final class OrdemServicoDaoImpl implements OrdemServicoDao {

    @Override
    public void insertItemOs(@NotNull final Connection conn,
                             @NotNull final Long codUnidade,
                             @NotNull final Checklist checklist) throws Throwable {

    }

    @NotNull
    @Override
    public List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                              @Nullable final Long tipoVeiculo,
                                                              @Nullable final String placa,
                                                              @Nullable final StatusOrdemServico statusOrdemServico,
                                                              @Nullable final Integer limit,
                                                              @Nullable final Integer offset) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public List<QtdItensPlacaListagem> getQtdItensPlacaListagem(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoVeiculo,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItemOrdemServico,
            final int limit,
            final int offset) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(
            @NotNull final Long codUnidade,
            @NotNull final Long codOrdemServico) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @NotNull final PrioridadeAlternativa prioridade) throws Throwable {
        return null;
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {

    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {

    }
}