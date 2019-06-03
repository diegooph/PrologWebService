package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class SistemaGlobusPiccolotur extends Sistema {
    @NotNull
    private final GlobusPiccoloturRequester requester;

    public SistemaGlobusPiccolotur(@NotNull final GlobusPiccoloturRequester requester,
                                   @NotNull final SistemaKey sistemaKey,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull final Checklist checklist) throws Exception {
        Connection conn = null;
        try {
            conn = new DatabaseConnectionProvider().provideDatabaseConnection();
            return 10L;
        } finally {
            new DatabaseConnectionProvider().closeResources(conn);
        }
    }
}
