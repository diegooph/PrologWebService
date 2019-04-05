package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

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
    public ChecklistOfflineSupport getChecklistOfflineSupport(final String tokenSincronizacao,
                                                              final Long versaoDadosApp,
                                                              final Long codUnidade,
                                                              final boolean forcarAtualizacao) throws ProLogException {
        try {
            // Precisamos verificar o token para ter certeza se o usuário é apto a utilizar os métodos.
            ensureValidToken(tokenSincronizacao);

            final DadosChecklistOfflineUnidade dadosChecklistOffline =
                    getDadosChecklistOffline(versaoDadosApp, codUnidade, forcarAtualizacao);

            switch (dadosChecklistOffline.getEstadoChecklistOfflineSupport()) {
                case ATUALIZADO:
                    return new ChecklistOfflineSupportAtualizado(dadosChecklistOffline.getCodUnidade());
                case ATUALIZACAO_FORCADA:
                    //noinspection ConstantConditions
                    return new ChecklistOfflineSupportAtualizacaoForcada(
                            dadosChecklistOffline.getCodUnidade(),
                            dadosChecklistOffline.getTokenSincronizacaoChecklist(),
                            dadosChecklistOffline.getVersaoDadosBanco(),
                            dao.getModelosChecklistOffline(codUnidade),
                            dao.getColaboradoresChecklistOffline(codUnidade),
                            dao.getVeiculosChecklistOffline(codUnidade),
                            dao.getEmpresaChecklistOffline(codUnidade));
                case DESATUALIZADO:
                    //noinspection ConstantConditions
                    return new ChecklistOfflineSupportDesatualizado(
                            dadosChecklistOffline.getCodUnidade(),
                            dadosChecklistOffline.getTokenSincronizacaoChecklist(),
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
                    "AtualizacaoForcada: %b", codUnidade, versaoDadosApp, forcarAtualizacao), t);
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
    private DadosChecklistOfflineUnidade getDadosChecklistOffline(@NotNull final Long versaoDadosApp,
                                                                  @NotNull final Long codUnidade) throws Throwable {
        return getDadosChecklistOffline(versaoDadosApp, codUnidade, false);
    }

    @NotNull
    private DadosChecklistOfflineUnidade getDadosChecklistOffline(@NotNull final Long versaoDadosApp,
                                                                  @NotNull final Long codUnidade,
                                                                  final boolean forcarAtualizacao) throws Throwable {
        final Pair<Long, String> dadosAtuaisUnidade = dao.getDadosAtuaisUnidade(codUnidade);
        final Long versaoDadosBanco = dadosAtuaisUnidade.getKey();
        if (versaoDadosBanco != null) {
            // Caso a Unidade tenha dados e 'forcarAtualizacao = true' então forçamos a atualização dos dados
            // retornando a consante ATUALIZACAO_FORCADA.
            if (forcarAtualizacao) {
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        dadosAtuaisUnidade.getValue(),
                        EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
            }
            // Se a versão dos dados recebida na requisição é menor que a versão no banco, então retornamos a
            // constante DESATUALIZADO.
            if (versaoDadosApp < versaoDadosBanco) {
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        dadosAtuaisUnidade.getValue(),
                        EstadoChecklistOfflineSupport.DESATUALIZADO);
            }
            // Se a versão dos dados recebida na requisição é igual a versão no banco, então retornamos a
            // constante ATUALIZADO.
            if (versaoDadosApp.equals(versaoDadosBanco)) {
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        dadosAtuaisUnidade.getValue(),
                        EstadoChecklistOfflineSupport.ATUALIZADO);
            } else {
                // Esse caso só acontece na estranha situação que a versão dos dados da requisição é maior que a
                // versão dos dados do Servidor. Para este caso peculiar entendemos que algo está errado e forçamos a
                // atualização dos dados com a constante ATUALIZACAO_FORCADA.
                return new DadosChecklistOfflineUnidade(
                        codUnidade,
                        versaoDadosBanco,
                        dadosAtuaisUnidade.getValue(),
                        EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA);
            }
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
