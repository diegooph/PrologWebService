package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class ConfiguracaoTipoVeiculoAferivelInsercao {

    @Nullable
    private final Long codConfiguracao;
    @NotNull
    private final Long codTipoVeiculo;
    private final boolean podeAferirPressao;
    private final boolean podeAferirSulco;
    private final boolean podeAferirSulcoPressao;
    private final boolean podeAferirEstepe;
    @NotNull
    private final FormaColetaDadosAfericaoEnum formaColetaDadosPressao;
    @NotNull
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulco;
    @NotNull
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;
    @NotNull
    private final FormaColetaDadosAfericaoEnum formaColetaDadosFechamentoServico;

}
