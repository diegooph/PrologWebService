package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.config.BuildConfig;

/**
 * Created on 2019-08-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProLogUtils {
    private ProLogUtils() {
        throw new IllegalStateException(ProLogUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}