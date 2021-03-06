package br.com.zalf.prolog.webservice.frota.veiculo.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jean on 20/06/16.
 */
public abstract class Modelo {
    @NotNull
    @Exclude
    private String tipo;
    private Long codigo;
    private String nome;

    public Modelo() {

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Modelo{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
