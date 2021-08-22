package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class CargoNaoUtilizado {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int qtdPermissoesVinculadas;

    @NotNull
    public static CargoNaoUtilizado createDummy() {
        return new CargoNaoUtilizado(
                1L,
                "Motorista",
                42);
    }
}