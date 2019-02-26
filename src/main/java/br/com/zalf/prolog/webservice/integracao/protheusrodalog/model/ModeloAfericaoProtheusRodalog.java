package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;

import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloAfericaoProtheusRodalog {
    /**
     * Representação do modelo do veículo.
     */
    private String nomeModelo;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#SULCO} dentro do prazo estipulado.
     */
    private Integer qtdPlacasSulcoOk;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#PRESSAO} dentro do prazo estipulado.
     */
    private Integer qtdPlacasPressaoOk;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoColetadaAfericao#SULCO} e a aferiçã do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}
     * dentro do prazo estipulado.
     */
    private Integer qtdPlacasSulcoPressaoOk;

    /**
     * Número inteiro que representa a quantidade de placas que estão associadas à este modelo.
     */
    private Integer totalPlacasModelo;

    /**
     * Lista de {@link PlacaAfericaoProtheusRodalog placas} que pertencem a este modelo de veículo.
     */
    private List<PlacaAfericaoProtheusRodalog> placasAfericao;

    public ModeloAfericaoProtheusRodalog() {
    }

    public String getNomeModelo() {
        return nomeModelo;
    }

    public void setNomeModelo(final String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

    public Integer getQtdPlacasSulcoOk() {
        return qtdPlacasSulcoOk;
    }

    public void setQtdPlacasSulcoOk(final Integer qtdPlacasSulcoOk) {
        this.qtdPlacasSulcoOk = qtdPlacasSulcoOk;
    }

    public Integer getQtdPlacasPressaoOk() {
        return qtdPlacasPressaoOk;
    }

    public void setQtdPlacasPressaoOk(final Integer qtdPlacasPressaoOk) {
        this.qtdPlacasPressaoOk = qtdPlacasPressaoOk;
    }

    public Integer getQtdPlacasSulcoPressaoOk() {
        return qtdPlacasSulcoPressaoOk;
    }

    public void setQtdPlacasSulcoPressaoOk(final Integer qtdPlacasSulcoPressaoOk) {
        this.qtdPlacasSulcoPressaoOk = qtdPlacasSulcoPressaoOk;
    }

    public Integer getTotalPlacasModelo() {
        return totalPlacasModelo;
    }

    public void setTotalPlacasModelo(final Integer totalPlacasModelo) {
        this.totalPlacasModelo = totalPlacasModelo;
    }

    public List<PlacaAfericaoProtheusRodalog> getPlacasAfericao() {
        return placasAfericao;
    }

    public void setPlacasAfericao(final List<PlacaAfericaoProtheusRodalog> placasAfericao) {
        this.placasAfericao = placasAfericao;
    }
}
