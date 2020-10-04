package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Representa um usuário do sistema interno do Prolog que está tentando se autenticar.
 * São usuários cadastrados na tabela "interno.prolog_user".
 * <p>
 * Created on 2020-03-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@Builder
public final class PrologInternalUserAuthentication {
    @NotNull
    private final String username;
    @NotNull
    private final String password;

    public boolean doesPasswordMatch(@NotNull final String bcryptDatabaseToken) {
        return BCrypt.verifyer().verify(password.toCharArray(), bcryptDatabaseToken).verified;
    }
}
