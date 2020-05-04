package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ConfiguracaoTipoVeiculoAferivelListagem {

    @NotNull
    private final Long codConfiguracao;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final ConfiguracaoTipoVeiculoAferivelVeiculoVisualizacao tipoVeiculo;
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