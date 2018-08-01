package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 9/21/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolVeiculoDia {
    @NotNull
    private final String placaVeiculo;
    @Nullable
    private final ChecklistFarol checklistSaidaDia;
    @Nullable
    private final ChecklistFarol checklistRetornoDia;
    @Nullable
    private final List<FarolPerguntaCritica> perguntaCriticasEmAberto;

    public FarolVeiculoDia(@NotNull final String placaVeiculo,
                           @Nullable final ChecklistFarol checklistSaidaDia,
                           @Nullable final ChecklistFarol checklistRetornoDia,
                           @Nullable final List<FarolPerguntaCritica> perguntaCriticasEmAberto) {
        this.placaVeiculo = placaVeiculo;
        this.checklistSaidaDia = checklistSaidaDia;
        this.checklistRetornoDia = checklistRetornoDia;
        this.perguntaCriticasEmAberto = perguntaCriticasEmAberto;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @Nullable
    public ChecklistFarol getChecklistSaidaDia() {
        return checklistSaidaDia;
    }

    @Nullable
    public ChecklistFarol getChecklistRetornoDia() {
        return checklistRetornoDia;
    }

    @Nullable
    public List<FarolPerguntaCritica> getPerguntaCriticasEmAberto() {
        return perguntaCriticasEmAberto;
    }
}