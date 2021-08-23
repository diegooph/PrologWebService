package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/06/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public final class CargoListagemEmpresa {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Long qtdColaboradoresVinculados;

    @NotNull
    public static CargoListagemEmpresa createDummy() {
        return new CargoListagemEmpresa(
                1L,
                "Motorista",
                2L);
    }
}