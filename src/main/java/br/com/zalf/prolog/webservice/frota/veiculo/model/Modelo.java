package br.com.zalf.prolog.webservice.frota.veiculo.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import com.sun.istack.internal.Nullable;

/**
 * Created by jean on 20/06/16.
 */
public class Modelo {
    @Nullable
    @Exclude
    private String tipo;
    private long codigo;
    private String nome;

    public Modelo() {

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Modelo{" +
                "tipo='" + tipo + '\'' +
                ", codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}