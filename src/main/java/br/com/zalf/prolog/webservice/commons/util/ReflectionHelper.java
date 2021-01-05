package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ReflectionHelper {
    private ReflectionHelper() {
        throw new IllegalStateException(ReflectionHelper.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static <T> T instance(@NotNull final Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}