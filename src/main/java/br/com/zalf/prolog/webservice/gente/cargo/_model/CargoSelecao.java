package br.com.zalf.prolog.webservice.gente.cargo._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class CargoSelecao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int qtdPermissoes;

    @NotNull
    public static CargoSelecao createDummy() {
        return new CargoSelecao(
                1L,
                "Motorista",
                2);
    }
}