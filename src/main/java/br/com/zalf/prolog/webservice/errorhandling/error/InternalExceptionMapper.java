package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.SqlExceptionV2Wrapper;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ProLogSqlExceptionTranslator;
import com.google.common.collect.ImmutableMap;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.glassfish.jersey.server.ParamException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataAccessException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2020-10-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class InternalExceptionMapper {
    @NotNull
    public static Response toResponse(final Throwable throwable) {
        if (throwable instanceof SqlExceptionV2Wrapper) {
            final SqlExceptionV2Wrapper sqlExceptionV2Wrapper = (SqlExceptionV2Wrapper) throwable;
            if (sqlExceptionV2Wrapper.getParentException() instanceof DataAccessException) {
                final PSQLException psqlException = (PSQLException) throwable.getCause().getCause();
                final ProLogSqlExceptionTranslator translator = Injection.provideProLogSqlExceptionTranslator();
                return createResponse(Response.Status.BAD_REQUEST.getStatusCode(),
                                      createPrologError(translator.doTranslate(psqlException,
                                                                               psqlException.getMessage())));
            }
            if (sqlExceptionV2Wrapper.getParentException() instanceof SQLException) {
                final SQLException sqlException = (SQLException) sqlExceptionV2Wrapper.getParentException();
                final ProLogSqlExceptionTranslator translator = Injection.provideProLogSqlExceptionTranslator();
                return createResponse(Response.Status.BAD_REQUEST.getStatusCode(),
                                      createPrologError(translator.doTranslate(sqlException, throwable.getMessage())));
            }
        }
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
        if (throwable instanceof ParamException) {
            return createResponse(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    createPrologError(convertToClientSideErrorException((ParamException) throwable)));
        }
        if (throwable instanceof NotFoundException) {
            return createResponse(
                    Response.Status.NOT_FOUND.getStatusCode(),
                    createPrologError((NotFoundException) throwable));
        }

        final ProLogException proLogException = convertToPrologException(throwable);
        tryToLogEventException(proLogException);
        return createResponse(
                proLogException.getHttpStatusCode(),
                createPrologError(proLogException));
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
    private static ClientSideErrorException convertToClientSideErrorException(
            @NotNull final ParamException exception) {
        return new ClientSideErrorException(
                String.format("O parâmetro %s possuí valores inválidos.", exception.getParameterName()),
                String.format(
                        "Não foi possível processar as informações passadas no parâmetro %s.",
                        exception.getParameterName()),
                exception.getCause().getMessage(),
                false);
    }

    @NotNull
    private static ProLogError createPrologError(@NotNull final ProLogException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogError createPrologError(@NotNull final NotAuthorizedException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogError createPrologError(@NotNull final ForbiddenException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogError createPrologError(@NotNull final NotFoundException proLogException) {
        return ProLogErrorFactory.create(proLogException);
    }

    @NotNull
    private static ProLogException convertToPrologException(@Nullable final Throwable throwable) {
        if (!(throwable instanceof ProLogException)) {
            final String genericMessage = "Algo deu errado, tente novamente";
            final String developerMessage = "Erro mapeado no PrologExceptionMapper: " +
                    ((throwable != null) ? throwable.getMessage() : "null");
            return new GenericException(genericMessage, developerMessage, throwable);
        } else {
            return (ProLogException) throwable;
        }
    }

    private static void tryToLogEventException(@NotNull final ProLogException proLogException) {
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

    @NotNull
    private static Map<String, Object> getExtrasByException(@NotNull final ProLogException prologException) {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("Error message", prologException.getMessage());
        if (prologException.getDetailedMessage() != null) {
            builder.put("Detailed message", prologException.getDetailedMessage());
        }
        if (prologException.getDeveloperMessage() != null) {
            builder.put("Developer message", prologException.getDeveloperMessage());
        }
        return builder.build();
    }
}
