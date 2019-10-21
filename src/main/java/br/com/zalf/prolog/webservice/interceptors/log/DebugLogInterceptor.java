package br.com.zalf.prolog.webservice.interceptors.log;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
                Log.d(TAG, String.format("Where: .%s(%s.java:%d)",
                        method.getName(),
                        resourceClass.getSimpleName(),
                        linenumber));
                return;
            }
        } catch (final Throwable t) {
            // Ignored.
            System.out.println("t");
        }

        // Fall back caso o log principal não funcione.
        Log.d(TAG, "Class Name: " + resourceClass.getName());
        Log.d(TAG, "Method Name: " + resourceInfo.getResourceMethod());
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