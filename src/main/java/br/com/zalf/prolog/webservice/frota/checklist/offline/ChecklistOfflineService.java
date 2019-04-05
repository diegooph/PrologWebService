package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.NotAuthorizedException;

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
    public ResponseWithCod insertChecklistOffline(final String tokenSincronizacao,
                                                  final long versaoDadosChecklsitApp,
                                                  final ChecklistInsercao checklist) throws ProLogException {
        try {
            // Precisamos verificar o token para ter certeza se o usuário é apto a utilizar os métodos.
            ensureValidToken(tokenSincronizacao);

            if (checklist == null || checklist.getCodUnidade() == null) {
                throw new IllegalStateException("Informações nulas providas para o checklist");
            }
            final DadosChecklistOfflineUnidade dadosChecklistOffline =
                    getDadosChecklistOffline(versaoDadosChecklsitApp, checklist.getCodUnidade());

            if (dadosChecklistOffline.getTokenSincronizacaoChecklist() == null
                    || !dadosChecklistOffline.getTokenSincronizacaoChecklist().equals(tokenSincronizacao)) {
                throw new IllegalArgumentException(
                        "Token inválido para sincronização de checklist: " + tokenSincronizacao);
            }
            return ResponseWithCod.ok(
                    "Checklist inserido com sucesso",
                    dao.insertChecklistOffline(checklist));
        } catch (Throwable t) {
            Log.e(TAG, String.format(
                    "Não foi possível inserir o checklist:\n" +
                            "tokenSincronizacao = %s\n" +
                            "versaoDadosChecklsitApp = %d",
                    tokenSincronizacao,
                    versaoDadosChecklsitApp), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível inserir o checklist, tente novamente");
        }
    }

    @NotNull
    public ChecklistOfflineSupport getChecklistOfflineSupport(final long versaoDadosChecklsitApp,
                                                              final Long codUnidade,
                                                              final boolean forcarAtualizacao) throws ProLogException {
        try {
            final Pair<Long, String> dadosAtuaisUnidade = dao.getDadosAtuaisUnidade(codUnidade);
            final Long versaoDadosBanco = dadosAtuaisUnidade.getKey();
            final String tokenWs = dadosAtuaisUnidade.getValue();
            final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport =
                    getEstadoChecklistOffline(versaoDadosBanco, versaoDadosChecklsitApp);

            if (versaoDadosBanco != null && tokenWs != null) {
                // Caso a Unidade tenha dados e 'forcarAtualizacao = true' então forçamos a atualização dos dados
                // retornando a consante ATUALIZACAO_FORCADA.
                if (forcarAtualizacao
                        || estadoChecklistOfflineSupport.equals(EstadoChecklistOfflineSupport.DESATUALIZADO)) {
                    return new ChecklistOfflineSupportComDados(
                            codUnidade,
                            estadoChecklistOfflineSupport,
                            new ChecklistOfflineData(
                                    tokenWs,
                                    versaoDadosBanco,
                                    dao.getModelosChecklistOffline(codUnidade),
                                    dao.getColaboradoresChecklistOffline(codUnidade),
                                    dao.getVeiculosChecklistOffline(codUnidade),
                                    dao.getEmpresaChecklistOffline(codUnidade)),
                            forcarAtualizacao);
                }
            }
            return new ChecklistOfflineSupportSemDados(
                    codUnidade,
                    estadoChecklistOfflineSupport,
                    forcarAtualizacao);

        } catch (Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar informações para realização de checklist offline: \n" +
                    "CodUnidade: %d\n" +
                    "VersaoDados: %d\n" +
                    "AtualizacaoForcada: %b", codUnidade, versaoDadosChecklsitApp, forcarAtualizacao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar informações do checklist offline, tente novamente");
        }
    }

    @NotNull
    DadosChecklistOfflineUnidade getEstadoDadosChecklistOffline(final String tokenSincronizacao,
                                                                final Long versaoDadosApp,
                                                                final Long codUnidade) throws ProLogException {
        try {
            // Precisamos verificar o token para ter certeza se o usuário é apto a utilizar os métodos.
            ensureValidToken(tokenSincronizacao);

            return getDadosChecklistOffline(versaoDadosApp, codUnidade);
        } catch (final Throwable t) {
            final String msg = String.format(
                    "Erro ao buscar estado dos dados do checklist offline para a unidade %d",
                    codUnidade);
            Log.e(TAG, msg, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar estado dos dados do checklist offline");
        }
    }

    public boolean getChecklistOfflineAtivoEmpresa(final Long cpfColaborador) throws ProLogException {
        try {
            return dao.getChecklistOfflineAtivoEmpresa(cpfColaborador);
        } catch (Throwable t) {
            final String msg =
                    "Erro ao busca informação se empresa do colaborador está liberada para realizar checklist offline";
            Log.e(TAG, msg, t);
            throw Injection.provideProLogExceptionHandler().map(t, msg);
        }
    }

    @NotNull
    private EstadoChecklistOfflineSupport getEstadoChecklistOffline(@Nullable final Long versaoDadosBanco,
                                                                    @NotNull final Long versaoDadosChecklsitApp) {
        if (versaoDadosBanco == null) {
            return EstadoChecklistOfflineSupport.SEM_DADOS;
        }
        if (versaoDadosChecklsitApp.equals(versaoDadosBanco)) {
            return EstadoChecklistOfflineSupport.ATUALIZADO;
        }
        return EstadoChecklistOfflineSupport.DESATUALIZADO;
    }

    @NotNull
    private DadosChecklistOfflineUnidade getDadosChecklistOffline(@NotNull final Long versaoDadosApp,
                                                                  @NotNull final Long codUnidade) throws Throwable {
        final Pair<Long, String> dadosAtuaisUnidade = dao.getDadosAtuaisUnidade(codUnidade);
        final Long versaoDadosBanco = dadosAtuaisUnidade.getKey();
        final String tokenBanco = dadosAtuaisUnidade.getValue();
        if (versaoDadosBanco != null && tokenBanco != null) {
            // Se a versão dos dados recebida na requisição é igual a versão no banco, então retornamos a
            // constante ATUALIZADO.
            if (versaoDadosApp.equals(versaoDadosBanco)) {
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        dadosAtuaisUnidade.getValue(),
                        EstadoChecklistOfflineSupport.ATUALIZADO);
            }

            return new DadosChecklistOfflineUnidade(
                    codUnidade,
                    versaoDadosBanco,
                    dadosAtuaisUnidade.getValue(),
                    EstadoChecklistOfflineSupport.DESATUALIZADO);

        } else {
            // Se não tem versão no BD, então a Unidade não tem dados.
            return new DadosChecklistOfflineUnidade(codUnidade, EstadoChecklistOfflineSupport.SEM_DADOS);
        }
    }

    private void ensureValidToken(@NotNull final String tokenSincronizacao) throws ProLogException {
        try {
            if (!dao.verifyIfTokenChecklistExists(tokenSincronizacao)) {
                throw new NotAuthorizedException("Token não existe no banco de dados: " + tokenSincronizacao);
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao verificar se o tokenSincronizacao existe: %s", tokenSincronizacao), t);
            if (t instanceof NotAuthorizedException) {
                throw (NotAuthorizedException) t;
            }
            throw Injection.provideProLogExceptionHandler().map(t, "Erro ao verificar token");
        }
    }
}
