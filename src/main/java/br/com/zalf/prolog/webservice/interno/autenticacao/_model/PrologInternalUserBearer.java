package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaDao;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Representa um usuário do sistema interno do Prolog que está tentando acessar algum recurso através de autorização
 * Bearer.
 * São usuários cadastrados na tabela "interno.prolog_user".
 * <p>
 * Created on 2020-03-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PrologInternalUserBearer implements PrologInternalUserAuthorization {
    @NotNull
    private final String token;

    @NotNull
    @Override
    public PrologInternalUser authorize(@NotNull final AutenticacaoInternaDao dao) throws Throwable {
        return dao.getPrologInternalUserByToken(token)
                .orElseThrow(() -> {
                    throw new NotAuthorizedException("User not found with token: " + token);
                });
    }
}
