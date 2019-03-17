package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
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
                    throw new IllegalStateException("Um estado não mapeado foi informado ");
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
        if (forcarAtualizacao) {
            dadosChecklistOfflineUnidade
                    .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
            return dadosChecklistOfflineUnidade;
        } else {
            final Long versaoDadosBanco = dadosChecklistOfflineUnidade.getVersaoDadosBanco();
            if (versaoDadosBanco != null) {
                if (versaoDadosApp < versaoDadosBanco) {
                    dadosChecklistOfflineUnidade
                            .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.DESATUALIZADO);
                    return dadosChecklistOfflineUnidade;
                } else if (versaoDadosApp.equals(versaoDadosBanco)) {
                    dadosChecklistOfflineUnidade
                            .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZADO);
                    return dadosChecklistOfflineUnidade;
                } else {
                    dadosChecklistOfflineUnidade
                            .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
                    return dadosChecklistOfflineUnidade;
                }
            } else {
                dadosChecklistOfflineUnidade
                        .setEstadoChecklistOfflineSupport(EstadoChecklistOfflineSupport.SEM_DADOS);
                return dadosChecklistOfflineUnidade;
            }
        }
    }
}
