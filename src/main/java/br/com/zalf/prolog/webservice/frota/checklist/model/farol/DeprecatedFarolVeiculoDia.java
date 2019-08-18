package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 08/08/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class DeprecatedFarolVeiculoDia {
    @NotNull
    private final Veiculo veiculo;
    @Nullable
    private final Checklist checklistSaidaDia;
    @Nullable
    private final Checklist checklistRetornoDia;
    @Nullable
    private final List<ItemOrdemServico> itensCriticoEmAberto;

    public DeprecatedFarolVeiculoDia(@NotNull final Veiculo veiculo,
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
