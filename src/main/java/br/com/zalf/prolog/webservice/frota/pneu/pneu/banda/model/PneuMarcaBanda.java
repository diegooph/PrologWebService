package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model;

/**
 * Created on 09/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuMarcaBanda {
    private Long codigo;
    private String nome;
    private PneuModeloBandaVisualizacao modelo;

    public PneuMarcaBanda() {
    }

    public PneuMarcaBanda(Long codigo, String nome, PneuModeloBandaVisualizacao modelos) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelo = modelos;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PneuModeloBandaVisualizacao getModelo() {
        return modelo;
    }

    public void setModelo(PneuModeloBandaVisualizacao modelos) {
        this.modelo = modelos;
    }

    @Override
    public String toString() {
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", modelos=" + modelo +
                '}';
    }
}
