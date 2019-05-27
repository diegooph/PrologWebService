package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MedicaoAfericaoRodoparHorizonte {
    /**
     * Atributo alfanumérico que representa o código pelo qual o usuário identifica o pneu. Normalmente costuma-se
     * utilizar o código de fogo do pneu para identificá-lo.
     */
    @NotNull
    private final String codigoCliente;
    /**
     * Código único de identificação do pneu no banco de dados.
     */
    @NotNull
    private final Long codigo;
    /**
     * Número inteiro que representa a vida atual do pneu. Vida do pneu pode ser interpretada como a quantidade de
     * recapes que o pneu já sofreu.
     * <p>
     * Para o ProLog o pneu quando novo tem {@code vidaAtual vida atual} igual a UM ({@code vidaAtual == 1}).
     */
    @NotNull
    private final Integer vidaAtual;
    /**
     * Medida de pressão coletada pelo processo de medição. Este valor representa a pressão do pneu em PSI.
     */
    @NotNull
    private final Double pressaoAtual;
    /**
     * Medida do sulco interno do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    @NotNull
    private final Double sulcoInterno;
    /**
     * Medida do sulco central interno do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     */
    @NotNull
    private final Double sulcoCentralInterno;
    /**
     * Medida do sulco central externo do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     * <p>
     * Caso o pneu possua apenas 3 sulcos, este valor será igual ao {@code sulcoCentralInterno}.
     */
    @NotNull
    private final Double sulcoCentralExterno;
    /**
     * Medida do sulco externo do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    @NotNull
    private final Double sulcoExterno;

    public MedicaoAfericaoRodoparHorizonte(@NotNull final String codigoCliente,
                                           @NotNull final Long codigo,
                                           @NotNull final Integer vidaAtual,
                                           @NotNull final Double pressaoAtual,
                                           @NotNull final Double sulcoInterno,
                                           @NotNull final Double sulcoCentralInterno,
                                           @NotNull final Double sulcoCentralExterno,
                                           @NotNull final Double sulcoExterno) {
        this.codigoCliente = codigoCliente;
        this.codigo = codigo;
        this.vidaAtual = vidaAtual;
        this.pressaoAtual = pressaoAtual;
        this.sulcoInterno = sulcoInterno;
        this.sulcoCentralInterno = sulcoCentralInterno;
        this.sulcoCentralExterno = sulcoCentralExterno;
        this.sulcoExterno = sulcoExterno;
    }

    @NotNull
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Integer getVidaAtual() {
        return vidaAtual;
    }

    @NotNull
    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    @NotNull
    public Double getSulcoInterno() {
        return sulcoInterno;
    }

    @NotNull
    public Double getSulcoCentralInterno() {
        return sulcoCentralInterno;
    }

    @NotNull
    public Double getSulcoCentralExterno() {
        return sulcoCentralExterno;
    }

    @NotNull
    public Double getSulcoExterno() {
        return sulcoExterno;
    }
}
