package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model;

import java.util.List;

/**
 * Created on 27/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModelo {

    private long codigo;
    private String nome;
    private List<PneuModeloVisualizacao> modelos;

    public PneuMarcaModelo() {
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<PneuModeloVisualizacao> getModelos() {
        return modelos;
    }

    public void setModelos(List<PneuModeloVisualizacao> modelos) {
        this.modelos = modelos;
    }

    @Override
    public String toString() {
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", modelos=" + modelos +
                '}';
    }
}
