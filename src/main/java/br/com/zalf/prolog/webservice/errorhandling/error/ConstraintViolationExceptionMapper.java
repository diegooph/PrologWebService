package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Created on 2019-10-31
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Provider
public final class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @NotNull
    public Response toResponse(final ConstraintViolationException exception) {

        final int totalErrors = exception.getConstraintViolations().size();
        final String constraintErrorMessages = exception
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));

        final String errorMessage = String.format(
                totalErrors > 1 ? "%d erros encontrados\n\n%s" : "%d erro encontrado\n\n%s",
                totalErrors,
                constraintErrorMessages);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ProLogErrorFactory.create(
                        new GenericException(
                                errorMessage,
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                exception.getConstraintViolations().toString(),
                                exception)))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
