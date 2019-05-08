package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import org.jetbrains.annotations.NotNull;

/**
 * Objeto responsável por conter as {@link MedicaoAfericaoProtheusRodalog medidas} capturadas em um pneu através do
 * processo de aferição de placa, do ProLog.
 * <p>
 * As medições coletadas serão enviadas à um endpoint integrado, o qual deverá estar preparado para ler os atributos
 * deste objeto seguindo esta estrutura.
 * <p>
 * Created on 27/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * {@see protheusrodalog}
 */
public final class MedicaoAfericaoProtheusRodalog {
    /**
     * Atributo alfanumérico que representa o código pelo qual o usuário identifica o pneu. Normalmente costuma-se
     * utilizar o código de fogo do pneu para identificá-lo.
     */
    private String codigoCliente;

    /**
     * Código único de identificação do pneu no banco de dados.
     */
    private Long codigo;

    /**
     * Número inteiro que representa a vida atual do pneu. Vida do pneu pode ser interpretada como a quantidade de
     * recapes que o pneu já sofreu.
     */
    private Integer vidaAtual;

    /**
     * Medida de pressão coletada pelo processo de medição. Este valor representa a pressão do pneu em PSI.
     */
    private Double pressaoAtual;

    /**
     * Medida do sulco interno do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    private Double sulcoInterno;

    /**
     * Medida do sulco central interno do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     */
    private Double sulcoCentralInterno;

    /**
     * Medida do sulco central externo do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     * <p>
     * Caso o pneu possua apenas 3 sulcos, este valor será igual ao {@code sulcoCentralInterno}.
     */
    private Double sulcoCentralExterno;

    /**
     * Medida do sulco externo do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    private Double sulcoExterno;

    public MedicaoAfericaoProtheusRodalog() {
    }

    @NotNull
    static MedicaoAfericaoProtheusRodalog getMedicaoDummy() {
        final MedicaoAfericaoProtheusRodalog medida = new MedicaoAfericaoProtheusRodalog();
        medida.setCodigoCliente("PN01");
        medida.setCodigo(1L);
        medida.setVidaAtual(2);
        medida.setPressaoAtual(110.0);
        medida.setSulcoInterno(15.4);
        medida.setSulcoCentralInterno(15.5);
        medida.setSulcoCentralExterno(15.6);
        medida.setSulcoExterno(15.7);
        return medida;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(final String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Integer getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(final Integer vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    public void setPressaoAtual(final Double pressaoAtual) {
        this.pressaoAtual = pressaoAtual;
    }

    public Double getSulcoInterno() {
        return sulcoInterno;
    }

    public void setSulcoInterno(final Double sulcoInterno) {
        this.sulcoInterno = sulcoInterno;
    }

    public Double getSulcoCentralInterno() {
        return sulcoCentralInterno;
    }

    public void setSulcoCentralInterno(final Double sulcoCentralInterno) {
        this.sulcoCentralInterno = sulcoCentralInterno;
    }

    public Double getSulcoCentralExterno() {
        return sulcoCentralExterno;
    }

    public void setSulcoCentralExterno(final Double sulcoCentralExterno) {
        this.sulcoCentralExterno = sulcoCentralExterno;
    }

    public Double getSulcoExterno() {
        return sulcoExterno;
    }

    public void setSulcoExterno(final Double sulcoExterno) {
        this.sulcoExterno = sulcoExterno;
    }
}
