package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.KeyCaseInsensitiveMultivaluedMap;
import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created on 2020-08-04
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class OkHttpLogInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final RequestLog requestLog = createRequestLog(request);
        final Response response;
        final LogService logService = new LogService();
        try {
            response = chain.proceed(request);
        } catch (final Throwable t) {
            // Em caso de erro na requisição http, logamos um erro genérico como ResponseLogProlog.
            logService.saveLogToDatabaseAsync(requestLog, ResponseLog.errorLog(t));
            throw t;
        }
        final ResponseLog responseLog = createResponseLog(response);
        logService.saveLogToDatabaseAsync(requestLog, responseLog);
        return response;
    }

    @NotNull
    private ResponseLog createResponseLog(@NotNull final Response response) throws IOException {
        final ResponseBody body = response.body();
        return new ResponseLog(
                getHeaders(response.headers()),
                null,
                null,
                !response.isSuccessful(),
                response.code(),
                body == null ? null : readResponseAsString(body));
    }

    @NotNull
    private RequestLog createRequestLog(@NotNull final Request request) throws IOException {
        final RequestBody body = request.body();
        return new RequestLog(
                getHeaders(request.headers()),
                request.url().toString(),
                request.method(),
                body == null ? null : readRequestAsString(body));
    }

    @NotNull
    private KeyCaseInsensitiveMultivaluedMap<String, String> getHeaders(@NotNull final Headers headers) {
        final KeyCaseInsensitiveMultivaluedMap<String, String> headersMap = new KeyCaseInsensitiveMultivaluedMap<>();
        headers.toMultimap().forEach(headersMap::put);
        return headersMap;
    }

    @NotNull
    private String readResponseAsString(@NotNull final ResponseBody body) throws IOException {
        final BufferedSource source = body.source();
        source.request(Long.MAX_VALUE);
        final Buffer buffer = source.getBuffer();
        return buffer.clone().readString(StandardCharsets.UTF_8);
    }

    @NotNull
    private String readRequestAsString(@NotNull final RequestBody body) throws IOException {
        final Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readString(StandardCharsets.UTF_8);
    }
}
