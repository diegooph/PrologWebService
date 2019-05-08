package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import org.jetbrains.annotations.NotNull;

/**
 * Objeto utilizado para encapsular as {@link RestricaoAfericaoProtheusRodalog restrições de aferição}. A restrição
 * consiste em atributos que determinam como a lógica de negócio do ProLog será executada na aferição dos sulcos.
 *
 * <b>Observação 1:</b> Apenas um serviço sobre a pressão poderá ser aberto, assim, se a pressão aferida estiver fora da
 * tolerância para calibragem e para inspeção, apenas o serviço de inspeção será aberto.
 *
 * <b>Observação 2:</b> O ProLog só indicará um serviço de movimentação para o descarte caso a
 * {@link PneuAfericaoProtheusRodalog#vidaAtual} for igual ao total de vidas que esse pneu pode ter.
 * <p>
 * Este objeto será recebido através de um endpoint integrado com o ERP Protheus, da empresa Rodalog. Assim, todas essas
 * informações deverão ser enviadas pelo endpoint seguindo o padrão e estrutura deste objeto.
 * <p>
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * {@see protheusrodalog}
 */
public final class RestricaoAfericaoProtheusRodalog {
    /**
     * {@code toleranciaCalibragem} é a variação tolerada para a medida de pressão dos pneus. Caso a pressão esteja fora
     * da variação tolerada, um <b>Serviço de Calibragem</b> será aberto para que seja executado no pneu.
     */
    private Double toleranciaCalibragem;

    /**
     * {@code toleranciaInspecao} é a variação tolerada para a medida de pressão dos pneus. Caso a pressão esteja fora da
     * variação torelarada, um <b>Serviço de Inspeção</b> será aberto para que seja executado no pneu.
     */
    private Double toleranciaInspecao;

    /**
     * {@code sulcoMinimoRecape} é a medida de sulco mínimo estipulado para os pneus da operação serem recapados. Se o
     * pneu aferido tiver seu sulco menor que o {@code sulcoMinimoRecape} um <b>Serviço de Movimentação</b> será aberto
     * sugerindo a troca deste pneu.
     */
    private Double sulcoMinimoRecape;

    /**
     * {@code sulcoMinimoDescarte} é a medida de sulco mínimo estipulado para os pneus da operação serem descartados. Se
     * o pneu aferido tiver seu sulco menor que o {@code sulcoMinimoDescarte} um <b>Serviço de Movimentação</b> será
     * aberto sugerindo o descarte deste pneu.
     */
    private Double sulcoMinimoDescarte;

    /**
     * {@code periodoDiasAfericaoPressao} é um atributo que indica a cada quantos dias que a pressão do pneu deve ser
     * aferida. Se a última vez que a pressão do pneu foi aferida não estiver dentro dos dias o
     * {@link CronogramaAfericaoProtheusRodalog cronograma} mostrara a placa como <b>vencida</b>.
     */
    private Integer periodoDiasAfericaoPressao;

    /**
     * {@code periodoDiasAfericaoSulco} é um atributo que indica a cada quantos dias que o sulco do pneu deve ser
     * medido. Se a última vez que o sulco do pneu foi medido não estiver dentro dos dias o
     * {@link CronogramaAfericaoProtheusRodalog cronograma} mostrara a placa como <b>vencida</b>.
     */
    private Integer periodoDiasAfericaoSulco;

    public RestricaoAfericaoProtheusRodalog() {
    }

    @NotNull
    static RestricaoAfericaoProtheusRodalog getRestricaoDummy() {
        final RestricaoAfericaoProtheusRodalog restricao = new RestricaoAfericaoProtheusRodalog();
        restricao.setToleranciaCalibragem(10.0);
        restricao.setToleranciaInspecao(30.0);
        restricao.setSulcoMinimoRecape(3.5);
        restricao.setSulcoMinimoDescarte(1.5);
        restricao.setPeriodoDiasAfericaoPressao(7);
        restricao.setPeriodoDiasAfericaoSulco(30);
        return restricao;
    }

    public Double getToleranciaCalibragem() {
        return toleranciaCalibragem;
    }

    public void setToleranciaCalibragem(final Double toleranciaCalibragem) {
        this.toleranciaCalibragem = toleranciaCalibragem;
    }

    public Double getToleranciaInspecao() {
        return toleranciaInspecao;
    }

    public void setToleranciaInspecao(final Double toleranciaInspecao) {
        this.toleranciaInspecao = toleranciaInspecao;
    }

    public Double getSulcoMinimoRecape() {
        return sulcoMinimoRecape;
    }

    public void setSulcoMinimoRecape(final Double sulcoMinimoRecape) {
        this.sulcoMinimoRecape = sulcoMinimoRecape;
    }

    public Double getSulcoMinimoDescarte() {
        return sulcoMinimoDescarte;
    }

    public void setSulcoMinimoDescarte(final Double sulcoMinimoDescarte) {
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
    }

    public Integer getPeriodoDiasAfericaoPressao() {
        return periodoDiasAfericaoPressao;
    }

    public void setPeriodoDiasAfericaoPressao(final Integer periodoDiasAfericaoPressao) {
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
    }

    public Integer getPeriodoDiasAfericaoSulco() {
        return periodoDiasAfericaoSulco;
    }

    public void setPeriodoDiasAfericaoSulco(final Integer periodoDiasAfericaoSulco) {
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
    }
}
