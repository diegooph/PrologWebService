package br.com.zalf.prolog.webservice.interno.autenticacao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-08
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class BCryptValidator {

    private BCryptValidator() {
        throw new IllegalStateException(BCryptValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean doesPasswordMatch(@NotNull final String clearTextPassword,
                                            @NotNull final String bcryptEncryptedPassword) {
        return BCrypt.verifyer().verify(clearTextPassword.toCharArray(), bcryptEncryptedPassword).verified;
    }
}
