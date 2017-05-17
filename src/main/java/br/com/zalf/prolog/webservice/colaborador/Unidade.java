package br.com.zalf.prolog.webservice.colaborador;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jean on 27/01/16.
 * Unidade de uma empresa, contém uma ou mais equipes (salas).
 */
public class Unidade {

    private Long codigo;
    private String nome;
    private List<Equipe> equipes;

    /**
     * Ainda não será removido pois implementações antidas utilizam essa lista com o nome das equipes.
     */
    @Deprecated
    @SerializedName("listEquipe")
    private List<String> listNomesEquipes;

    public Unidade() {
    }

    public Unidade(Long codigo, String nome, List<String> listEquipe) {
        this.codigo = codigo;
        this.nome = nome;
        this.listNomesEquipes = listEquipe;
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

    public List<String> getListNomesEquipes() {
        return listNomesEquipes;
    }

    public void setListNomesEquipes(List<String> listNomesEquipes) {
        this.listNomesEquipes = listNomesEquipes;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    @Override
    public String toString() {
        return "Unidade{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", listNomesEquipes=" + listNomesEquipes +
                '}';
    }
}
