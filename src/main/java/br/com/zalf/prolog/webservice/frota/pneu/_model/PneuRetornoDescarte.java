package br.com.zalf.prolog.webservice.frota.pneu._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;

/**
 * Created on 2020-12-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class PneuRetornoDescarte {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codPneu;
    @NotNull
    private final Long codColaborador;
    @NotBlank(message = "É necessário haver um motivo do retorno do pneu do descarte.")
    private final String motivoRetornoDescarte;
}
