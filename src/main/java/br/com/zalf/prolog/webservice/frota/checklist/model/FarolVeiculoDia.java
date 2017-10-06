package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * Created on 9/21/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolVeiculoDia {
    @NotNull
    private final Veiculo veiculo;
    @Nullable
    private final Checklist checklistSaidaDia;
    @Nullable
    private final Checklist checklistRetornoDia;
    @Nullable
    private final List<ItemOrdemServico> itensCriticoEmAberto;

    public FarolVeiculoDia(@NotNull final Veiculo veiculo,
                           @Nullable final Checklist checklistSaidaDia,
                           @Nullable final Checklist checklistRetornoDia,
                           @Nullable final List<ItemOrdemServico> itensCriticoEmAberto) {
        this.veiculo = veiculo;
        this.checklistSaidaDia = checklistSaidaDia;
        this.checklistRetornoDia = checklistRetornoDia;
        this.itensCriticoEmAberto = itensCriticoEmAberto;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Checklist getChecklistSaidaDia() {
        return checklistSaidaDia;
    }

    public Checklist getChecklistRetornoDia() {
        return checklistRetornoDia;
    }

    public List<ItemOrdemServico> getItensCriticoEmAberto() {
        return itensCriticoEmAberto;
    }
}