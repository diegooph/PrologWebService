package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloChecklistVisualizacao {
    private Long codigo;
    private Long codUnidade;
    private String nome;
    private List<TipoVeiculo> tiposVeiculoLiberados;
    private List<Cargo> cargosLiberados;
    private List<PerguntaModeloChecklistVisualizacao> perguntas;
    private boolean ativo;

    public ModeloChecklistVisualizacao() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public List<TipoVeiculo> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    public void setTiposVeiculoLiberados(final List<TipoVeiculo> tiposVeiculoLiberados) {
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
    }

    public List<Cargo> getCargosLiberados() {
        return cargosLiberados;
    }

    public void setCargosLiberados(final List<Cargo> cargosLiberados) {
        this.cargosLiberados = cargosLiberados;
    }

    public List<PerguntaModeloChecklistVisualizacao> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(final List<PerguntaModeloChecklistVisualizacao> perguntas) {
        this.perguntas = perguntas;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }
}