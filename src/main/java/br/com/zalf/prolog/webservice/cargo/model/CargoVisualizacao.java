package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-05-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoVisualizacao {
    @NotNull
    private final Long codCargo;
    @NotNull
    private final Long codUnidadeCargo;
    @NotNull
    private final String nomeCargo;
    @NotNull
    private final List<CargoPilarProLog> pilaresCargo;

    public CargoVisualizacao(@NotNull final Long codCargo,
                             @NotNull final Long codUnidadeCargo,
                             @NotNull final String nomeCargo,
                             @NotNull final List<CargoPilarProLog> pilaresCargo) {
        this.codCargo = codCargo;
        this.codUnidadeCargo = codUnidadeCargo;
        this.nomeCargo = nomeCargo;
        this.pilaresCargo = pilaresCargo;
    }

    @NotNull
    public Long getCodCargo() {
        return codCargo;
    }

    @NotNull
    public Long getCodUnidadeCargo() {
        return codUnidadeCargo;
    }

    @NotNull
    public String getNomeCargo() {
        return nomeCargo;
    }

    @NotNull
    public List<CargoPilarProLog> getPilaresCargo() {
        return pilaresCargo;
    }
}
