package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class OpcaoProblemaService {
    private static final String TAG = OpcaoProblemaService.class.getSimpleName();
    @NotNull
    private final OpcaoProblemaDao dao = Injection.provideOpcaoProblemaDao();

    @NotNull
    public List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) {
        try {
            return dao.getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(codEmpresa);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as opções de problema disponíveis para abertura de socorro.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as opções de problema, tente novamente");
        }
    }

    @NotNull
    public List<OpcaoProblemaSocorroRotaListagem> getOpcoesProblemasSocorroRotaByEmpresa(@NotNull final Long codEmpresa) {
        try {
            return dao.getOpcoesProblemasSocorroRotaByEmpresa(codEmpresa);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as opções de problema.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as opções de problema, tente novamente");
        }
    }

    public OpcaoProblemaSocorroRotaVisualizacao getOpcaoProblemaSocorroRotaVisualizacao(
            @NotNull final Long codOpcaoProblema) {
        try {
            return dao.getOpcaoProblemaSocorroRotaVisualizacao(codOpcaoProblema);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar a opção de problema.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar a opção de problema, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod insertOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaCadastro opcaoProblemaSocorroRotaCadastro) {
        try {
            return ResponseWithCod.ok(
                    "Opção de problema inserida com sucesso",
                    dao.insertOpcoesProblemas(opcaoProblemaSocorroRotaCadastro));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir a opção de problema", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível inserir esta opção de problema, " +
                            "tente novamente");
        }
    }

    @NotNull
    Response updateOpcoesProblemas(
            @NotNull final OpcaoProblemaSocorroRotaEdicao opcaoProblemaSocorroRotaEdicao) {
        try {
            dao.updateOpcoesProblemas(opcaoProblemaSocorroRotaEdicao);
            return Response.ok("Opção de problema editada com sucesso!");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao editar a opção de problema", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível editar esta opção de problema, " +
                            "tente novamente");
        }
    }

    Response updateStatusOpcoesProblemas
            (@NotNull final OpcaoProblemaSocorroRotaStatus opcaoProblemaSocorroRotaStatus) {
        try {
            dao.updateStatusAtivo(opcaoProblemaSocorroRotaStatus);
            return Response.ok("Opção de problema " + (opcaoProblemaSocorroRotaStatus.isStatusAtivo() ? "ativado" : "inativado") + " com sucesso");
        } catch (Throwable t) {
            Log.e(TAG, "Erro ao ativar/inativar a opcao de problema " + opcaoProblemaSocorroRotaStatus.getCodOpcaoProblema(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao ativar/inativar a opcao de problema, tente novamente");
        }
    }
}