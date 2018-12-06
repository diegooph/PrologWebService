package br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ModeloChecklistInsercao {
    private Long codigo;
    private String nome;
    private Long codUnidade;
    private List<Long> tiposVeiculoLiberados;
    private List<Long> cargosLiberados;
    private List<PerguntaModeloChecklist> perguntas;

    public ModeloChecklistInsercao() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public List<Long> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    public void setTiposVeiculoLiberados(final List<Long> tiposVeiculoLiberados) {
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
    }

    public List<Long> getCargosLiberados() {
        return cargosLiberados;
    }

    public void setCargosLiberados(final List<Long> cargosLiberados) {
        this.cargosLiberados = cargosLiberados;
    }

    public List<PerguntaModeloChecklist> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(final List<PerguntaModeloChecklist> perguntas) {
        this.perguntas = perguntas;
    }
}
