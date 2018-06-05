package br.com.zalf.prolog.webservice.errorhandling.exception;

import com.google.common.base.Throwables;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogExceptionPropagator {

    private ProLogExceptionPropagator() {
        throw new IllegalStateException(ProLogExceptionPropagator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void handle(@NotNull final Throwable throwable,
                              @NotNull final String genericErrorMessage) throws ProLogException {
        Throwables.throwIfInstanceOf(throwable, ProLogException.class);
        throw new GenericException(genericErrorMessage, null);
    }
}