package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-05-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class CargoVisualizacao {
    @NotNull
    private final Long codCargo;
    @NotNull
    private final Long codUnidadeCargo;
    @NotNull
    private final String nomeCargo;
    @NotNull
    private final List<CargoPilarProlog> pilaresCargo;
}
