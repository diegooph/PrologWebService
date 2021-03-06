package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.spring.SpringContext;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoAberturaSocorroRota;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoAtendimentoSocorroRota;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.ColaboradorNotificacaoInvalidacaoSocorroRota;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email.PrologEmailService;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailReceiver;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailSender;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplate;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import br.com.zalf.prolog.webservice.messaging.push._model.*;
import br.com.zalf.prolog.webservice.messaging.push.send.FirebasePushMessageApi;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
            Log.d(TAG, "Processo de notifica????o com Firebase finalizado com sucesso!");
            shutdowService();
        }

        @Override
        public void onFailure(@NotNull final Throwable t) {
            Log.e(TAG, "Erro ao realizar envio de notifica????es do socorro em rota", t);
            shutdowService();
        }
    };

    private final PrologEmailService prologEmailService;

    NotificadorSocorroRota() {
        prologEmailService = SpringContext.getBean(PrologEmailService.class);
        service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
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
                    "Erro ao buscar colaboradores para notifica????o de abertura de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia e-mail.
            // Utilizamos um try/catch para evitar que estoure algum erro e impe??a o envio das notifica????es push.
            try {
                notificaAberturaSocorroEmail(colaboradores, nomeColaboradorAbertura, placaVeiculoProblema, codSocorro);
            } catch (final Throwable t) {
                // Logamos erro para ter controle no Sentry se estamos deixando de avisar os respons??veis sobre os
                // socorros solicitados.
                Log.e(TAG, "Erro ao enviar e-mail de abertura de socorro", t);
            }

            try {
                notificaAberturaSocorroPush(codSocorro, colaboradores, placaVeiculoProblema, nomeColaboradorAbertura);
            } catch (final Throwable t) {
                // Logamos erro para ter controle no Sentry se estamos deixando de avisar os respons??veis sobre os
                // socorros solicitados.
                Log.e(TAG, "Erro ao enviar push notification de abertura de socorro", t);
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
                    "Erro ao buscar colaboradores para notifica????o de atendimento de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notifica????o via firebase.
            final String messageBody =
                    "Seu pedido de socorro foi atendido. A ajuda est?? a caminho. Clique para ver mais.";
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

    private void notificaAberturaSocorroPush(
            @NotNull final Long codSocorro,
            @NotNull final List<ColaboradorNotificacaoAberturaSocorroRota> colaboradores,
            @NotNull final String placaVeiculoProblema,
            @NotNull final String nomeColaboradorAbertura) {
        final List<SimplePushDestination> pushDestinations = colaboradores
                .stream()
                .filter(c -> c.getTokensPushFirebase() != null)
                .map(c ->
                        Arrays.stream(c.getTokensPushFirebase())
                                .map(token -> new SimplePushDestination(String.valueOf(c.getCodColaborador()), token))
                                .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (!pushDestinations.isEmpty()) {
            // Envia notifica????o via firebase.
            final String messageBody = String.format(
                    "%s solicitou um socorro para o ve??culo %s. Clique para ver mais.",
                    formataNomeColaborador(nomeColaboradorAbertura),
                    placaVeiculoProblema.trim());

            //noinspection unchecked
            new FirebasePushMessageApi().deliver(
                    (List<PushDestination>) (List<?>) pushDestinations,
                    MessageScope.ABERTURA_SOCORRO_ROTA,
                    PushMessage.builder()
                            .withTitle("ATEN????O! Pedido de Socorro!")
                            .withBody(messageBody)
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.SOS_NOTIFICATION)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum colaborador para notificar VIA PUSH sobre abertura do socorro");
        }
    }

    private void notificaAberturaSocorroEmail(
            @NotNull final List<ColaboradorNotificacaoAberturaSocorroRota> colaboradores,
            @NotNull final String nomeColaboradorAbertura,
            @NotNull final String placaVeiculoProblema,
            @NotNull final Long codSocorro) {
        final Set<EmailReceiver> emails = colaboradores
                .stream()
                .map(ColaboradorNotificacaoAberturaSocorroRota::getEmailColaborador)
                .filter(Objects::nonNull)
                .distinct()
                .map(EmailReceiver::of)
                .collect(Collectors.toSet());
        if (!emails.isEmpty()) {
            this.prologEmailService
                    .deliverTemplate(
                            emails,
                            MessageScope.ABERTURA_SOCORRO_ROTA,
                            new EmailTemplateMessage(
                                    EmailTemplate.ABERTURA_SOCORRO_ROTA,
                                    EmailSender.of("socorroemrota@prologapp.com", "Socorro em Rota"),
                                    String.format("Pedido de Socorro ??? %s", placaVeiculoProblema),
                                    new ImmutableMap.Builder<String, String>()
                                            .put("nome_colaborador", nomeColaboradorAbertura)
                                            .put("placa_veiculo", placaVeiculoProblema)
                                            .put("link_socorro_rota",
                                                    // Adicionamos o c??digo do socorro em rota no path para propiciar
                                                    // a navega????o.
                                                    String.format(
                                                            "https://adm.prologapp.com/socorro-em-rota/%d",
                                                            codSocorro))
                                            .build()));
        } else {
            Log.d(TAG, "Nenhum colaborador para notificar VIA E-MAIL sobre abertura do socorro");
        }
    }

    private void internalNotificaSobreInvalidacao(@NotNull final SocorroRotaDao socorroDao,
                                                  @NotNull final Long codColaboradorInvalidacaoSocorro,
                                                  @NotNull final Long codSocorro) {
        Log.d(TAG, "internalNotificaSobreInvalidacao(...) on thread: " + Thread.currentThread().getName());
        final List<ColaboradorNotificacaoInvalidacaoSocorroRota> colaboradores;
        try {
            colaboradores = socorroDao.getColaboradoresNotificacaoInvalidacao(
                    codColaboradorInvalidacaoSocorro,
                    codSocorro);
        } catch (final Throwable t) {
            throw new IllegalStateException(
                    "Erro ao buscar colaboradores para notifica????o de invalida????o de socorro em rota\n" +
                            "codSocorro: " + codSocorro, t);
        }

        if (!colaboradores.isEmpty()) {
            // Envia notifica????o via firebase.
            final String messageBody =
                    "Seu pedido de socorro foi invalidado. Algo est?? errado. Clique para ver mais.";
            new FirebasePushMessageApi().deliver(
                    new ArrayList<>(colaboradores),
                    MessageScope.INVALIDACAO_SOCORRO_ROTA,
                    PushMessage.builder()
                            .withTitle("Socorro invalidado")
                            .withBody(messageBody)
                            .withAndroidSmallIcon(AndroidSmallIcon.SOS_NOTIFICATION)
                            .withAndroidLargeIcon(AndroidLargeIcon.ERROR_X)
                            .withScreenToNavigate(AndroidAppScreens.VISUALIZAR_SOCORRO_ROTA)
                            .withMetadataScreen(String.valueOf(codSocorro))
                            .build());
        } else {
            Log.d(TAG, "Nenhum token para notificar sobre invalida????o do socorro");
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

    @SuppressWarnings("UnstableApiUsage")
    void notificaSobreAbertura(@NotNull final SocorroRotaDao socorroRotaDao,
                               @NotNull final Long codUnidadeSocorro,
                               @NotNull final String nomeColaboradorAbertura,
                               @NotNull final String placaVeiculoProblema,
                               @NotNull final Long codSocorro) {
        Log.d(TAG, "notificaSobreAbertura(...) on thread: " + Thread.currentThread().getName());
        try {
            // Se estourar algo nessa chamada, n??o ser?? logado no BD a tentativa de envio de mensagem. Al??m desse caso,
            // tamb??m n??o ser?? logado no BD se acontecer um erro na busca dos colaboradores para notificar.
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
            Log.e(TAG, "Erro ao realizar envio de notifica????es sobre abertura de socorro", t);
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
            Log.e(TAG, "Erro ao realizar envio de notifica????es sobre atendimento de socorro", t);
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
            Log.e(TAG, "Erro ao realizar envio de notifica????es sobre invalida????o de socorro", t);
            shutdowService();
        }
    }
}
