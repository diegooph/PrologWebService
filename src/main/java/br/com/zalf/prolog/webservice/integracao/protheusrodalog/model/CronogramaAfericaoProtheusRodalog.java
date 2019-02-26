package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;

import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class CronogramaAfericaoProtheusRodalog {
    /**
     * Número inteiro que representa a cada quantos dias a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#SULCO} deve ser realizada.
     */
    private Integer metaDiasAfericaoSulco;

    /**
     * Número inteiro que representa a cada quantos dias a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#PRESSAO} deve ser realizada.
     */
    private Integer metaDiasAfericaoPressao;

    /**
     * Número inteiro que representa o total de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#SULCO} dentro do prazo estipulado.
     */
    private Integer totalPlacasSulcosOk;

    /**
     * Número inteiro que representa o total de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#PRESSAO} dentro do prazo estipulado.
     */
    private Integer totalPlacasPressaoOk;

    /**
     * Número inteiro que representa o total de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#SULCO} e aferição do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}
     * dentro do prazo estipulado.
     */
    private Integer totalPlacasSulcoPressaoOk;

    /**
     * Número inteiro que representa a quantidade de placas listadas no cronograma.
     */
    private Integer totalPlacas;

    /**
     * Lista de {@link ModeloAfericaoProtheusRodalog modelos} que possuem placas que podem ser aferidas.
     */
    private List<ModeloAfericaoProtheusRodalog> modelosPlacasAfericao;

    public CronogramaAfericaoProtheusRodalog() {
    }

    public Integer getMetaDiasAfericaoSulco() {
        return metaDiasAfericaoSulco;
    }

    public void setMetaDiasAfericaoSulco(final Integer metaDiasAfericaoSulco) {
        this.metaDiasAfericaoSulco = metaDiasAfericaoSulco;
    }

    public Integer getMetaDiasAfericaoPressao() {
        return metaDiasAfericaoPressao;
    }

    public void setMetaDiasAfericaoPressao(final Integer metaDiasAfericaoPressao) {
        this.metaDiasAfericaoPressao = metaDiasAfericaoPressao;
    }

    public Integer getTotalPlacasSulcosOk() {
        return totalPlacasSulcosOk;
    }

    public void setTotalPlacasSulcosOk(final Integer totalPlacasSulcosOk) {
        this.totalPlacasSulcosOk = totalPlacasSulcosOk;
    }

    public Integer getTotalPlacasPressaoOk() {
        return totalPlacasPressaoOk;
    }

    public void setTotalPlacasPressaoOk(final Integer totalPlacasPressaoOk) {
        this.totalPlacasPressaoOk = totalPlacasPressaoOk;
    }

    public Integer getTotalPlacasSulcoPressaoOk() {
        return totalPlacasSulcoPressaoOk;
    }

    public void setTotalPlacasSulcoPressaoOk(final Integer totalPlacasSulcoPressaoOk) {
        this.totalPlacasSulcoPressaoOk = totalPlacasSulcoPressaoOk;
    }

    public Integer getTotalPlacas() {
        return totalPlacas;
    }

    public void setTotalPlacas(final Integer totalPlacas) {
        this.totalPlacas = totalPlacas;
    }

    public List<ModeloAfericaoProtheusRodalog> getModelosPlacasAfericao() {
        return modelosPlacasAfericao;
    }

    public void setModelosPlacasAfericao(final List<ModeloAfericaoProtheusRodalog> modelosPlacasAfericao) {
        this.modelosPlacasAfericao = modelosPlacasAfericao;
    }
}
