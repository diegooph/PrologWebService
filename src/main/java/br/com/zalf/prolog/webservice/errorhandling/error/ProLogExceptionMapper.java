package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ProLogExceptionMapper implements ExceptionMapper<Throwable> {

    @NotNull
    public Response toResponse(Throwable throwable) {
        // Em casos de NotAuthorizedException, retornamos erro 401, sem seguir o padrão ProLog.
        if (throwable instanceof NotAuthorizedException) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        // Também cobre os casos de throwable == null.
        if (!(throwable instanceof ProLogException)) {
            throwable = new GenericException(
                    "Algo deu errado, tente novamente",
                    "Erro mapeado no ProLogExceptionMapper: " + (throwable != null ? throwable.getMessage() : "null"),
                    throwable);
        }

        final ProLogException proLogException = (ProLogException) throwable;
        return Response
                .status(proLogException.getHttpStatusCode())
                .entity(ProLogErrorFactory.create(proLogException))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}