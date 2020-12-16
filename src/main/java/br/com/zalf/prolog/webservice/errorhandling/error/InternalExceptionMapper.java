package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2020-10-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class InternalExceptionMapper {
    @NotNull
    public static Response toResponse(final Throwable throwable) {
        if (throwable instanceof NotAuthorizedException) {
            return createResponse(
                    Response.Status.UNAUTHORIZED.getStatusCode(),
                    createPrologError((NotAuthorizedException) throwable));
        }
        if (throwable instanceof ForbiddenException) {
            return createResponse(
                    Response.Status.FORBIDDEN.getStatusCode(),
                    createPrologError((ForbiddenException) throwable));
        }
        if (throwable instanceof ConstraintViolationException) {
            return createResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    createPrologError(convertToClientSideErrorException((ConstraintViolationException) throwable)));
        }

        final ProLogException proLogException = convertToPrologException(throwable);
        tryToLogEventException(proLogException);
        return createResponse(
                proLogException.getHttpStatusCode(),
                createPrologError(proLogException)
        );
    }

    @NotNull
    private static Response createResponse(final int statusCode,
                                           @NotNull final ProLogError entity) {
        return Response
                .status(statusCode)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @NotNull
    private static ClientSideErrorException convertToClientSideErrorException(
            @NotNull final ConstraintViolationException exception) {
        final int totalErrors = exception.getConstraintViolations().size();
        final String totalErrorsMessage = getTotalErrorsMessage(totalErrors);
        final String detailedMessage =
                getDetailedMessage(totalErrorsMessage, getConstraintErrorMessages(exception));
        return new ClientSideErrorException(
                totalErrorsMessage,
                detailedMessage,
                exception.getConstraintViolations().toString(),
                false);
    }

    @NotNull
    private static ProLogError createPrologError(final ProLogException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogError createPrologError(final NotAuthorizedException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogError createPrologError(final ForbiddenException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogException convertToPrologException(@Nullable final Throwable throwable) {
        if (!(throwable instanceof ProLogException)) {
            final String genericMessage = "Algo deu errado, tente novamente";
            final String developerMessage = "Erro mapeado no PrologExceptionMapper: " +
                    ((throwable != null) ? throwable.getMessage() : "null");
            final ProLogException ex = new GenericException(genericMessage, developerMessage, throwable);
            return ex;
        } else {
            return (ProLogException) throwable;
        }
    }

    private static void tryToLogEventException(final ProLogException proLogException) {
        if (proLogException.isloggableOnErrorReportSystem()) {

            final SentryEvent event = new SentryEvent();
            final Message message = new Message();
            final Map<String, Object> extras = getExtrasByException(proLogException);
            message.setMessage(proLogException.getMessage());
            event.setMessage(message);
            event.setLevel(SentryLevel.ERROR);
            event.setLogger(proLogException.getClass().getSimpleName());
            event.setThrowable(proLogException);
            event.setExtras(extras);

            ErrorReportSystem.logEvent(event);
        }
    }

    @NotNull
    private static String getConstraintErrorMessages(final ConstraintViolationException exception) {
        return exception
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
    }

    @NotNull
    private static String getDetailedMessage(final String totalErrorsMessage, final String constraintErrorMessages) {
        return String.format(
                totalErrorsMessage + ":\n%s",
                constraintErrorMessages);
    }

    @NotNull
    private static String getTotalErrorsMessage(final int totalErrors) {
        return String.format(
                totalErrors > 1 ? "%d erros encontrados" : "%d erro encontrado",
                totalErrors);
    }

    private static Map<String, Object> getExtrasByException(final ProLogException proLogException) {
        return Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("Developer message",
                                                       proLogException.getDeveloperMessage()),
                new AbstractMap.SimpleImmutableEntry<>("Parent exception",
                                                       proLogException.getParentException()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),
                        Collections::unmodifiableMap));
    }
}
