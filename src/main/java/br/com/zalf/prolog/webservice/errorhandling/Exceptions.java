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
        } catch (final Throwable ignored) {
        }
    }

    /**
     * Cast a CheckedException as an unchecked one.
     *
     * @param throwable to cast
     * @param <T>       the type of the Throwable
     * @return this method will never return a Throwable instance, it will just throw it.
     * @throws T the throwable as an unchecked throwable
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(final Throwable throwable) throws T {
        throw (T) throwable;
    }

    @FunctionalInterface
    public interface RunnableException {
        void run() throws Throwable;
    }
}
