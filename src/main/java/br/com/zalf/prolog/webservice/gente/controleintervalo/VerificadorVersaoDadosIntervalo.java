package br.com.zalf.prolog.webservice.gente.controleintervalo;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.io.IOException;

public final class VerificadorVersaoDadosIntervalo {

    public void filter(ContainerRequestContext request) throws IOException {
        if (request.getHeaders() == null)
            throw new IllegalStateException();

        request.getHeaders().forEach((headerName, headerValues) -> {
            if (headerName.equals("ProLog-Versao-Dados-Intervalo")) {
                if (headerValues.size() == 1) {
                    if (headerValues.get(0) != null) {
                        try {
                            final long versaoDadosIntervalo = Long.parseLong(headerValues.get(0));
                            final ControleIntervaloDao dao = new ControleIntervaloDaoImpl();

                            return;
                        } catch (Exception e) {
                            // Pegamos qualquer erro no parse e lançamos como não autorizado.
                            throwsNotAuthorized();
                        }
                    } else {
                        throwsNotAuthorized();
                    }
                } else {
                    throwsNotAuthorized();
                }
            }
        });

        throw new IllegalStateException();
    }

    private void throwsNotAuthorized() {
        throw new NotAuthorizedException(Response.status(Response.Status.FORBIDDEN));
    }
}