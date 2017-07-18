package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 18/07/17.
 */
abstract class Router {
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final Integrador integradorDatabase;
    @NotNull
    private final String userToken;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    Router(IntegracaoDao integracaoDao, Integrador integradorDatabase, String userToken) {
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorDatabase = checkNotNull(integradorDatabase, "integradorDatabase não pode ser null!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser null!");
    }

    Sistema getSistema() throws Exception {
        if (sistemaKey == null && !hasTried) {
            sistemaKey = integracaoDao.getSistemaKey(userToken);
            hasTried = true;
            return null;
        }

        return SistemasFactory.createSistema(sistemaKey, integradorDatabase);
    }

    Integrador getIntegradorDatabase() {
        return integradorDatabase;
    }
}