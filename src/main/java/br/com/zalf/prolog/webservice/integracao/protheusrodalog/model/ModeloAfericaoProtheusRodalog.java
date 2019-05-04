package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * Este objeto consiste nas informações de cada {@link ModeloAfericaoProtheusRodalog modelo de veículo} disponível para
 * a aferição dentro do {@link CronogramaAfericaoProtheusRodalog cronograma de aferição}.
 * Utilizado apenas para a integração entre ProLog e Protheus, da empresa Rodalog.
 * <p>
 * Todas as informações presentes neste objeto são recebidas através de um endpoint integrado. Assim, é de
 * responsábilidade do endpoint, fornecer as informações necessárias no padrão especificado por este objeto para que a
 * integração funcione corretamente.
 * <p>
 * {@see protheusrodalog}
 */
public final class ModeloAfericaoProtheusRodalog {
    /**
     * Representação do modelo do veículo.
     */
    private String nomeModelo;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoAfericaoProtheusRodalog#SULCO} dentro do prazo estipulado.
     */
    private Integer qtdPlacasSulcoOk;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoAfericaoProtheusRodalog#PRESSAO} dentro do prazo estipulado.
     */
    private Integer qtdPlacasPressaoOk;

    /**
     * Número inteiro que representa a quantidade de placas que estão com a aferição do tipo
     * {@link TipoMedicaoAfericaoProtheusRodalog#SULCO} e a aferiçã do tipo
     * {@link TipoMedicaoAfericaoProtheusRodalog#PRESSAO} dentro do prazo estipulado.
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

    @NotNull
    public static ModeloAfericaoProtheusRodalog getModeloAfericaoDummy() {
        final ModeloAfericaoProtheusRodalog modeloAfericao = new ModeloAfericaoProtheusRodalog();
        modeloAfericao.setNomeModelo("TOCO");
        modeloAfericao.setQtdPlacasSulcoOk(3);
        modeloAfericao.setQtdPlacasPressaoOk(4);
        modeloAfericao.setQtdPlacasSulcoPressaoOk(2);
        modeloAfericao.setTotalPlacasModelo(4);
        final List<PlacaAfericaoProtheusRodalog> placasAfericao = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            placasAfericao.add(PlacaAfericaoProtheusRodalog.getPlacaAfericaoDummy());
        }
        modeloAfericao.setPlacasAfericao(placasAfericao);
        return modeloAfericao;
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
