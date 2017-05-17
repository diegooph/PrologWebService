package br.com.zalf.prolog.webservice.colaborador;

import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * Created by jean on 27/01/16.
 * Dados da uma empresa.
 * Usado nos campos onde é necessária alguma filtragem.
 * Cada empresa tem unidades em uma ou mais regionais, por isso o List de Regional
 * @see Regional
 */
public class Empresa {

    private int codigo;
    private String nome;
    private List<Regional> listRegional;
    @Nullable
    private String logoThumbnailUrl;

    public Empresa(int codigo, String nome, List<Regional> listRegional) {
        this.codigo = codigo;
        this.nome = nome;
        this.listRegional = listRegional;
    }

    public Empresa() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Regional> getListRegional() {
        return listRegional;
    }

    public void setListRegional(List<Regional> listRegional) {
        this.listRegional = listRegional;
    }

    public String getLogoThumbnailUrl() {
        return logoThumbnailUrl;
    }

    public void setLogoThumbnailUrl(String logoThumbnailUrl) {
        this.logoThumbnailUrl = logoThumbnailUrl;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", listRegional=" + listRegional +
                '}';
    }
}
