package br.com.zalf.prolog.webservice.interceptors.log;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@DebugLog
@Provider
public final class DebugLogInterceptor implements ContainerRequestFilter {
    private static final String TAG = DebugLogInterceptor.class.getSimpleName();

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (!BuildConfig.DEBUG)
            return;

        Log.d(TAG, "--> " + request.getMethod() + " " + request.getUriInfo().getPath());
        Log.d(TAG, "Class Name: " + resourceInfo.getResourceClass().getName());
        Log.d(TAG, "Method Name: " + resourceInfo.getResourceMethod().getName());
        printQueryParameters(request);
        printHeaders(request);
        final String sizeBody = request.getLength() >= 0 ? " (" + request.getLength() + "-byte body)" : "";
        Log.d(TAG, "--> END " + request.getMethod() + sizeBody);

        if (isJson(request)) {
            final String json = IOUtils.toString(request.getEntityStream(), StandardCharsets.UTF_8);
            Log.d(TAG, json);
            final InputStream in = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
            request.setEntityStream(in);
        }
    }

    private void printQueryParameters(ContainerRequestContext request) {
        final MultivaluedMap<String, String> map = request.getUriInfo().getQueryParameters();
        if (map != null && !map.isEmpty()) {
            Log.d(TAG, "Query-Parameters: " + map.toString());
        }
    }

    private void printHeaders(ContainerRequestContext request) {
        if (request.getHeaders() == null)
            return;

        request.getHeaders().forEach((s, strings) -> Log.d(TAG, s + ": " + strings));
    }

    private boolean isJson(ContainerRequestContext request) {
        final MediaType mediaType = request.getMediaType();
        return mediaType != null && mediaType.toString().contains("application/json");
    }
}