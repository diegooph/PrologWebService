package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public final class CargoEdicao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    @NotNull
    public static CargoEdicao createDummy() {
        return new CargoEdicao(
                1L,
                1L,
                "Motorista");
    }
}
