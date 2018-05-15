package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 14/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ReflectionHelper {

    private ReflectionHelper() {
        throw new IllegalStateException(ReflectionHelper.class.getSimpleName() + " cannot be instantiated!");
    }

    public static <T> T instance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}