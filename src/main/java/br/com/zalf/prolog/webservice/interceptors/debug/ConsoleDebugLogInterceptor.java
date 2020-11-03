package br.com.zalf.prolog.webservice.interceptors.debug;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
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
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

@ConsoleDebugLog
@Provider
public final class ConsoleDebugLogInterceptor implements ContainerRequestFilter {
    private static final String TAG = ConsoleDebugLogInterceptor.class.getSimpleName();

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext request) throws IOException {
        if (!BuildConfig.DEBUG) {
            return;
        }

        Log.d(TAG, "--> " + request.getMethod() + " " + request.getUriInfo().getPath());
        printClassAndMethodName(resourceInfo);
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

    private void printClassAndMethodName(final ResourceInfo resourceInfo) {
        final Method method = resourceInfo.getResourceMethod();
        final Class<?> resourceClass = resourceInfo.getResourceClass();
        try {
            final ClassPool pool = ClassPool.getDefault();
            // Por conta do Javassist e o Tomcat manterem diferentes classloaders.
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            final CtClass cc = pool.get(method.getDeclaringClass().getCanonicalName());
            final CtMethod javassistMethod = cc.getDeclaredMethod(method.getName());
            final int linenumber = javassistMethod.getMethodInfo().getLineNumber(0);
            if (linenumber > 0) {
                // Com logs no padrão .(%s.java:%d), o IntelliJ cria um link clicável.
                Log.d(TAG, String.format("Where: .%s(%s.java:%d)",
                        method.getName(),
                        resourceClass.getSimpleName(),
                        linenumber));
                return;
            }
        } catch (final Throwable t) {
            // Ignored.
        }

        // Fall back caso o log principal não funcione.
        Log.d(TAG, "Class Name: " + resourceClass.getName());
        Log.d(TAG, "Method Name: " + resourceInfo.getResourceMethod());
    }

    private void printQueryParameters(final ContainerRequestContext request) {
        final MultivaluedMap<String, String> map = request.getUriInfo().getQueryParameters();
        if (map != null && !map.isEmpty()) {
            Log.d(TAG, "Query-Parameters: " + map.toString());
        }
    }

    private void printHeaders(final ContainerRequestContext request) {
        if (request.getHeaders() == null) {
            return;
        }

        request.getHeaders().forEach((s, strings) -> Log.d(TAG, s + ": " + strings));
    }

    private boolean isJson(final ContainerRequestContext request) {
        final MediaType mediaType = request.getMediaType();
        return mediaType != null && mediaType.toString().contains("application/json");
    }
}