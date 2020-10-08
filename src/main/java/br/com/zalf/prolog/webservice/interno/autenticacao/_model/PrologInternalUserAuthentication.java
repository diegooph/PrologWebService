package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Representa um usuário do sistema interno do Prolog que está tentando se autenticar (logar) no sistema.
 * São usuários cadastrados na tabela "interno.prolog_user".
 * <p>
 * Created on 2020-10-08
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PrologInternalUserAuthentication {
    @NotNull
    private final String username;
    @NotNull
    private final String password;
}