package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public final class CargoInsercao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String nome;

    @NotNull
    public static CargoInsercao createDummy() {
        return new CargoInsercao(
                1L,
                "Vendedor");
    }
}
