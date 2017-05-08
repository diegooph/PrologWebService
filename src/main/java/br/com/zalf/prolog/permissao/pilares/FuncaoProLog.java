package br.com.zalf.prolog.permissao.pilares;

/**
 * Created by Zalf on 24/10/16.
 */
public class FuncaoProLog {

    int codigo;
    String descricao;

    public FuncaoProLog() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "FuncaoProLog{" +
                "codigo=" + codigo +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
