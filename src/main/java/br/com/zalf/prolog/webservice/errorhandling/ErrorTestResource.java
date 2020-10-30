package br.com.zalf.prolog.webservice.errorhandling;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 05/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/error-test")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ErrorTestResource {

    @GET
    @Path("{error-type}")
    public Object get(@PathParam("error-type") final String errorType) throws Throwable {
        return generateError(errorType);
    }

    @PUT
    @Path("{error-type}")
    public Object put(@PathParam("error-type") final String errorType) throws Throwable {
        return generateError(errorType);
    }

    @POST
    @Path("{error-type}")
    public Object post(@PathParam("error-type") final String errorType) throws Throwable {
        return generateError(errorType);
    }

    @DELETE
    @Path("{error-type}")
    public Object delete(@PathParam("error-type") final String errorType) throws Throwable {
        return generateError(errorType);
    }

    @Nullable
    private Object generateError(final String errorType) throws Throwable {
        final ErrorType type = ErrorType.fromString(errorType);
        switch (type) {
            case RESPONSE_WITH_ERROR:
                return Response.error("Isso é um response com status de ERROR e mensagem");
            case JAVA_STACK_TRACE:
                throw new IllegalStateException("Esse é um erro com stack trace do java");
            case PROLOG_ERROR_CODE:
                throw new GenericException("Isso é um erro com proLogErrorCode", "Mensagem aos devs o//");
            case NULL:
                return null;
            default:
                throw new IllegalStateException("ErrorType ainda não mapeado");
        }
    }

    enum ErrorType {
        RESPONSE_WITH_ERROR,
        JAVA_STACK_TRACE,
        PROLOG_ERROR_CODE,
        NULL;

        public static ErrorType fromString(@NotNull final String error) {
            Preconditions.checkNotNull(error);

            for (final ErrorType errorType : ErrorType.values()) {
                if (errorType.name().equals(error)) {
                    return errorType;
                }
            }

            throw new IllegalArgumentException("No error type mapped: " + error);
        }
    }
}