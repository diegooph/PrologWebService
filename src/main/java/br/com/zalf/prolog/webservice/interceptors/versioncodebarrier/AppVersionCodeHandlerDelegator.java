package br.com.zalf.prolog.webservice.interceptors.versioncodebarrier;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.ReflectionHelper;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AppVersionCodeHandler(
        targetVersionCode = -1,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.IGNORE)
@Provider
public final class AppVersionCodeHandlerDelegator implements ContainerRequestFilter {
    private static final String TAG = AppVersionCodeHandlerDelegator.class.getSimpleName();
    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        final String userAgent = containerRequestContext.getHeaderString(HttpHeaders.USER_AGENT);
        Log.d(TAG, "User-Agent: " + userAgent);
        if (userAgent == null || !userAgent.contains("okhttp")) {
            Log.d(TAG, "Requisição não veio do aplicativo Android, iremos ignorar");
            return;
        }
        // Se chegou até aqui sabemos que a requisição partiu do App Android.

        final Class<?> resourceClass = resourceInfo.getResourceClass();
        final Method resourceMethod = resourceInfo.getResourceMethod();
        final AppVersionCodeHandler methodAnnot = resourceMethod.getAnnotation(AppVersionCodeHandler.class);

        // Priorizamos anotações no método, se existirem.
        final AppVersionCodeHandler annotation = methodAnnot != null
                ? methodAnnot
                : resourceClass.getAnnotation(AppVersionCodeHandler.class);

        // Sanity check.
        if (annotation.targetVersionCode() == -1) {
            return;
        }

        final String versionCodeString = containerRequestContext
                .getHeaderString(ProLogCustomHeaders.APP_VERSION_ANDROID_APP.getHeaderName());

        final VersionNotPresentAction notPresentAction = annotation.actionIfVersionNotPresent();
        if (versionCodeString != null) {
            Log.d(TAG, "AppVersionCodeBarrier instanciado. VersionCode: " + versionCodeString);
            final AppVersionCodeBarrier versionCodeBarrier = ReflectionHelper.instance(annotation.implementation());
            versionCodeBarrier.stopIfNeeded(
                    Long.valueOf(versionCodeString),
                    annotation.targetVersionCode(),
                    annotation.versionCodeHandlerMode());
        } else if (notPresentAction.equals(VersionNotPresentAction.IGNORE)) {
            Log.d(TAG, "Versão do app não presente no header e foi setado para IGNORAR a requisição nesse caso");
        } else if (notPresentAction.equals(VersionNotPresentAction.BLOCK_ANYWAY)) {
            Log.d(TAG, "Versão do app não presente no header e foi setado para BLOQUEAR a requisição nesse caso");
            throw new RuntimeException();
        }
    }
}