package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class ConfiguracaoTipoVeiculoAferivelVeiculoVisualizacao {

    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String nomeVeiculo;

}
