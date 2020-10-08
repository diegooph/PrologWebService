package br.com.zalf.prolog.webservice.interno;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-01
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@Builder
public final class PrologInternalUser {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String username;
    @NotNull
    @Exclude
    private final String encryptedPassword;
    @NotNull
    private final String databaseUsername;
}
