package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 12/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MathUtils {
    private MathUtils() {
        throw new IllegalStateException(MathUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static int getNumberInPosition(final int number, final int position) {
        return Character.digit(String.valueOf(number).charAt(position - 1), 10);
    }
}
