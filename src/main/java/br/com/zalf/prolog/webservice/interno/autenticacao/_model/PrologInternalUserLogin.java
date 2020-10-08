package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-08
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@Builder
public final class PrologInternalUserLogin {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String username;
    @NotNull
    private final String databaseUsername;
    @NotNull
    private final String token;
}
