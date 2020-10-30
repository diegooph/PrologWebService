package br.com.zalf.prolog.webservice.errorhandling.error;

import org.jetbrains.annotations.NotNull;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
        return InternalExceptionMapper.toResponse(exception);
    }
}
