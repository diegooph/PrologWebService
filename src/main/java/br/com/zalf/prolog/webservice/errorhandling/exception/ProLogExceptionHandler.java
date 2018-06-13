package br.com.zalf.prolog.webservice.errorhandling.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogExceptionHandler {

    private ProLogExceptionHandler() {
        throw new IllegalStateException(ProLogExceptionHandler.class.getSimpleName() + " cannot be instantiated!");
    }

    public static ProLogException map(@NotNull final Throwable throwable,
                                      @NotNull final String genericErrorMessage) {
        if (throwable instanceof ProLogException) {
            return (ProLogException) throwable;
        } else {
            return new GenericException(genericErrorMessage, null);
        }
    }
}