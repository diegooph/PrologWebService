package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

import java.util.List;

/**
 * Created on 01/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuMarcaBandas {
    private long codigo;
    private String nome;
    private List<PneuModeloBandaVisualizacao> modelos;

    public PneuMarcaBandas() {
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

    public List<PneuModeloBandaVisualizacao> getModelos() {
        return modelos;
    }

    public void setModelos(List<PneuModeloBandaVisualizacao> modelos) {
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
