package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoAberturaSocorro;
import br.com.zalf.prolog.webservice.messaging.AndroidAppScreens;
import br.com.zalf.prolog.webservice.messaging.AndroidLargeIcon;
import br.com.zalf.prolog.webservice.messaging.AndroidSmallIcon;
import br.com.zalf.prolog.webservice.messaging.send.FirebasePushMessageApi;
import br.com.zalf.prolog.webservice.messaging.send.PushMessage;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created on 2020-01-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class NotificadorAberturaSocorro {
    private static final String TAG = NotificadorAberturaSocorro.class.getSimpleName();
    @NotNull
    private final ListeningExecutorService service;

    NotificadorAberturaSocorro() {
        service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    }

    @SuppressWarnings("UnstableApiUsage")
    void notificarColaboradores(@NotNull final SocorroRotaDao socorroRotaDao,
                                @NotNull final Long codUnidadeSocorro,
                                @NotNull final Long codSocorro) {
        Log.d(TAG, "notificarColaboradores(...) on thread: " + Thread.currentThread().getName());
        try {
            // Se estourar algo nessa chamada, não será logado no BD a tentativa de envio de mensagem. Além desse caso,
            // também não será logado no BD se acontecer um erro na busca dos colaboradores para notificar.
            final ListenableFuture<Void> future = Futures.submit(
                    () -> innerRun(socorroRotaDao, codUnidadeSocorro, codSocorro),
                    service);
            Futures.addCallback(
                    future,
                    new FutureCallback<Void>() {
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
                    },
                    service);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notificações do socorro em rota", t);
            shutdowService();
        }
    }

    private void shutdowService() {
        Exceptions.swallowAny(service::shutdown);
    }

    private void innerRun(@NotNull final SocorroRotaDao socorroDao,
                          @NotNull final Long codUnidadeSocorro,
                          @NotNull final Long codSocorro) {
        Log.d(TAG, "innerRun(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoAberturaSocorro> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoAbertura(codUnidadeSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException("Erro ao buscar colaboradores para notificação de abertura de socorro em rota\n" +
                    "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notificação via firebase
            new FirebasePushMessageApi().deliver(
                    new ArrayList<>(colaboradores),
                    PushMessage.builder()
                            .withTitle("ATENÇÃO! Pedido de Socorro!")
                            .withBody("Clique para visualizar as informações")
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.SOS_NOTIFICATION)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum token para notificar sobre abertura do socorro");
        }
    }
}
