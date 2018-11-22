package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
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

import java.util.List;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoService {
    private static final String TAG = OrdemServicoService.class.getSimpleName();
    @NotNull
    private final OrdemServicoDao dao = Injection.provideOrdemServicoDao();

    @NotNull
    public List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                              @Nullable final Long codTipoVeiculo,
                                                              @Nullable final String placaVeiculo,
                                                              @Nullable final StatusOrdemServico statusOrdemServico,
                                                              final int limit,
                                                              final int offset) throws ProLogException {
        try {
            return dao.getOrdemServicoListagem(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    statusOrdemServico,
                    limit,
                    offset);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar ordens de serviço para a unidade: " + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    @NotNull
    public List<QtdItensPlacaListagem> getQtdItensPlacaListagem(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoVeiculo,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItemOrdemServico,
            final int limit,
            final int offset) throws ProLogException {
        try {
            return dao.getQtdItensPlacaListagem(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    statusItemOrdemServico,
                    limit,
                    offset);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar quantidade de itens de O.S. para a unidade: " + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    @NotNull
    public HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(@NotNull final Long codUnidade,
                                                                      @NotNull final Long codOrdemServico)
            throws ProLogException {
        try {
            return dao.getHolderResolucaoOrdemServico(codUnidade, codOrdemServico);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar holder de resolução das ordens de serviços para a unidade: " + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    @NotNull
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @NotNull final PrioridadeAlternativa prioridade) throws ProLogException {
        try {
            return dao.getHolderResolucaoItensOrdemServico(placaVeiculo, prioridade);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar holder de resolução de itens de O.S. para a placa: " + placaVeiculo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws ProLogException {
        try {
            dao.resolverItem(item);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver item", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver item, tente novamente");
        }
    }


    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws ProLogException {
        try {
            dao.resolverItens(itensResolucao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver itens", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver itens, tente novamente");
        }
    }
}