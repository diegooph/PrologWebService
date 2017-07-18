package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.OperacoesIntegradasVeiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public class RouterVeiculo extends Router implements OperacoesIntegradasVeiculo {

    public RouterVeiculo(IntegracaoDao integracaoDao, Integrador integradorDatabase, String userToken) {
        super(integracaoDao, integradorDatabase, userToken);
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade);
        } else {
            return getIntegradorDatabase().getVeiculosAtivosByUnidade(codUnidade);
        }
    }
}