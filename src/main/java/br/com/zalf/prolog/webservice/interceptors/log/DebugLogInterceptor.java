package br.com.zalf.prolog.webservice.interceptors.log;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.L;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@DebugLog
@Provider
public final class DebugLogInterceptor implements ContainerRequestFilter {

    private static final String TAG = DebugLogInterceptor.class.getSimpleName();

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (!BuildConfig.DEBUG)
            return;

        L.d(TAG, "--> " + request.getMethod() + " " + request.getUriInfo().getPath());
        printHeaders(request);
        final String sizeBody = request.getLength() >= 0 ? " (" + request.getLength() + "-byte body)" : "";
        L.d(TAG, "--> END " + request.getMethod() + sizeBody);

        if (isJson(request)) {
            final String json = IOUtils.toString(request.getEntityStream(), StandardCharsets.UTF_8);
            L.d(TAG, json);
            final InputStream in = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
            request.setEntityStream(in);
        }
    }

    private void printHeaders(ContainerRequestContext request) {
        if (request.getHeaders() == null)
            return;

        request.getHeaders().forEach((s, strings) -> L.d(TAG, s + ": " + strings));
    }

    private boolean isJson(ContainerRequestContext request) {
        return request.getMediaType().toString().contains("application/json");
    }
}