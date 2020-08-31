package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaAvaCorpAvilan extends Sistema {
    public SistemaAvaCorpAvilan(@NotNull final SistemaKey sistemaKey,
                                @NotNull final RecursoIntegrado recursoIntegrado,
                                @NotNull final IntegradorProLog integradorProLog,
                                @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
    }
}
