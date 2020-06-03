package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/13/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class InfosTipoVeiculoConfiguracaoAfericao {
    /**
     * Código da Unidade a qual os dados de configuração de aferição se referem.
     */
    @NotNull
    private final Long codUnidade;
    /**
     * Código do Tipo do Veículo a qual os dados de configuração de aferição se referem.
     */
    @NotNull
    private final Long codTipoVeiculo;
    /**
     * Indica a forma de coleta de dados do Sulco do {@link #codTipoVeiculo}.
     */
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulco;
    /**
     * Indica a forma de coleta de dados da Pressão do {@link #codTipoVeiculo}.
     */
    private final FormaColetaDadosAfericaoEnum formaColetaDadosPressao;
    /**
     * Indica a forma de coleta de dados do Sulco e da Pressão do {@link #codTipoVeiculo}.
     */
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;
    /**
     * Indica se o {@link #codTipoVeiculo} permite aferir estepes, caso o veículo possuir.
     */
    private final boolean podeAferirEstepes;

}
