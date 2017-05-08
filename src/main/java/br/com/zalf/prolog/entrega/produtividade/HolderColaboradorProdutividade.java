package br.com.zalf.prolog.entrega.produtividade;

import java.util.List;

/**
 * Created by jean on 23/08/16.
 */
public class HolderColaboradorProdutividade {

    private String funcao;
    private List<ColaboradorProdutividade> produtividades;

    public HolderColaboradorProdutividade() {
    }

    public HolderColaboradorProdutividade(String funcao, List<ColaboradorProdutividade> produtividades) {
        this.funcao = funcao;
        this.produtividades = produtividades;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public List<ColaboradorProdutividade> getProdutividades() {
        return produtividades;
    }

    public void setProdutividades(List<ColaboradorProdutividade> produtividades) {
        this.produtividades = produtividades;
    }

    @Override
    public String toString() {
        return "HolderColaboradorProdutividade{" +
                "funcao='" + funcao + '\'' +
                ", produtividades=" + produtividades +
                '}';
    }
}
