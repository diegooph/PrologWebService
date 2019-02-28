package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Neste objeto estão as informações referentes às {@link PlacaAfericaoProtheusRodalog placas} que estão listadas no
 * {@link CronogramaAfericaoProtheusRodalog cronograma de aferição}, bem como algumas informações extras para montar
 * o cronograma corretamente no Aplicativo.
 * <p>
 * Todas as informações disponíveis neste objeto serão providas através de um endpoint integrado, e é de total
 * responsabilidade do endpoint prover as informações seguindo o padrão e estrutura deste objeto.
 * <p>
 * {@see protheusrodalog}
 */
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
     * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#SULCO}.
     */
    private Boolean podeAferirSulco;

    /**
     * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}.
     */
    private Boolean podeAferirPressao;

    /**
     * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#SULCO} e
     * do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}.
     */
    private Boolean podeAferirSulcoPressao;

    /**
     * Indica se a {@link #placa} permite aferição de estepes.
     */
    private Boolean podeAferirEstepe;

    public PlacaAfericaoProtheusRodalog() {
    }

    @NotNull
    public static PlacaAfericaoProtheusRodalog getPlacaAfericaoDummy() {
        final PlacaAfericaoProtheusRodalog placa = new PlacaAfericaoProtheusRodalog();
        placa.setPlaca("PRO0001");
        placa.setIntervaloDiasUltimaAfericaoSulco(3);
        placa.setIntervaloDiasUltimaAfericaoPressao(5);
        placa.setQuantidadePneusAplicados(4);
        placa.setPodeAferirEstepe(true);
        placa.setPodeAferirPressao(true);
        placa.setPodeAferirSulco(true);
        placa.setPodeAferirSulcoPressao(true);
        return placa;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public Integer getIntervaloDiasUltimaAfericaoSulco() {
        return intervaloDiasUltimaAfericaoSulco;
    }

    public void setIntervaloDiasUltimaAfericaoSulco(final Integer intervaloDiasUltimaAfericaoSulco) {
        this.intervaloDiasUltimaAfericaoSulco = intervaloDiasUltimaAfericaoSulco;
    }

    public Integer getIntervaloDiasUltimaAfericaoPressao() {
        return intervaloDiasUltimaAfericaoPressao;
    }

    public void setIntervaloDiasUltimaAfericaoPressao(final Integer intervaloDiasUltimaAfericaoPressao) {
        this.intervaloDiasUltimaAfericaoPressao = intervaloDiasUltimaAfericaoPressao;
    }

    public Integer getQuantidadePneusAplicados() {
        return quantidadePneusAplicados;
    }

    public void setQuantidadePneusAplicados(final Integer quantidadePneusAplicados) {
        this.quantidadePneusAplicados = quantidadePneusAplicados;
    }

    public Boolean getPodeAferirSulco() {
        return podeAferirSulco;
    }

    public void setPodeAferirSulco(final Boolean podeAferirSulco) {
        this.podeAferirSulco = podeAferirSulco;
    }

    public Boolean getPodeAferirPressao() {
        return podeAferirPressao;
    }

    public void setPodeAferirPressao(final Boolean podeAferirPressao) {
        this.podeAferirPressao = podeAferirPressao;
    }

    public Boolean getPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public void setPodeAferirSulcoPressao(final Boolean podeAferirSulcoPressao) {
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
    }

    public Boolean getPodeAferirEstepe() {
        return podeAferirEstepe;
    }

    public void setPodeAferirEstepe(final Boolean podeAferirEstepe) {
        this.podeAferirEstepe = podeAferirEstepe;
    }
}
