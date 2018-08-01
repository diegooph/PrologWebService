package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created on 9/21/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolChecklist {
    @NotNull
    private final List<FarolVeiculoDia> farolVeiculos;

    public FarolChecklist(List<FarolVeiculoDia> farolVeiculos) {
        this.farolVeiculos = farolVeiculos;
    }

    public List<FarolVeiculoDia> getFarolVeiculos() {
        return farolVeiculos;
    }
}