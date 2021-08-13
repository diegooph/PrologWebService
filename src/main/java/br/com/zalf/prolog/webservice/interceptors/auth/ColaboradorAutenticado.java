package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.authorization.StatusSecured;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;
import java.time.ZoneId;

/**
 * Created on 2020-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class ColaboradorAutenticado implements Principal {
    private static final String TAG = ColaboradorAutenticado.class.getSimpleName();
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

    @NotNull
    public ZoneId getZoneIdUnidadeColaborador() {
        try {
            return TimeZoneManager.getZoneIdForCpf(cpf);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar ZoneId pelo CPF: " + cpf, throwable);
            throw new RuntimeException(throwable);
        }
    }
}
