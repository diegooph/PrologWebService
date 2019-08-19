package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloChecklistEdicao {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codVersaoModelo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<Long> tiposVeiculoLiberados;
    @NotNull
    private final List<Long> cargosLiberados;
    @NotNull
    private final List<PerguntaModeloChecklistEdicao> perguntas;
    private final boolean ativo;

    private final boolean criarNovaVersao;

    public ModeloChecklistEdicao(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final String nome,
            @NotNull final List<Long> tiposVeiculoLiberados,
            @NotNull final List<Long> cargosLiberados,
            @NotNull final List<PerguntaModeloChecklistEdicao> perguntas,
            final boolean ativo,
            final boolean criarNovaVersao) {
        this.codUnidade = codUnidade;
        this.codModelo = codModelo;
        this.codVersaoModelo = codVersaoModelo;
        this.nome = nome;
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
        this.cargosLiberados = cargosLiberados;
        this.perguntas = perguntas;
        this.ativo = ativo;
        this.criarNovaVersao = criarNovaVersao;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<Long> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    @NotNull
    public List<Long> getCargosLiberados() {
        return cargosLiberados;
    }

    @NotNull
    public List<PerguntaModeloChecklistEdicao> getPerguntas() {
        return perguntas;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isCriarNovaVersao() {
        return criarNovaVersao;
    }
}