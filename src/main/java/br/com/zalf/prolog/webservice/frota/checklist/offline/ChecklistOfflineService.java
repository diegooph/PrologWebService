package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.InfosChecklistInserido;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklistOffline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.NotAuthorizedException;
import java.util.Optional;

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
                                                  final ChecklistInsercao checklist) throws ProLogException {
        try {
            // Precisamos verificar o token para ter certeza se o usuário é apto a utilizar os métodos.
            ensureValidToken(tokenSincronizacao);
            // Buscamos um token para o usuário que realizou o checklist sendo sincronizado
            final AutenticacaoResponse autenticacaoResponse =
                    Injection.provideAutenticacaoDao().createTokenByCodColaborador(checklist.getCodColaborador());
            final InfosChecklistInserido infosChecklistInserido =
                    RouterChecklistOffline.create(dao, autenticacaoResponse.getToken()).insertChecklistOffline(checklist);
            return ResponseWithCod.ok(
                    "Checklist inserido com sucesso",
                    infosChecklistInserido.getCodChecklist());
        } catch (final Throwable t) {
            Log.e(TAG, String.format(
                    "Não foi possível inserir o checklist offline:\n" +
                            "tokenSincronizacao = %s",
                    tokenSincronizacao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível inserir o checklist, tente novamente");
        }
    }

    @NotNull
    public ChecklistOfflineSupport getChecklistOfflineSupport(final Long versaoDadosChecklsitApp,
                                                              final Long codUnidade,
                                                              final boolean forcarAtualizacao) throws ProLogException {
        try {
            final Optional<TokenVersaoChecklist> optionalDados = dao.getDadosAtuaisUnidade(codUnidade);
            if (optionalDados.isPresent()) {
                final Long versaoDadosBanco = optionalDados.get().getVersaoDados();
                final String tokenBanco = optionalDados.get().getTokenSincronizacao();

                final EstadoChecklistOfflineSupport estadoChecklistOfflineSupport =
                        getEstadoChecklistOffline(versaoDadosBanco, versaoDadosChecklsitApp);

                // Caso a Unidade tenha dados e 'forcarAtualizacao = true' os dados serão retornados independente do
                // estado atual deles, mesmo sendo atualizado.
                if (forcarAtualizacao
                        || estadoChecklistOfflineSupport.equals(EstadoChecklistOfflineSupport.DESATUALIZADO)) {
                    return new ChecklistOfflineSupportComDados(
                            codUnidade,
                            estadoChecklistOfflineSupport,
                            new ChecklistOfflineData(
                                    tokenBanco,
                                    versaoDadosBanco,
                                    dao.getModelosChecklistOffline(codUnidade),
                                    dao.getColaboradoresChecklistOffline(codUnidade),
                                    dao.getVeiculosChecklistOffline(codUnidade),
                                    dao.getUnidadeChecklistOffline(codUnidade)),
                            forcarAtualizacao);
                } else {
                    return new ChecklistOfflineSupportSemDados(
                            codUnidade,
                            EstadoChecklistOfflineSupport.ATUALIZADO,
                            false);
                }
            } else {
                return new ChecklistOfflineSupportSemDados(
                        codUnidade,
                        EstadoChecklistOfflineSupport.SEM_DADOS,
                        forcarAtualizacao);
            }
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar informações para realização de checklist offline: \n" +
                    "CodUnidade: %d\n" +
                    "VersaoDados: %d\n" +
                    "AtualizacaoForcada: %b", codUnidade, versaoDadosChecklsitApp, forcarAtualizacao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar informações do checklist offline, tente novamente");
        }
    }

    public boolean getChecklistOfflineAtivoEmpresa(final Long codEmpresa) throws ProLogException {
        try {
            return dao.getChecklistOfflineAtivoEmpresa(codEmpresa);
        } catch (final Throwable t) {
            final String msg = "Erro ao verificar se empresa está liberada para realizar checklist offline";
            Log.e(TAG, msg, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, msg);
        }
    }

    @NotNull
    private EstadoChecklistOfflineSupport getEstadoChecklistOffline(@NotNull final Long versaoDadosBanco,
                                                                    @Nullable final Long versaoDadosChecklsitApp) {
        if (versaoDadosChecklsitApp == null) {
            return EstadoChecklistOfflineSupport.DESATUALIZADO;
        }
        if (versaoDadosChecklsitApp.equals(versaoDadosBanco)) {
            return EstadoChecklistOfflineSupport.ATUALIZADO;
        }
        return EstadoChecklistOfflineSupport.DESATUALIZADO;
    }

    @NotNull
    private DadosChecklistOfflineUnidade getDadosChecklistOffline(@NotNull final Long versaoDadosChecklsitApp,
                                                                  @NotNull final Long codUnidade) throws Throwable {
        final Optional<TokenVersaoChecklist> optionalDados = dao.getDadosAtuaisUnidade(codUnidade);
        if (optionalDados.isPresent()) {
            final Long versaoDadosBanco = optionalDados.get().getVersaoDados();
            final String tokenBanco = optionalDados.get().getTokenSincronizacao();
            // Se a versão dos dados recebida na requisição é igual a versão no banco, então retornamos a
            // constante ATUALIZADO.
            if (versaoDadosChecklsitApp.equals(versaoDadosBanco)) {
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        tokenBanco,
                        EstadoChecklistOfflineSupport.ATUALIZADO);
            }

            return new DadosChecklistOfflineUnidade(
                    codUnidade,
                    versaoDadosBanco,
                    tokenBanco,
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
}
