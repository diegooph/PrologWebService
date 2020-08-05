package br.com.zalf.prolog.webservice.integracao.network;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created on 2020-08-04
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class LogRequestResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        final ResponseBody body = response.body();
        if (body != null) {
            final BufferedSource source = body.source();
            source.request(Long.MAX_VALUE);
            final Buffer buffer = source.getBuffer();
            final String s = buffer.clone().readString(StandardCharsets.UTF_8);
            System.out.println(s);
        }
        return response;
    }
}
