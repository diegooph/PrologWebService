package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklistOrdemServico;
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
    public Response resolverItem(final String token,
                                 final ResolverItemOrdemServico item) throws ProLogException {
        try {
            OrdemServicoValidator.validaResolucaoItem(TimeZoneManager.getZoneIdForToken(token), item);
            RouterChecklistOrdemServico
                    .create(dao, token)
                    .resolverItem(item);
            return Response.ok("Item resolvido com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver item", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver item, tente novamente");
        }
    }

    @NotNull
    public Response resolverItens(final String token,
                                  final ResolverMultiplosItensOs itensResolucao) throws ProLogException {
        try {
            OrdemServicoValidator.validaResolucaoMultiplosItens(TimeZoneManager.getZoneIdForToken(token),
                                                                itensResolucao);
            RouterChecklistOrdemServico
                    .create(dao, token)
                    .resolverItens(itensResolucao);
            return Response.ok("Itens resolvidos com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver itens", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver itens, tente novamente");
        }
    }

    @NotNull
    List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                       @Nullable final Long codTipoVeiculo,
                                                       @Nullable final Long codVeiculo,
                                                       @Nullable final StatusOrdemServico statusOrdemServico,
                                                       final int limit,
                                                       final int offset) throws ProLogException {
        try {
            return dao.getOrdemServicoListagem(
                    codUnidade,
                    codTipoVeiculo,
                    codVeiculo,
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
    List<QtdItensPlacaListagem> getQtdItensPlacaListagem(@NotNull final Long codUnidade,
                                                         @Nullable final Long codTipoVeiculo,
                                                         @Nullable final String placaVeiculo,
                                                         @Nullable final StatusItemOrdemServico statusItens,
                                                         final int limit,
                                                         final int offset) throws ProLogException {
        try {
            return dao.getQtdItensPlacaListagem(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    statusItens,
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
    HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(
            @NotNull final Long codUnidade,
            @NotNull final Long codOrdemServico) throws ProLogException {
        try {
            return dao.getHolderResolucaoOrdemServico(codUnidade, codOrdemServico);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar holder de resolução das ordens de serviços para a unidade: "
                    + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @Nullable final PrioridadeAlternativa prioridade,
            @Nullable final StatusItemOrdemServico statusItens,
            final int limit,
            final int offset) throws ProLogException {
        try {
            return dao.getHolderResolucaoItensOrdemServico(placaVeiculo, prioridade, statusItens, limit, offset);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar holder de resolução de itens de O.S. para a placa: " + placaVeiculo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws ProLogException {
        try {
            if (placaVeiculo == null && (codUnidade == null || codOrdemServico == null)) {
                throw new IllegalStateException(
                        "Já que a placa é nula, você deve filtrar por código da unidade e da O.S.");
            }

            return dao.getHolderResolucaoMultiplosItens(codUnidade, codOrdemServico, placaVeiculo, statusItens);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar holder de resolução de múltiplos itens para a placa: " + placaVeiculo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar busca, tente novamente");
        }
    }
}