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

    public Regional(final Long codigo, final String nome, final List<Unidade> listUnidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.listUnidade = listUnidade;
    }

    public Regional(final Long codigo, final String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public Regional() {
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

    public List<Unidade> getListUnidade() {
        return listUnidade;
    }

    public void setListUnidade(final List<Unidade> listUnidade) {
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
