package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloChecklistVisualizacao {
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codVersaoModelo;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nome;
    @NotNull
    private final List<TipoVeiculo> tiposVeiculoLiberados;
    @NotNull
    private final List<Cargo> cargosLiberados;
    @NotNull
    private final List<PerguntaModeloChecklistVisualizacao> perguntas;
    private final boolean ativo;

    public ModeloChecklistVisualizacao(@NotNull final Long codModelo,
                                       @NotNull final Long codVersaoModelo,
                                       @NotNull final Long codUnidade,
                                       @NotNull final String nome,
                                       @NotNull final List<TipoVeiculo> tiposVeiculoLiberados,
                                       @NotNull final List<Cargo> cargosLiberados,
                                       @NotNull final List<PerguntaModeloChecklistVisualizacao> perguntas,
                                       final boolean ativo) {
        this.codModelo = codModelo;
        this.codVersaoModelo = codVersaoModelo;
        this.codUnidade = codUnidade;
        this.nome = nome;
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
        this.cargosLiberados = cargosLiberados;
        this.perguntas = perguntas;
        this.ativo = ativo;
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
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<TipoVeiculo> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    @NotNull
    public List<Cargo> getCargosLiberados() {
        return cargosLiberados;
    }

    @NotNull
    public List<PerguntaModeloChecklistVisualizacao> getPerguntas() {
        return perguntas;
    }

    public boolean isAtivo() {
        return ativo;
    }
}