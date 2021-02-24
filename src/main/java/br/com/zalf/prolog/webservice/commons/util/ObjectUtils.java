package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-12-21
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ObjectUtils {

    private ObjectUtils() {
        throw new IllegalStateException(ObjectUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean allNotNull(@Nullable final Object... values) {
        return org.apache.commons.lang3.ObjectUtils.allNotNull(values);
    }
}
