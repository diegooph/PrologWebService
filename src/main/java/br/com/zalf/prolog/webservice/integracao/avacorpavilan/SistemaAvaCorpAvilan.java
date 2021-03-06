package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaAvaCorpAvilan extends Sistema {
    public SistemaAvaCorpAvilan(@NotNull final SistemaKey sistemaKey,
                                @NotNull final RecursoIntegrado recursoIntegrado,
                                @NotNull final IntegradorProLog integradorProLog,
                                @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
    }

    @Override
    @NotNull
    public InfosChecklistInserido insertChecklist(@NotNull final ChecklistInsercao checklist,
                                                  final boolean foiOffline,
                                                  final boolean deveAbrirOs) throws Throwable {
        final InfosChecklistInserido infosChecklistInserido =
                Injection.provideChecklistDao().insertChecklist(checklist, foiOffline, deveAbrirOs);
        if (infosChecklistInserido.abriuOs()
                && verificaModeloChecklistIntegrado(checklist.getCodUnidade(), checklist.getCodModelo())) {
            //noinspection ConstantConditions
            final Long codInternoOsProlog = Injection
                    .provideIntegracaoDao()
                    .insertOsPendente(checklist.getCodUnidade(), infosChecklistInserido.getCodOsAberta());
            enviaOsIntegrada(Collections.singletonList(codInternoOsProlog));
        }

        return infosChecklistInserido;
    }

    @Override
    @NotNull
    public InfosChecklistInserido insertChecklistOffline(@NotNull final ChecklistInsercao checklist) throws Throwable {
        final InfosChecklistInserido infosChecklistInserido =
                Injection.provideChecklistOfflineDao().insertChecklistOffline(checklist);
        if (infosChecklistInserido.abriuOs()
                && verificaModeloChecklistIntegrado(checklist.getCodUnidade(), checklist.getCodModelo())) {
            //noinspection ConstantConditions
            final Long codInternoOsProlog = Injection
                    .provideIntegracaoDao()
                    .insertOsPendente(checklist.getCodUnidade(), infosChecklistInserido.getCodOsAberta());
            enviaOsIntegrada(Collections.singletonList(codInternoOsProlog));
        }

        return infosChecklistInserido;
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        // Fecha o item no Prolog.
        getIntegradorProLog().resolverItem(item);
        // Marca a OS como pendente para sincronizar.
        final List<Long> codsInternoOsProlog = Injection
                .provideIntegracaoDao()
                .buscaCodOsByCodItem(Collections.singletonList(item.getCodItemResolvido()));
        if (!codsInternoOsProlog.isEmpty()) {
            Injection
                    .provideIntegracaoDao()
                    .atualizaStatusOsIntegrada(
                            codsInternoOsProlog,
                            true,
                            false,
                            // N??o incrementa, pois somente quando ?? feito o envio, deve ser incrementado.
                            false);
            enviaOsIntegrada(codsInternoOsProlog);
        }
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        // Fecha os itens no Prolog.
        getIntegradorProLog().resolverItens(itensResolucao);
        // Marca OSs dos itens como pendentes para sincronizar.
        final List<Long> codsInternoOsProlog = Injection
                .provideIntegracaoDao()
                .buscaCodOsByCodItem(itensResolucao.getCodigosItens());
        if (!codsInternoOsProlog.isEmpty()) {
            Injection
                    .provideIntegracaoDao()
                    .atualizaStatusOsIntegrada(
                            codsInternoOsProlog,
                            true,
                            false,
                            // N??o incrementa, pois somente quando ?? feito o envio, deve ser incrementado.
                            false);
            enviaOsIntegrada(codsInternoOsProlog);
        }
    }

    private boolean verificaModeloChecklistIntegrado(@NotNull final Long codUnidade,
                                                     @NotNull final Long codModelo) throws Throwable {
        final ModelosChecklistBloqueados modelosChecklistBloqueados
                = Injection.provideIntegracaoDao().getModelosChecklistBloqueados(codUnidade);
        return !modelosChecklistBloqueados.getCodModelosBloqueados().contains(codModelo);
    }

    private void enviaOsIntegrada(@NotNull final List<Long> codsInternoOsProlog) {
        Executors.newSingleThreadExecutor().execute(new IntegracaoOsTask(codsInternoOsProlog));
    }
}
