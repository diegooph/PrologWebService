package br.com.zalf.prolog.webservice.permissao.pilares;

import java.util.List;

/**
 * Created by luiz on 4/18/16.
 */
public class Pilar {
    public int codigo;
    public String nome;
    public List<FuncaoProLog> funcoes;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(final int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public List<FuncaoProLog> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(final List<FuncaoProLog> funcoes) {
        this.funcoes = funcoes;
    }

    @Override
    public String toString() {
        return "Pilar{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", funcoes=" + funcoes +
                '}';
    }
}
