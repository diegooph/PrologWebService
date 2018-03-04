package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 04/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class Now {

    private Now() {
        throw new IllegalStateException(Now.class.getSimpleName() + " cannot be instantiated!");
    }

    public static long utcMillis() {
        return System.currentTimeMillis();
    }
}