package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaDao;
import br.com.zalf.prolog.webservice.interno.autenticacao.BCryptValidator;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Representa um usuário do sistema interno do Prolog que está tentando acessar algum recurso através de autorização
 * Basic.
 * São usuários cadastrados na tabela "interno.prolog_user".
 * <p>
 * Created on 2020-03-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@Builder
public final class PrologInternalUserBasic implements PrologInternalUserAuthorization {
    @NotNull
    private final String username;
    @NotNull
    private final String password;

    @NotNull
    @Override
    public PrologInternalUser authorize(@NotNull final AutenticacaoInternaDao dao) throws Throwable {
        final PrologInternalUser internalUser = dao.getPrologInternalUserByUsername(username)
                .orElseThrow(() -> {
                    throw new NotAuthorizedException("User not found with username: " + username);
                });
        if (!BCryptValidator.doesPasswordMatch(password, internalUser.getEncryptedPassword())) {
            throw new NotAuthorizedException("Incorrect password!");
        }
        return internalUser;
    }
}
