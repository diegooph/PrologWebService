package br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao;

import java.util.Set;

/**
 * Created on 14/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ModeloChecklistListagem {
    private Long codigo;
    private String nome;
    private Long codUnidade;
    private Set<String> tiposVeiculoLiberados;
    private Set<String> cargosLiberados;
    private int qtdPerguntas;
    private boolean ativo;

    public ModeloChecklistListagem() {

    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
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

    public Set<String> getTiposVeiculoLiberados() {
        return tiposVeiculoLiberados;
    }

    public void setTiposVeiculoLiberados(final Set<String> tiposVeiculoLiberados) {
        this.tiposVeiculoLiberados = tiposVeiculoLiberados;
    }

    public Set<String> getCargosLiberados() {
        return cargosLiberados;
    }

    public void setCargosLiberados(final Set<String> cargosLiberados) {
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
                ", codUnidade=" + codUnidade +
                ", nome='" + nome + '\'' +
                ", tiposVeiculoLiberados=" + tiposVeiculoLiberados +
                ", cargosLiberados=" + cargosLiberados +
                ", qtdPerguntas=" + qtdPerguntas +
                ", ativo=" + ativo +
                '}';
    }
}