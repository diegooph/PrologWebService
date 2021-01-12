package br.com.zalf.prolog.webservice.commons.util.validators;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created on 30/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class PrologValidator {
    @NotNull
    @CanIgnoreReturnValue
    public static <T> T checkNotNull(@Nullable final T reference, @Nullable final Object errorMessage)
            throws ProLogException {
        if (reference == null) {
            throw new GenericException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static void checkArgument(final boolean expression, @Nullable final Object errorMessage)
            throws ProLogException {
        if (!expression) {
            throw new GenericException(String.valueOf(errorMessage));
        }
    }
}
