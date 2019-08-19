package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloChecklistInsercao {
    @NotNull
    private final String nome;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final List<Long> tiposVeiculoLiberados;
    @NotNull
    private final List<Long> cargosLiberados;
    @NotNull
    private final List<PerguntaModeloChecklistInsercao> perguntas;

    public ModeloChecklistInsercao(@NotNull final String nome,
                                   @NotNull final Long codUnidade,
                                   @NotNull final List<Long> tiposVeiculoLiberados,
                                   @NotNull final List<Long> cargosLiberados,
                                   @NotNull final List<PerguntaModeloChecklistInsercao> perguntas) {
        this.nome = nome;
        this.codUnidade = codUnidade;
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
        this.cargosLiberados = cargosLiberados;
        this.perguntas = perguntas;
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
    public List<Long> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    @NotNull
    public List<Long> getCargosLiberados() {
        return cargosLiberados;
    }

    @NotNull
    public List<PerguntaModeloChecklistInsercao> getPerguntas() {
        return perguntas;
    }
}