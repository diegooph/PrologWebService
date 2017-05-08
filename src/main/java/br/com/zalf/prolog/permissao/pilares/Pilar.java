package br.com.zalf.prolog.permissao.pilares;

import java.util.List;

/**
 * Created by luiz on 4/18/16.
 */
public class Pilar {
    public int codigo;
    public String nome;
    public List<FuncaoProLog> funcoes;

    @Override
    public String toString() {
        return "Pilar{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", funcoes=" + funcoes +
                '}';
    }
}
