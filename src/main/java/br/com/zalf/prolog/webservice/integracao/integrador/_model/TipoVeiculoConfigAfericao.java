package br.com.zalf.prolog.webservice.integracao.integrador._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class TipoVeiculoConfigAfericao {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codTipoVeiculo;
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulco;
    private final FormaColetaDadosAfericaoEnum formaColetaDadosPressao;
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;
    private final boolean podeAferirEstepes;
}
