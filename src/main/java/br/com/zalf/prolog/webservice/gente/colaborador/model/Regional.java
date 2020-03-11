package br.com.zalf.prolog.webservice.gente.colaborador.model;

import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;

import java.util.List;

/**
 * Created by jean on 27/01/16.
 * Regional, geograficamente falando.
 * Cada regional tem zero ou mais unidades, depende da empresa.
 * @see Unidade
 */
public class Regional {

    private Long codigo;
    private String nome;
    private List<Unidade> listUnidade;

    public Regional(Long codigo, String nome, List<Unidade> listUnidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.listUnidade = listUnidade;
    }

    public Regional() {
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

    public List<Unidade> getListUnidade() {
        return listUnidade;
    }

    public void setListUnidade(List<Unidade> listUnidade) {
        this.listUnidade = listUnidade;
    }

    @Override
    public String toString() {
        return "Regional{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", listUnidade=" + listUnidade +
                '}';
    }
}
