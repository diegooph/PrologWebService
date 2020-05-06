package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ConfiguracaoTipoVeiculoAferivelListagem {

    @Nullable
    private final Long codConfiguracao;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final ConfiguracaoTipoVeiculoAferivelVeiculoVisualizacao tipoVeiculo;
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