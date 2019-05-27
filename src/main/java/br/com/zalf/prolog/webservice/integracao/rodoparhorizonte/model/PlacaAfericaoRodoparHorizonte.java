package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

/**
 * Neste objeto estão as informações referentes às {@link PlacaAfericaoRodoparHorizonte placas} que estão listadas no
 * {@link CronogramaAfericaoRodoparHorizonte cronograma de aferição}, bem como algumas informações extras para montar
 * o cronograma corretamente no Aplicativo.
 * <p>
 * Todas as informações disponíveis neste objeto serão providas através de um endpoint integrado, e é de total
 * responsabilidade do endpoint prover as informações seguindo o padrão e estrutura deste objeto.
 * <p>
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PlacaAfericaoRodoparHorizonte {
    /**
     * Representação da placa do veículo.
     */
    @NotNull
    private final String placa;
    /**
     * Indica quantos pneus estão vinculados a esse veículo.
     */
    @NotNull
    private final Integer qtdPneusAplicados;
    /**
     * Data e hora que a última aferição ocorreu nessa placa.
     * <p>
     * Esse valor se apresenta e UTC, exemplo: "2019-02-21T15:30:00-03:00"
     */
    @NotNull
    private final OffsetDateTime dataHoraUltimaAfericaoUtc;

    public PlacaAfericaoRodoparHorizonte(@NotNull final String placa,
                                         @NotNull final Integer qtdPneusAplicados,
                                         @NotNull final OffsetDateTime dataHoraUltimaAfericaoUtc) {
        this.placa = placa;
        this.qtdPneusAplicados = qtdPneusAplicados;
        this.dataHoraUltimaAfericaoUtc = dataHoraUltimaAfericaoUtc;
    }

    @NotNull
    public String getPlaca() {
        return placa;
    }

    @NotNull
    public Integer getQtdPneusAplicados() {
        return qtdPneusAplicados;
    }

    @NotNull
    public OffsetDateTime getDataHoraUltimaAfericaoUtc() {
        return dataHoraUltimaAfericaoUtc;
    }
}
