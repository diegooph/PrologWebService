package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {

    public AvaCorpAvilan(@NotNull final Integrador integradorHttp, @NotNull final Integrador integradoDatabase) {
        super(integradorHttp, integradoDatabase);
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return getIntegradorHttp().getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        return getIntegradorHttp().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return getIntegradorHttp().insertChecklist(checklist);
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getIntegradorHttp().insertAfericao(afericao, codUnidade);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return getIntegradorHttp().getNovaAfericao(placaVeiculo);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        return getIntegradorHttp().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
    }
}