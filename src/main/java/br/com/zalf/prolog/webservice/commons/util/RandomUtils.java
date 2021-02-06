package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Created on 22/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RandomUtils {
    @NotNull
    private static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    @NotNull
    private final static Random RANDOM = new Random();

    private RandomUtils() {
        throw new IllegalStateException(RandomUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String randomAlphanumeric(final int length) {
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        return sb.toString();
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }
}
