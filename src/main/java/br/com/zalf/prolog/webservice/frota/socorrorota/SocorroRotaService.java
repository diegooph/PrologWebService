package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.messaging.send.FirebasePushMessageSender;
import br.com.zalf.prolog.webservice.messaging.send.PushMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    ResponseWithCod aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) {
        try {
            final Long codSocorro = dao.aberturaSocorro(socorroRotaAbertura);

            final List<ColaboradorNotificacaoAberturaSocorro> colaboradores = dao.getColaboradoresNotificacaoAbertura(
                    socorroRotaAbertura.getCodUnidade());

            if (!colaboradores.isEmpty()) {
                // Envia notificação via firebase
                new FirebasePushMessageSender().deliver(
                        new ArrayList<>(colaboradores),
                        new PushMessage("ATENÇÃO!", "Um socorro em rota foi solicitado na sua unidade"));
            } else {
                Log.d(TAG, "Nenhum token para notificar sobre abertura do socorro");
            }

            return ResponseWithCod.ok("Solicitação de socorro aberta com sucesso", codSocorro);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao abrir uma solitação de socorro", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a abertura desta solicitação de socorro, " +
                            "tente novamente");
        }
    }

    @NotNull
    public List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(@NotNull final Long codColaborador) {
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
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(@NotNull final Long codUnidade) {
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
    public List<SocorroRotaListagem> getListagemSocorroRota(@NotNull final List<Long> codUnidades,
                                                            @NotNull final String dataInicial,
                                                            @NotNull final String dataFinal,
                                                            @NotNull final String userToken) {
        try {
            return dao.getListagemSocorroRota(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal),
                    userToken);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar a lista de socorros em rota.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar a lista de socorros em rota, tente novamente.");
        }
    }

    @NotNull
    ResponseWithCod invalidacaoSocorro(@NotNull final SocorroRotaInvalidacao socorroRotaInvalidacao) {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro invalidada com sucesso.",
                    dao.invalidacaoSocorro(socorroRotaInvalidacao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao invalidar uma solitação de socorro.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a invalidação desta solicitação de socorro, " +
                            "tente novamente.");
        }
    }

    @NotNull
    ResponseWithCod atendimentoSocorro(@NotNull final SocorroRotaAtendimento socorroRotaAtendimento) {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro atendida com sucesso.",
                    dao.atendimentoSocorro(socorroRotaAtendimento));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atender uma solitação de socorro.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar o atendimento desta solicitação de socorro, " +
                            "tente novamente.");
        }
    }


    @NotNull
    ResponseWithCod finalizacaoSocorro(@NotNull final SocorroRotaFinalizacao socorroRotaFinalizacao) {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro finalizada com sucesso.",
                    dao.finalizacaoSocorro(socorroRotaFinalizacao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao finalizar uma solitação de socorro.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a finalização desta solicitação de socorro, " +
                            "tente novamente.");
        }
    }

    @NotNull
    public SocorroRotaVisualizacao getVisualizacaoSocorroRota(@NotNull final Long codSocorroRota) {
        try {
            return dao.getVisualizacaoSocorroRota(codSocorroRota);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as informações deste socorro em rota.", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as informações deste socorro em rota, tente novamente.");
        }
    }
}