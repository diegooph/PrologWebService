package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-08
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PrologInternalUserAuthorization {
    @NotNull
    PrologInternalUser authorize(@NotNull final AutenticacaoInternaDao dao) throws Throwable;
}
