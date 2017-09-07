package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.sun.istack.internal.NotNull;

import javax.ws.rs.NotAuthorizedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;

public final class BasicAuthenticator extends ProLogAuthenticator {

    BasicAuthenticator(AutenticacaoService service) {
        super(service);
    }

    private static final SimpleDateFormat FORMAT_DATA_NASCIMENTO_BASIC_AUTHORIZATION =
            new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));

    @Override
    public void validate(@NotNull final String value,
                            @NotNull final int[] permissions,
                            final boolean needsToHaveAll) throws NotAuthorizedException {
        final String[] cpfDataNascimento = new String(Base64.getDecoder().decode(value.getBytes())).split(":");
        if (cpfDataNascimento.length != 2) {
            throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        }

        try {
            if (!service.userHasPermission(
                    Long.parseLong(cpfDataNascimento[0]),
                    FORMAT_DATA_NASCIMENTO_BASIC_AUTHORIZATION.parse(cpfDataNascimento[1]).getTime(),
                    permissions,
                    needsToHaveAll)) {
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        }
    }
}