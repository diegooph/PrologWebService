package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.StatusSecured;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;

/**
 * Created on 2020-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class ColaboradorAutenticado implements Principal {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long cpf;
    @NotNull
    private final StatusSecured statusSecured;

    @Override
    public String getName() {
        return String.valueOf(codigo);
    }
}
