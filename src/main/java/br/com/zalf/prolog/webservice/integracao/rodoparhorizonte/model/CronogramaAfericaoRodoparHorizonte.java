package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * O objeto {@link CronogramaAfericaoRodoparHorizonte Cronograma de Aferição} compoẽm as informações necessárias para
 * montar a tela de cronograma no Aplicativo do ProLog.
 * <p>
 * As informações presentes neste objeto não serão providas pelo ProLog, todas serão buscadas em um endpoint integrado.
 * É de responsabilidade deste endpoint, prover todas as informações, na estrutura estabelecida por este objeto.
 * <p>
 * Utilizamos uma classe específica para não termos dependências entre diferentes integrações, assim, se outras
 * empresas necessitarem de um tratamento diferente, um tipo diferente de atributo, não esbarramos na complexidade
 * de ter que manter compatibilidade entre várias integrações.
 * <p>
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class CronogramaAfericaoRodoparHorizonte {
    /**
     * Número inteiro que representa a quantidade de placas listadas no cronograma.
     */
    @NotNull
    private final Integer totalPlacas;
    /**
     * Lista de {@link ModeloAfericaoRodoparHorizonte modelos} que possuem placas que podem ser aferidas.
     */
    @NotNull
    private final List<ModeloAfericaoRodoparHorizonte> modelosPlacasAfericao;

    public CronogramaAfericaoRodoparHorizonte(
            @NotNull final Integer totalPlacas,
            @NotNull final List<ModeloAfericaoRodoparHorizonte> modelosPlacasAfericao) {
        this.totalPlacas = totalPlacas;
        this.modelosPlacasAfericao = modelosPlacasAfericao;
    }

    @NotNull
    public Integer getTotalPlacas() {
        return totalPlacas;
    }

    @NotNull
    public List<ModeloAfericaoRodoparHorizonte> getModelosPlacasAfericao() {
        return modelosPlacasAfericao;
    }
}
