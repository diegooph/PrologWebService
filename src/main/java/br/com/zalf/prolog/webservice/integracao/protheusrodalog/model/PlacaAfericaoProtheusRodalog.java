package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Neste objeto estão as informações referentes às {@link PlacaAfericaoProtheusRodalog placas} que estão listadas no
 * {@link CronogramaAfericaoProtheusRodalog cronograma de aferição}, bem como algumas informações extras para montar
 * o cronograma corretamente no Aplicativo.
 * <p>
 * Todas as informações disponíveis neste objeto serão providas através de um endpoint integrado, e é de total
 * responsabilidade do endpoint prover as informações seguindo o padrão e estrutura deste objeto.
 * <p>
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * {@see protheusrodalog}
 */
@Data
public final class PlacaAfericaoProtheusRodalog {
    /**
     * Representação da placa do veículo.
     */
    private String placa;

    /**
     * Número inteiro que representa a quantidade de dias desde a última aferição de Sulco.
     */
    private Integer intervaloDiasUltimaAfericaoSulco;

    /**
     * Número inteiro que representa a quantidade de dias desde a última aferição da Pressão.
     */
    private Integer intervaloDiasUltimaAfericaoPressao;

    /**
     * Indica quantos pneus estão vinculados a esse veículo.
     */
    private Integer quantidadePneusAplicados;

    /**
     * Indica se a forma de coleta do sulco da {@link #placa}.
     */
    private FormaColetaDadosAfericaoEnum formaColetaDadosSulco;

    /**
     * Indica se a forma de coleta da pressão da {@link #placa}.
     */
    private FormaColetaDadosAfericaoEnum formaColetaDadosPressao;

    /**
     * Indica se a forma de coleta do sulco e da pressão da {@link #placa}.
     */
    private FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;

    /**
     * Indica se a {@link #placa} permite aferição de estepes.
     */
    private Boolean podeAferirEstepe;

    @NotNull
    public static PlacaAfericaoProtheusRodalog getPlacaAfericaoDummy() {
        final PlacaAfericaoProtheusRodalog placa = new PlacaAfericaoProtheusRodalog();
        placa.setPlaca("PRO0001");
        placa.setIntervaloDiasUltimaAfericaoSulco(3);
        placa.setIntervaloDiasUltimaAfericaoPressao(5);
        placa.setQuantidadePneusAplicados(4);
        placa.setPodeAferirEstepe(true);
        placa.setFormaColetaDadosPressao(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        placa.setFormaColetaDadosSulco(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        placa.setFormaColetaDadosSulcoPressao(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        return placa;
    }

}
