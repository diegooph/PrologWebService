package br.com.zalf.prolog.webservice.commons.colaborador;

import java.util.List;

/**
 * Created by jean on 27/01/16.
 * Unidade de uma empresa, contém uma ou mais equipes (salas).
 */
public class Unidade {

    private Long codigo;
    private String nome;
    private List<String> listEquipe;

    public Unidade() {
    }

    public Unidade(Long codigo, String nome, List<String> listEquipe) {
        this.codigo = codigo;
        this.nome = nome;
        this.listEquipe = listEquipe;
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

    public List<String> getListEquipe() {
        return listEquipe;
    }

    public void setListEquipe(List<String> listEquipe) {
        this.listEquipe = listEquipe;
    }

    @Override
    public String toString() {
        return "Unidade{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", listEquipe=" + listEquipe +
                '}';
    }
}
