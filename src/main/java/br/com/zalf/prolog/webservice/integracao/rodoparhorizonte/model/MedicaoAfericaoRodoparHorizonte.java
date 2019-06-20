package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MedicaoAfericaoRodoparHorizonte {
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
     * Número inteiro que representa a vida atual do pneu.
     * <p>
     * Para o ProLog o pneu quando novo tem {@code vidaAtual vida atual} igual a UM ({@code vidaAtual == 1}).
     */
    @NotNull
    private final Integer vidaAtual;
    /**
     * Medida de pressão coletada pelo processo de medição. Este valor representa a pressão do pneu em PSI.
     * <p>
     * Essa medida será <code>NULL</code> somente se o {@link TipoMedicaoAfericaoRodoparHorizonte#SULCO}.
     */
    @Nullable
    private final Double pressaoAtual;
    /**
     * Medida do sulco interno do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     * <p>
     * Essa medida será <code>NULL</code> somente se o {@link TipoMedicaoAfericaoRodoparHorizonte#PRESSAO}.
     */
    @Nullable
    private final Double sulcoInterno;
    /**
     * Medida do sulco central interno do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     * <p>
     * Essa medida será <code>NULL</code> somente se o {@link TipoMedicaoAfericaoRodoparHorizonte#PRESSAO}.
     */
    @Nullable
    private final Double sulcoCentralInterno;
    /**
     * Medida do sulco central externo do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     * <p>
     * Caso o pneu possua apenas 3 sulcos, este valor será igual ao {@code sulcoCentralInterno}.
     * <p>
     * Essa medida será <code>NULL</code> somente se o {@link TipoMedicaoAfericaoRodoparHorizonte#PRESSAO}.
     */
    @Nullable
    private final Double sulcoCentralExterno;
    /**
     * Medida do sulco externo do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     * <p>
     * Essa medida será <code>NULL</code> somente se o {@link TipoMedicaoAfericaoRodoparHorizonte#PRESSAO}.
     */
    @Nullable
    private final Double sulcoExterno;

    public MedicaoAfericaoRodoparHorizonte(@NotNull final String codigoCliente,
                                           @NotNull final Long codigo,
                                           @NotNull final Integer vidaAtual,
                                           @Nullable final Double pressaoAtual,
                                           @Nullable final Double sulcoInterno,
                                           @Nullable final Double sulcoCentralInterno,
                                           @Nullable final Double sulcoCentralExterno,
                                           @Nullable final Double sulcoExterno) {
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
    public static MedicaoAfericaoRodoparHorizonte getDummy(
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoMedicaoAfericao) {
        if (tipoMedicaoAfericao.equals(TipoMedicaoAfericaoRodoparHorizonte.PRESSAO)) {
            return new MedicaoAfericaoRodoparHorizonte(
                    "PN001",
                    12345L,
                    2,
                    110.0,
                    null,
                    null,
                    null,
                    null);
        } else if (tipoMedicaoAfericao.equals(TipoMedicaoAfericaoRodoparHorizonte.SULCO)) {
            return new MedicaoAfericaoRodoparHorizonte(
                    "PN001",
                    12345L,
                    2,
                    null,
                    12.0,
                    11.8,
                    11.9,
                    11.9);
        } else {
            return new MedicaoAfericaoRodoparHorizonte(
                    "PN001",
                    12345L,
                    2,
                    110.0,
                    12.0,
                    11.8,
                    11.9,
                    11.9);
        }
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

    @Nullable
    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    @Nullable
    public Double getSulcoInterno() {
        return sulcoInterno;
    }

    @Nullable
    public Double getSulcoCentralInterno() {
        return sulcoCentralInterno;
    }

    @Nullable
    public Double getSulcoCentralExterno() {
        return sulcoCentralExterno;
    }

    @Nullable
    public Double getSulcoExterno() {
        return sulcoExterno;
    }
}
