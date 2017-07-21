package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
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
    private final Integrador integradorHttp;
    @NotNull
    private final Integrador integradorDatabase;

    protected Sistema(@NotNull final Integrador integradorHttp, Integrador integradoDatabase) {
        this.integradorHttp = checkNotNull(integradorHttp, "integradorHttp não pode ser nulo!");
        this.integradorDatabase = checkNotNull(integradoDatabase, "integradorDatabase não pode ser nulo!");
    }

    protected Integrador getIntegradorHttp() {
        return integradorHttp;
    }

    protected Integrador getIntegradorDatabase() {
        return integradorDatabase;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return getIntegradorDatabase().getVeiculosAtivosByUnidade(codUnidade);
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws Exception {
        return getIntegradorDatabase().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
    }

    @Override
    public NovaAfericao getNovaAfericao(String placaVeiculo) throws Exception {
        return getIntegradorDatabase().getNovaAfericao(placaVeiculo);
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao, @NotNull Long codUnidade) throws Exception {
        return getIntegradorDatabase().insertAfericao(afericao, codUnidade);
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo) throws Exception {
        return getIntegradorDatabase().getNovoChecklistHolder(codUnidade, codModelo, placaVeiculo);
    }
}