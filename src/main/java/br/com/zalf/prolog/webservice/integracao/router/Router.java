package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public class Router implements OperacoesIntegradas {
    private IntegracaoDao integracaoDao;

    public Router(IntegracaoDao integracaoDao) {
        this.integracaoDao = integracaoDao;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull String userToken, @NotNull Long codUnidade) {
        return getSistema(userToken).getVeiculosAtivosByUnidade(userToken, codUnidade);
    }

    private Sistema getSistema(@NotNull final String userToken) {
        return SistemasFactory.createSistema(integracaoDao.getSistemaKey(userToken));
    }
}