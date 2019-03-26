package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineService {
    @NotNull
    private static final String TAG = ChecklistOfflineService.class.getSimpleName();
    @NotNull
    private final ChecklistOfflineDao dao = Injection.provideChecklistOfflineDao();

    @NotNull
    public ResponseChecklist insertChecklistOffline(final String tokenSincronizacao,
                                                    final long versaoDadosChecklsitApp,
                                                    final long versaoAppMomentoSincronizacao,
                                                    final ChecklistInsercao checklist) throws ProLogException {
        try {
            if (checklist == null || checklist.getCodUnidade() == null) {
                throw new IllegalStateException("Informações nulas providas para o checklist");
            }
            final DadosChecklistOfflineUnidade dadosChecklistOffline =
                    getDadosChecklistOffline(versaoDadosChecklsitApp, checklist.getCodUnidade(), false);

            if (dadosChecklistOffline.getTokenSincronizacaoMarcacao() == null
                    || !dadosChecklistOffline.getTokenSincronizacaoMarcacao().equals(tokenSincronizacao)) {
                throw new IllegalArgumentException(
                        "Token inválido para sincronização de checklist: " + tokenSincronizacao);
            }
            if (dadosChecklistOffline.getEstadoChecklistOfflineSupport() == null) {
                throw new IllegalArgumentException("Um estado deve ser fornecido para os dados do checklist offline");
            }
            return ResponseChecklist.ok(
                    dao.insertChecklistOffline(
                            tokenSincronizacao,
                            versaoAppMomentoSincronizacao,
                            checklist),
                    "Checklist inserido com sucesso",
                    dadosChecklistOffline.getEstadoChecklistOfflineSupport());
        } catch (Throwable t) {
            Log.e(TAG, "");
            throw Injection.provideProLogExceptionHandler().map(t, "");
        }
    }

    public boolean getChecklistOfflineAtivoEmpresa(final Long cpfColaborador) throws ProLogException {
        try {
            return dao.getChecklistOfflineAtivoEmpresa(cpfColaborador);
        } catch (Throwable t) {
            final String msg =
                    "Erro ao busca informação se empresa do colaborador está liberada para realizar checklist offline";
            Log.e(TAG, msg);
            throw Injection.provideProLogExceptionHandler().map(t, msg);
        }
    }

    @NotNull
    public ChecklistOfflineSupport getChecklistOfflineSupport(final Long versaoDadosApp,
                                                              final Long codUnidade,
                                                              final boolean forcarAtualizacao) throws ProLogException {
        try {
            final DadosChecklistOfflineUnidade dadosChecklistOffline =
                    getDadosChecklistOffline(versaoDadosApp, codUnidade, forcarAtualizacao);

            if (dadosChecklistOffline.getEstadoChecklistOfflineSupport() == null) {
                throw new IllegalStateException("Um estado deve ser fornecido para os dados do checklist offline");
            }

            switch (dadosChecklistOffline.getEstadoChecklistOfflineSupport()) {
                case ATUALIZADO:
                    return new ChecklistOfflineSupportAtualizado(dadosChecklistOffline.getCodUnidade());
                case ATUALIZACAO_FORCADA:
                    //noinspection ConstantConditions
                    return new ChecklistOfflineSupportAtualizacaoForcada(
                            dadosChecklistOffline.getCodUnidade(),
                            dadosChecklistOffline.getTokenSincronizacaoMarcacao(),
                            dadosChecklistOffline.getVersaoDadosBanco(),
                            dao.getModelosChecklistOffline(codUnidade),
                            dao.getColaboradoresChecklistOffline(codUnidade),
                            dao.getVeiculosChecklistOffline(codUnidade),
                            dao.getEmpresaChecklistOffline(codUnidade));
                case DESATUALIZADO:
                    //noinspection ConstantConditions
                    return new ChecklistOfflineSupportDesatualizado(
                            dadosChecklistOffline.getCodUnidade(),
                            dadosChecklistOffline.getTokenSincronizacaoMarcacao(),
                            dadosChecklistOffline.getVersaoDadosBanco(),
                            dao.getModelosChecklistOffline(codUnidade),
                            dao.getColaboradoresChecklistOffline(codUnidade),
                            dao.getVeiculosChecklistOffline(codUnidade),
                            dao.getEmpresaChecklistOffline(codUnidade));
                case SEM_DADOS:
                    return new ChecklistOfflineSupportSemDados(dadosChecklistOffline.getCodUnidade());
                default:
                    throw new IllegalStateException("Um estado não mapeado foi informado: "
                            + dadosChecklistOffline.getEstadoChecklistOfflineSupport());
            }
        } catch (Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar informações para realização de checklist offline: \n" +
                    "CodUnidade: %d\n" +
                    "VersaoDados: %d\n" +
                    "AtualizacaoForcada: %b", codUnidade, versaoDadosApp, forcarAtualizacao));
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar informações do checklist offline, tente novamente");
        }
    }

    @NotNull
    private DadosChecklistOfflineUnidade getDadosChecklistOffline(@NotNull final Long versaoDadosApp,
                                                                  @NotNull final Long codUnidade,
                                                                  final boolean forcarAtualizacao) throws Throwable {
        final DadosChecklistOfflineUnidade dadosChecklistOfflineUnidade = dao.getVersaoDadosAtual(codUnidade);
        final Long versaoDadosBanco = dadosChecklistOfflineUnidade.getVersaoDadosBanco();
        if (versaoDadosBanco != null) {
            // Caso a Unidade tenha dados e 'forcarAtualizacao = true' então forçamos a atualização dos dados
            // retornando a consante ATUALIZACAO_FORCADA.
            if (forcarAtualizacao) {
                dadosChecklistOfflineUnidade
                        .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
                return dadosChecklistOfflineUnidade;
            }
            // Se a versão dos dados recebida na requisição é menor que a versão no banco, então retornamos a
            // constante DESATUALIZADO.
            if (versaoDadosApp < versaoDadosBanco) {
                dadosChecklistOfflineUnidade
                        .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.DESATUALIZADO);
                return dadosChecklistOfflineUnidade;
            }
            // Se a versão dos dados recebida na requisição é igual a versão no banco, então retornamos a
            // constante ATUALIZADO.
            if (versaoDadosApp.equals(versaoDadosBanco)) {
                dadosChecklistOfflineUnidade
                        .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZADO);
                return dadosChecklistOfflineUnidade;
            } else {
                // Esse caso só acontece na estranha situação que a versão dos dados da requisição é maior que a
                // versão dos dados do Servidor. Para este caso peculiar entendemos que algo está errado e forçamos a
                // atualização dos dados com a constante ATUALIZACAO_FORCADA.
                dadosChecklistOfflineUnidade
                        .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
                return dadosChecklistOfflineUnidade;
            }
        } else {
            // Se não tem versão no BD, então a Unidade não tem dados.
            dadosChecklistOfflineUnidade
                    .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.SEM_DADOS);
            return dadosChecklistOfflineUnidade;
        }
    }
}
