package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoChecklistOffline {
    @NotNull
    private final Long codCargo;

    public CargoChecklistOffline(@NotNull final Long codCargo) {
        this.codCargo = codCargo;
    }

    @NotNull
    public Long getCodCargo() {
        return codCargo;
    }
}