package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class IntegradorBase implements Integrador {

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception  {
        throw new UnsupportedOperationException();
    }
}