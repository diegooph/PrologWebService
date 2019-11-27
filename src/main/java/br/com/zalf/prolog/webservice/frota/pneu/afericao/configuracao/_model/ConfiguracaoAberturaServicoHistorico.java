package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/25/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ConfiguracaoAberturaServicoHistorico {
    /**
     * Nome da Unidade
     * */
    @NotNull
    private final String nomeUnidadeReferente;
    /**
     * Nome da colaborador
     * */
    @NotNull
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


    public ConfiguracaoAberturaServicoHistorico(@NotNull final String nomeUnidadeReferente,
                                                @NotNull final String nomeColaboradorAjuste,
                                                @NotNull final LocalDateTime dataHoraAlteracao,
                                                @NotNull final Double toleranciaCalibragem,
                                                @NotNull final Double toleranciaInspecao,
                                                @NotNull final Double sulcoMinimoRecape,
                                                @NotNull final Double sulcoMinimoDescarte,
                                                @NotNull final Integer periodoAfericaoPressao,
                                                @NotNull final Integer periodoAfericaoSulco) {
        this.nomeUnidadeReferente = nomeUnidadeReferente;
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
        this.dataHoraAlteracao = dataHoraAlteracao;
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.toleranciaInspecao = toleranciaInspecao;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.periodoAfericaoPressao = periodoAfericaoPressao;
        this.periodoAfericaoSulco = periodoAfericaoSulco;
    }

    @NotNull
    public static ConfiguracaoAberturaServicoHistorico getDummy() {
        return new ConfiguracaoAberturaServicoHistorico(
                "Unidade Teste Zalf",
                "Colaborador teste",
                ProLogDateParser.toLocalDateTime("2019-01-10T09:45:00"),
                0.1D,
                0.2D,
                11.1D,
                11.2D,
                15,
                7
        );
    }

    @NotNull
    public String getNomeUnidadeReferente() { return nomeUnidadeReferente; }

    @NotNull
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
}