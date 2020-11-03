package br.com.zalf.prolog.webservice.errorhandling.error;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ProLogExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    @NotNull
    public Response toResponse(final Throwable throwable) {
        return InternalExceptionMapper.toResponse(throwable);
    }
}