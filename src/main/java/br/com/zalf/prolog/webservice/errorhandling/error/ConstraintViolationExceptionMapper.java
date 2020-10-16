package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.BadRequestException;
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

    @Override
    @NotNull
    public Response toResponse(final ConstraintViolationException exception) {

        final int totalErrors = exception.getConstraintViolations().size();
        final String totalErrorsMessage = String.format(
                totalErrors > 1 ? "%d erros encontrados" : "%d erro encontrado",
                totalErrors);
        final String constraintErrorMessages = exception
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));

        final String detailedMessage = String.format(
                totalErrorsMessage + ":\n%s",
                constraintErrorMessages);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ProLogErrorFactory.create(
                        new BadRequestException(
                                totalErrorsMessage,
                                detailedMessage,
                                exception.getConstraintViolations().toString())))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
