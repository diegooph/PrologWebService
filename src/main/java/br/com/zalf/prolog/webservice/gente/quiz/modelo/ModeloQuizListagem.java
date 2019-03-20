package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloQuizListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Set<String> cargosLiberados;
    private final double porcentagemAprovacao;
    private final int qtdPerguntas;
    private final boolean temMaterialApoio;
    private final boolean estaAbertoParaRealizacao;

    @NotNull
    public static ModeloQuizListagem createDummy(final boolean temMaterialApoio,
                                                 final boolean estaAbertoParaRealizacao) {
        final Set<String> cargosLiberados = new HashSet<>();
        cargosLiberados.add("Motorista");
        cargosLiberados.add("Ajudante");
        cargosLiberados.add("Carregador");
        cargosLiberados.add("Gerente");
        cargosLiberados.add("Supervisor RH");
        return new ModeloQuizListagem(
                1L,
                "Modelo Teste",
                5L,
                cargosLiberados,
                0.8,
                15,
                temMaterialApoio,
                estaAbertoParaRealizacao);
    }

    public ModeloQuizListagem(@NotNull final Long codigo,
                              @NotNull final String nome,
                              @NotNull final Long codUnidade,
                              @NotNull final Set<String> cargosLiberados,
                              final double porcentagemAprovacao,
                              final int qtdPerguntas,
                              final boolean temMaterialApoio,
                              final boolean estaAbertoParaRealizacao) {
        this.codigo = codigo;
        this.nome = nome;
        this.codUnidade = codUnidade;
        this.cargosLiberados = cargosLiberados;
        this.porcentagemAprovacao = porcentagemAprovacao;
        this.qtdPerguntas = qtdPerguntas;
        this.temMaterialApoio = temMaterialApoio;
        this.estaAbertoParaRealizacao = estaAbertoParaRealizacao;
    }


    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Set<String> getCargosLiberados() {
        return cargosLiberados;
    }

    public double getPorcentagemAprovacao() {
        return porcentagemAprovacao;
    }

    public int getQtdPerguntas() {
        return qtdPerguntas;
    }

    public boolean isTemMaterialApoio() {
        return temMaterialApoio;
    }

    public boolean isEstaAbertoParaRealizacao() {
        return estaAbertoParaRealizacao;
    }
}