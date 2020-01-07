package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroRotaService {
    private static final String TAG = SocorroRotaService.class.getSimpleName();
    @NotNull
    private final SocorroRotaDao dao = Injection.provideSocorroDao();

    @NotNull
    ResponseWithCod aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro aberta com sucesso",
                    dao.aberturaSocorro(socorroRotaAbertura));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao abrir uma solitação de socorro", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a abertura desta solicitação de socorro, " +
                            "tente novamente");
        }
    }

    @NotNull
    public List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @NotNull final Long codColaborador) throws ProLogException {
        try {
            return dao.getUnidadesDisponiveisAberturaSocorroByCodColaborador(codColaborador);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as unidades disponíveis para abertura de socorro.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as unidades, tente novamente");
        }
    }

    @NotNull
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(
            @NotNull final Long codUnidade) throws ProLogException {
        try {
            return dao.getVeiculosDisponiveisAberturaSocorroByUnidade(codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar os veículos disponíveis para abertura de socorro.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar os veículos, tente novamente");
        }
    }

    @NotNull
    public List<OpcaoProblemaAberturaSocorro> getOpcoesProblemasDisponiveisAberturaSocorroByEmpresa(
            @NotNull final Long codEmpresa) throws ProLogException {
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
    public List<SocorroRotaListagem> getListagemSocorroRota(
            @NotNull final List<Long> codUnidades,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getListagemSocorroRota(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar a lista de socorros em rota.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar a lista de socorros em rota, tente novamente.");
        }
    }

    @NotNull
    ResponseWithCod invalidacaoSocorro(@NotNull final SocorroRotaInvalidacao socorroRotaInvalidacao) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro invalidada com sucesso.",
                    dao.invalidacaoSocorro(socorroRotaInvalidacao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao invalidar uma solitação de socorro.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a abertura desta solicitação de socorro, " +
                            "tente novamente.");
        }
    }
}