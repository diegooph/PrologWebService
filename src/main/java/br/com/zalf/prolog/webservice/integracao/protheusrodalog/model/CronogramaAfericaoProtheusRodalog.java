package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * O objeto {@link CronogramaAfericaoProtheusRodalog Cronograma de Aferição} compoẽm as informações necessárias para
 * montar a tela de cronograma no Aplicativo do ProLog.
 * Este objeto em específico é utilizado apenas para a integração entre o ProLog e o ERP Protheus, da Empresa Rodalog.
 * As informações presentes neste objeto não serão providas pelo ProLog, todas serão buscadas em um endpoint integrado.
 * É de responsabilidade deste endpoint, prover todas as informações, na estrutura estabelecida por este objeto.
 * <p>
 * Utilizamos uma classe específica para não termos dependências entre diferentes integrações, assim, se outras
 * empresas necessitarem de um tratamento diferente, um tipo diferentes de atributo, não esbarramos na complexidade
 * de ter que manter compatibilidade entre várias integrações.
 * Evitamos também o grau de impacto de pequenas alterações. Uma melhoria aplicada para a integração da empresa Rodalog
 * ficará restrita aos objetos criados para a integração desta empresa.
 * <p>
 * {@see protheusrodalog}
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

    @NotNull
    public static CronogramaAfericaoProtheusRodalog createCronogramaDummy() {
        final CronogramaAfericaoProtheusRodalog cronograma = new CronogramaAfericaoProtheusRodalog();
        cronograma.setMetaDiasAfericaoPressao(7);
        cronograma.setMetaDiasAfericaoSulco(30);
        cronograma.setTotalPlacasSulcosOk(5);
        cronograma.setTotalPlacasPressaoOk(6);
        cronograma.setTotalPlacasSulcoPressaoOk(4);
        cronograma.setTotalPlacas(7);
        final List<ModeloAfericaoProtheusRodalog> modelosPlacasAfericao = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            modelosPlacasAfericao.add(ModeloAfericaoProtheusRodalog.getModeloAfericaoDummy());
        }
        cronograma.setModelosPlacasAfericao(modelosPlacasAfericao);
        return cronograma;
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
