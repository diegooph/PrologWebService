package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public class Router {
    private IntegracaoDao integracaoDao;

    private Router(IntegracaoDao integracaoDao) {
        this.integracaoDao = integracaoDao;
    }

    public static Router newInstance(@NotNull final IntegracaoDao integracaoDao) {
        return new Router(integracaoDao);
    }

    public void accept(@NotNull String userToken, @NotNull final RouterFlow routerFlow) throws Exception {
        final SistemaKey sistemaKey = integracaoDao.getSistemaKey(userToken);
        if (sistemaKey != null) {
            routerFlow.proced(SistemasFactory.createSistema(sistemaKey));
        } else {
            routerFlow.cancel();
        }
    }
}