package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import com.sun.istack.internal.NotNull;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    @NotNull
    private final IntegradorProLog integradorProLog;

    protected Sistema(IntegradorProLog integradorProLog) {
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser nulo!");
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        return getIntegradorProLog().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return getIntegradorProLog().getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getIntegradorProLog().insertAfericao(afericao, codUnidade);
    }

    @Override
    public boolean insertChecklist(@NotNull Checklist checklist) throws Exception {
        return getIntegradorProLog().insertChecklist(checklist);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        return getIntegradorProLog().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
    }

    protected IntegradorProLog getIntegradorProLog() {
        return integradorProLog;
    }
}