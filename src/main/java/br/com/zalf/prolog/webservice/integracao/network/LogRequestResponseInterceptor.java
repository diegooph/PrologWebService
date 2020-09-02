package br.com.zalf.prolog.webservice.integracao.network;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.logger._model.RequestLogProlog;
import br.com.zalf.prolog.webservice.integracao.logger._model.ResponseLogProlog;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created on 2020-08-04
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class LogRequestResponseInterceptor implements Interceptor {
    @NotNull
    private static final String TAG = LogRequestResponseInterceptor.class.getSimpleName();

    @NotNull
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final RequestLogProlog requestLogProlog = createRequestLog(request);
        Response response;
        try {
            response = chain.proceed(request);
        } catch (final Throwable t) {
            // Em caso de erro na requisição http, logamos um erro genérico como ResponseLogProlog.
            saveLog(requestLogProlog, ResponseLogProlog.errorLog(t));
            throw t;
        }
        final ResponseLogProlog responseLogProlog = createResponseLog(response);
        saveLog(requestLogProlog, responseLogProlog);
        return response;
    }

    private void saveLog(@NotNull final RequestLogProlog requestLogProlog,
                         @NotNull final ResponseLogProlog responseLogProlog) {
        try {
            Injection.provideLogDao().insertRequestResponseLogProlog(requestLogProlog, responseLogProlog);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir log de requisição no banco de dados", t);
        }
    }

    @NotNull
    private ResponseLogProlog createResponseLog(@NotNull final Response response) throws IOException {
        final ResponseBody body = response.body();
        return new ResponseLogProlog(
                response.code(),
                response.headers().toMultimap(),
                body == null ? null : readResponseAsString(body));
    }

    @NotNull
    private RequestLogProlog createRequestLog(@NotNull final Request request) throws IOException {
        final RequestBody body = request.body();
        return new RequestLogProlog(
                request.method(),
                request.url().toString(),
                request.headers().toMultimap(),
                body == null ? null : readContentType(body),
                body == null ? null : readRequestAsString(body));
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

    @Nullable
    private String readContentType(@NotNull final RequestBody body) {
        //noinspection ConstantConditions
        return body.contentType() == null ? null : body.contentType().toString();
    }
}
