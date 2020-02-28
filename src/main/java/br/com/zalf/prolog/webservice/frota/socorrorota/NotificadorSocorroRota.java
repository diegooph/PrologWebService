package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoAberturaSocorroRota;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoAtendimentoSocorroRota;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email.PrologEmailApi;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailSender;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplate;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import br.com.zalf.prolog.webservice.messaging.push._model.*;
import br.com.zalf.prolog.webservice.messaging.push.send.FirebasePushMessageApi;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @NotNull
    private final ListeningExecutorService service;

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

    private void internalNotificaSobreAbertura(@NotNull final SocorroRotaDao socorroDao,
                                               @NotNull final Long codUnidadeSocorro,
                                               @NotNull final String nomeColaboradorAbertura,
                                               @NotNull final String placaVeiculoProblema,
                                               @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreAbertura(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoAberturaSocorroRota> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoAbertura(codUnidadeSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException(
                    "Erro ao buscar colaboradores para notificação de abertura de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia e-mail.
            final List<String> emails = colaboradores
                    .stream()
                    .map(ColaboradorNotificacaoAberturaSocorroRota::getEmailColaborador)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!emails.isEmpty()) {
                new PrologEmailApi()
                        .deliverTemplate(
                                emails,
                                MessageScope.ABERTURA_SOCORRO_ROTA,
                                new EmailTemplateMessage(
                                        EmailTemplate.ABERTURA_SOCORRO_ROTA,
                                        EmailSender.of("socorroemrota@prologapp.com", "Socorro em Rota"),
                                        String.format("Pedido de Socorro • %s", placaVeiculoProblema),
                                        new ImmutableMap.Builder<String, String>()
                                                .put("nome_colaborador", nomeColaboradorAbertura)
                                                .put("placa_veiculo", placaVeiculoProblema)
                                                .put("link_socorro_rota", "https://adm.prologapp.com/socorro-em-rota")
                                                .build()));
            } else {
                Log.d(TAG, "Nenhum colaborador para notificar VIA E-MAIL sobre abertura do socorro");
            }

            final List<SimplePushDestination> destinations = colaboradores
                    .stream()
                    .filter(c -> c.getTokensPushFirebase() != null)
                    .map(c ->
                            Arrays.stream(c.getTokensPushFirebase())
                                    .map(token -> new SimplePushDestination(String.valueOf(c.getCodColaborador()), token))
                                    .collect(Collectors.toList()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (!destinations.isEmpty()) {
                // Envia notificação via firebase.
                final String messageBody = String.format(
                        "%s solicitou um socorro para o veículo %s. Clique para ver mais.",
                        formataNomeColaborador(nomeColaboradorAbertura),
                        placaVeiculoProblema.trim());

                //noinspection unchecked
                new FirebasePushMessageApi().deliver(
                        (List<PushDestination>) (List<?>) destinations,
                        MessageScope.ABERTURA_SOCORRO_ROTA,
                        PushMessage.builder()
                                .withTitle("ATENÇÃO! Pedido de Socorro!")
                                .withBody(messageBody)
                                .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                                .withAndroidLargeIcon(AndroidLargeIcon.SOS_NOTIFICATION)
                                .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                                .withMetadataScreen(String.valueOf(codSocorro))
                                .build());
            } else {
                Log.d(TAG, "Nenhum colaborador para notificar VIA PUSH sobre abertura do socorro");
            }
        } else {
            Log.d(TAG, "Nenhum colaborador para notificar sobre abertura do socorro");
        }
    }

    private void internalNotificaSobreAtendimento(@NotNull final SocorroRotaDao socorroDao,
                                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreAtendimento(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoAtendimentoSocorroRota> colaboradores;
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
                    MessageScope.ATENDIMENTO_SOCORRO_ROTA,
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
