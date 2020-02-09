package br.com.zalf.prolog.webservice.errorhandling;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class Exceptions {

    private Exceptions() {
        throw new IllegalStateException(Exceptions.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void swallowAny(@NotNull final RunnableException runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) { }
    }

    @FunctionalInterface
    public interface RunnableException {
        void run() throws Throwable;
    }
}
