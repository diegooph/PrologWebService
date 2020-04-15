package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoSocorroRota;
import br.com.zalf.prolog.webservice.messaging.AndroidAppScreens;
import br.com.zalf.prolog.webservice.messaging.AndroidLargeIcon;
import br.com.zalf.prolog.webservice.messaging.AndroidSmallIcon;
import br.com.zalf.prolog.webservice.messaging.PushMessageScope;
import br.com.zalf.prolog.webservice.messaging.send.FirebasePushMessageApi;
import br.com.zalf.prolog.webservice.messaging.send.PushMessage;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.removeExtraSpaces;
import static br.com.zalf.prolog.webservice.commons.util.StringUtils.stripSpecialCharacters;

/**
 * Created on 2020-01-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class NotificadorSocorroRota {
    private static final String TAG = NotificadorSocorroRota.class.getSimpleName();
    private static final int MAX_LENGTH_NOME_COLABORADOR = 20;
    @NotNull
    private final ListeningExecutorService service;
    @NotNull
    private final FutureCallback<Void> shutdownServiceCallback = new FutureCallback<Void>() {
        @Override
        public void onSuccess(@Nullable final Void result) {
            Log.d(TAG, "Processo de notificação com Firebase finalizado com sucesso!");
            shutdowService();
        }

        @Override
        public void onFailure(@NotNull final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notificações do socorro em rota", t);
            shutdowService();
        }
    };

    NotificadorSocorroRota() {
        service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    }

    @SuppressWarnings("UnstableApiUsage")
    void notificaSobreAbertura(@NotNull final SocorroRotaDao socorroRotaDao,
                               @NotNull final Long codUnidadeSocorro,
                               @NotNull final String nomeColaboradorAbertura,
                               @NotNull final String placaVeiculoProblema,
                               @NotNull final Long codSocorro) {
        Log.d(TAG, "notificaSobreAbertura(...) on thread: " + Thread.currentThread().getName());
        try {
            // Se estourar algo nessa chamada, não será logado no BD a tentativa de envio de mensagem. Além desse caso,
            // também não será logado no BD se acontecer um erro na busca dos colaboradores para notificar.
            final ListenableFuture<Void> future = Futures.submit(
                    () -> internalNotificaSobreAbertura(
                            socorroRotaDao,
                            codUnidadeSocorro,
                            nomeColaboradorAbertura,
                            placaVeiculoProblema,
                            codSocorro),
                    service);
            Futures.addCallback(
                    future,
                    shutdownServiceCallback,
                    service);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notificações sobre abertura de socorro", t);
            shutdowService();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    void notificaSobreAtendimento(@NotNull final SocorroRotaDao socorroDao,
                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "notificaSobreAtendimento(...) on thread: " + Thread.currentThread().getName());
        try {
            final ListenableFuture<Void> future = Futures.submit(
                    () -> internalNotificaSobreAtendimento(socorroDao, codSocorro),
                    service);
            Futures.addCallback(
                    future,
                    shutdownServiceCallback,
                    service);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notificações sobre atendimento de socorro", t);
            shutdowService();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    void notificaSobreInvalidacao(@NotNull final SocorroRotaDao socorroDao,
                                  @NotNull final Long codColaboradorInvalidacaoSocorro,
                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "notificaSobreInvalidacao(...) on thread: " + Thread.currentThread().getName());
        try {
            final ListenableFuture<Void> future = Futures.submit(
                    () -> internalNotificaSobreInvalidacao(socorroDao, codColaboradorInvalidacaoSocorro, codSocorro),
                    service);
            Futures.addCallback(
                    future,
                    shutdownServiceCallback,
                    service);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notificações sobre invalidação de socorro", t);
            shutdowService();
        }
    }

    private void internalNotificaSobreAbertura(@NotNull final SocorroRotaDao socorroDao,
                                               @NotNull final Long codUnidadeSocorro,
                                               @NotNull final String nomeColaboradorAbertura,
                                               @NotNull final String placaVeiculoProblema,
                                               @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreAbertura(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoSocorroRota> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoAbertura(codUnidadeSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException(
                    "Erro ao buscar colaboradores para notificação de abertura de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notificação via firebase.
            final String messageBody = String.format(
                    "%s solicitou um socorro para o veículo %s. Clique para ver mais.",
                    formataNomeColaborador(nomeColaboradorAbertura),
                    placaVeiculoProblema.trim());
            new FirebasePushMessageApi().deliver(
                    new ArrayList<>(colaboradores),
                    PushMessageScope.ABERTURA_SOCORRO_ROTA,
                    PushMessage.builder()
                            .withTitle("ATENÇÃO! Pedido de Socorro!")
                            .withBody(messageBody)
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.SOS_NOTIFICATION)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum token para notificar sobre abertura do socorro");
        }
    }

    private void internalNotificaSobreAtendimento(@NotNull final SocorroRotaDao socorroDao,
                                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreAtendimento(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoSocorroRota> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoAtendimento(codSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException(
                    "Erro ao buscar colaboradores para notificação de atendimento de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notificação via firebase.
            final String messageBody =
                    "Seu pedido de socorro foi atendido. A ajuda está a caminho. Clique para ver mais.";
            new FirebasePushMessageApi().deliver(
                    new ArrayList<>(colaboradores),
                    PushMessageScope.ATENDIMENTO_SOCORRO_ROTA,
                    PushMessage.builder()
                            .withTitle("Socorro a caminho")
                            .withBody(messageBody)
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.HELPING_HAND)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum token para notificar sobre atendimento do socorro");
        }
    }

    private void internalNotificaSobreInvalidacao(@NotNull final SocorroRotaDao socorroDao,
                                                  @NotNull final Long codColaboradorInvalidacaoSocorro,
                                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreInvalidacao(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoSocorroRota> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoInvalidacao(
                    codColaboradorInvalidacaoSocorro,
                    codSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException(
                    "Erro ao buscar colaboradores para notificação de invalidação de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notificação via firebase.
            final String messageBody =
                    "Seu pedido de socorro foi invalidado. Algo está errado. Clique para ver mais.";
            new FirebasePushMessageApi().deliver(
                    new ArrayList<>(colaboradores),
                    PushMessageScope.INVALIDACAO_SOCORRO_ROTA,
                    PushMessage.builder()
                            .withTitle("Socorro invalidado")
                            .withBody(messageBody)
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.ERROR_X)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum token para notificar sobre invalidação do socorro");
        }
    }

    @NotNull
    private String formataNomeColaborador(@NotNull final String nomeColaboradorAbertura) {
        final String[] words = stripSpecialCharacters(
                removeExtraSpaces(nomeColaboradorAbertura.trim()))
                // Split it on any whitespace character (space, tab, newline, cr).
                .split("\\s");

        // Transforma em uppercase a primeira letra de cada palavra.
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }

        return limitaNomeLength(String.join(" ", words));
    }

    @NotNull
    private String limitaNomeLength(@NotNull final String nomeColaboradorAbertura) {
        if (nomeColaboradorAbertura.length() > MAX_LENGTH_NOME_COLABORADOR) {
            return nomeColaboradorAbertura.substring(0, MAX_LENGTH_NOME_COLABORADOR - 1).trim().concat(".");
        } else {
            return nomeColaboradorAbertura;
        }
    }

    private void shutdowService() {
        Exceptions.swallowAny(service::shutdown);
    }
}
