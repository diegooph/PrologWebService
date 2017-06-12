package br.com.zalf.prolog.webservice.frota.veiculo.model;

import com.google.gson.annotations.Expose;
import com.sun.istack.internal.Nullable;

/**
 * Created by jean on 20/06/16.
 */
public class Modelo {

    @Nullable
    @Expose(serialize = false, deserialize = false)
    private String tipo;
    private long codigo;
    private String nome;

    public Modelo() {
    }

    @Nullable
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Modelo{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
