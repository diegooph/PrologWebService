package br.com.zalf.prolog.webservice.permissao.pilares;

import java.util.List;

/**
 * Created by Wellington on 22/05/19.
 */
public class FuncionalidadeProLog {

    public int codigo;
    public String nome;
    public List<FuncaoProLog> funcoes;

    public FuncionalidadeProLog() {
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

    public List<FuncaoProLog> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(final List<FuncaoProLog> funcoes) {
        this.funcoes = funcoes;
    }

    @Override
    public String toString() {
        return "FuncionalidadeProLog{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
