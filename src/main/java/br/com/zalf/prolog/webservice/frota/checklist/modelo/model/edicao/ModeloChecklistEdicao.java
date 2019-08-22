package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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
    private final boolean ativo;

    private final boolean criarNovaVersao;
    @Nullable
    private final List<PerguntaModeloChecklistEdicao> perguntasNovaVersao;

    @Nullable
    private final List<PerguntaAlteracaoDescricao> perguntasAlteracaoDescricao;
    @Nullable
    private final List<AlternativaAlteracaoDescricao> alternativasAlteracaoDescricao;
    @Nullable
    private final Map<Long, Integer> perguntasAlteracaoOrdemExibicao;
    @Nullable
    private final Map<Long, Integer> alternativasAlteracaoOrdemExibicao;

    public ModeloChecklistEdicao(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final String nome,
            @NotNull final List<Long> tiposVeiculoLiberados,
            @NotNull final List<Long> cargosLiberados,
            final boolean ativo,
            final boolean criarNovaVersao,
            @Nullable final List<PerguntaModeloChecklistEdicao> perguntasNovaVersao,
            @Nullable final List<PerguntaAlteracaoDescricao> perguntasAlteracaoDescricao,
            @Nullable final List<AlternativaAlteracaoDescricao> alternativasAlteracaoDescricao,
            @Nullable final Map<Long, Integer> perguntasAlteracaoOrdemExibicao,
            @Nullable final Map<Long, Integer> alternativasAlteracaoOrdemExibicao) {
        this.codUnidade = codUnidade;
        this.codModelo = codModelo;
        this.codVersaoModelo = codVersaoModelo;
        this.nome = nome;
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
        this.cargosLiberados = cargosLiberados;
        this.ativo = ativo;
        this.criarNovaVersao = criarNovaVersao;
        this.perguntasNovaVersao = perguntasNovaVersao;
        this.perguntasAlteracaoDescricao = perguntasAlteracaoDescricao;
        this.alternativasAlteracaoDescricao = alternativasAlteracaoDescricao;
        this.perguntasAlteracaoOrdemExibicao = perguntasAlteracaoOrdemExibicao;
        this.alternativasAlteracaoOrdemExibicao = alternativasAlteracaoOrdemExibicao;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodVersaoModelo() {
        return codVersaoModelo;
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

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isCriarNovaVersao() {
        return criarNovaVersao;
    }

    @Nullable
    public List<PerguntaModeloChecklistEdicao> getPerguntasNovaVersao() {
        return perguntasNovaVersao;
    }

    @Nullable
    public List<PerguntaAlteracaoDescricao> getPerguntasAlteracaoDescricao() {
        return perguntasAlteracaoDescricao;
    }

    @Nullable
    public List<AlternativaAlteracaoDescricao> getAlternativasAlteracaoDescricao() {
        return alternativasAlteracaoDescricao;
    }

    @Nullable
    public Map<Long, Integer> getPerguntasAlteracaoOrdemExibicao() {
        return perguntasAlteracaoOrdemExibicao;
    }

    @Nullable
    public Map<Long, Integer> getAlternativasAlteracaoOrdemExibicao() {
        return alternativasAlteracaoOrdemExibicao;
    }
}