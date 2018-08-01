package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nullable;

/**
 * Created on 30/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class ProLogValidator {

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws ProLogException if {@code reference} is null
     */
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) throws ProLogException {
        if (reference == null) {
            throw new GenericException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @throws ProLogException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, @Nullable Object errorMessage) throws ProLogException {
        if (!expression) {
            throw new GenericException(String.valueOf(errorMessage));
        }
    }
}
