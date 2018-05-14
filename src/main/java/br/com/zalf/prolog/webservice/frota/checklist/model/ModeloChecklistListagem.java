package br.com.zalf.prolog.webservice.frota.checklist.model;

import java.util.List;

/**
 * Created on 14/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ModeloChecklistListagem {
    private Long codigo;
    private Long codUnidade;
    private String nome;
    private List<String> tiposVeiculoLiberados;
    private List<String> cargosLiberados;
    private int qtdPerguntas;

    public ModeloChecklistListagem() {
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

    public List<String> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    public void setTiposVeiculoLiberados(final List<String> tiposVeiculoLiberados) {
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
    }

    public List<String> getCargosLiberados() {
        return cargosLiberados;
    }

    public void setCargosLiberados(final List<String> cargosLiberados) {
        this.cargosLiberados = cargosLiberados;
    }

    public int getQtdPerguntas() {
        return qtdPerguntas;
    }

    public void setQtdPerguntas(final int qtdPerguntas) {
        this.qtdPerguntas = qtdPerguntas;
    }

    @Override
    public String toString() {
        return "ModeloChecklistListagem{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", tiposVeiculoLiberados=" + tiposVeiculoLiberados +
                ", cargosLiberados=" + cargosLiberados +
                ", qtdPerguntas=" + qtdPerguntas +
                '}';
    }
}
