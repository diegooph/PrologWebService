package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 08/08/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Deprecated
public class DeprecatedFarolChecklist {
    @NotNull
    private final List<DeprecatedFarolVeiculoDia> farolVeiculos;

    public DeprecatedFarolChecklist(List<DeprecatedFarolVeiculoDia> farolVeiculos) {
        this.farolVeiculos = farolVeiculos;
    }

    public List<DeprecatedFarolVeiculoDia> getFarolVeiculos() {
        return farolVeiculos;
    }
}
