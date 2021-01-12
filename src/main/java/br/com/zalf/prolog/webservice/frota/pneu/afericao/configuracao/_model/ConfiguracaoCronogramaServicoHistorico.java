package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 11/25/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ConfiguracaoCronogramaServicoHistorico {
    /**
     * Nome da Unidade
     * */
    @NotNull
    private final String nomeUnidadeReferente;
    /**
     * Nome da colaborador
     * */
    @Nullable
    private final String nomeColaboradorAjuste;
    /**
     * Data e hora do ajuste
     * */
    @NotNull
    private final LocalDateTime dataHoraAlteracao;
    /**
     * Tolerância da calibragem
     * */
    @NotNull
    private final Double toleranciaCalibragem;
    /**
     * Tolerância de inspeção
     * */
    @NotNull
    private final Double toleranciaInspecao;
    /**
     * Sulco mínimo para recape
     * */
    @NotNull
    private final Double sulcoMinimoRecape;
    /**
     * Sunco mínimo para descarte
     * */
    @NotNull
    private final Double sulcoMinimoDescarte;
    /**
     * Período para aferição de pressão
     * */
    @NotNull
    private final Integer periodoAfericaoPressao;
    /**
     * Período para aferição de sulco
     * */
    @NotNull
    private final Integer periodoAfericaoSulco;
    /**
     * Define se o objeto se trata do registro atual
     */
    @NotNull
    private final Boolean atual;


    public ConfiguracaoCronogramaServicoHistorico(@NotNull final String nomeUnidadeReferente,
                                                  @Nullable final String nomeColaboradorAjuste,
                                                  @NotNull final LocalDateTime dataHoraAlteracao,
                                                  @NotNull final Double toleranciaCalibragem,
                                                  @NotNull final Double toleranciaInspecao,
                                                  @NotNull final Double sulcoMinimoRecape,
                                                  @NotNull final Double sulcoMinimoDescarte,
                                                  @NotNull final Integer periodoAfericaoPressao,
                                                  @NotNull final Integer periodoAfericaoSulco,
                                                  @NotNull final Boolean atual) {
        this.nomeUnidadeReferente = nomeUnidadeReferente;
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
        this.dataHoraAlteracao = dataHoraAlteracao;
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.toleranciaInspecao = toleranciaInspecao;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.periodoAfericaoPressao = periodoAfericaoPressao;
        this.periodoAfericaoSulco = periodoAfericaoSulco;
        this.atual = atual;
    }

    @NotNull
    public static ConfiguracaoCronogramaServicoHistorico getDummy() {
        return new ConfiguracaoCronogramaServicoHistorico(
                "Unidade Teste Zalf",
                "Colaborador teste",
                PrologDateParser.toLocalDateTime("2019-01-10T09:45:00"),
                0.1D,
                0.2D,
                11.1D,
                11.2D,
                15,
                7,
                false
        );
    }

    @NotNull
    public String getNomeUnidadeReferente() { return nomeUnidadeReferente; }

    @Nullable
    public String getNomeColaboradorAjuste() { return nomeColaboradorAjuste; }

    @NotNull
    public LocalDateTime getDataHoraAlteracao() { return dataHoraAlteracao; }

    @NotNull
    public Double getToleranciaCalibragem() { return toleranciaCalibragem; }

    @NotNull
    public Double getToleranciaInspecao() { return toleranciaInspecao; }

    @NotNull
    public Double getSulcoMinimoRecape() { return sulcoMinimoRecape; }

    @NotNull
    public Double getSulcoMinimoDescarte() { return sulcoMinimoDescarte; }

    @NotNull
    public Integer getPeriodoAfericaoPressao() { return periodoAfericaoPressao; }

    @NotNull
    public Integer getPeriodoAfericaoSulco() { return periodoAfericaoSulco; }

    @NotNull
    public Boolean getAtual() { return atual; }
}