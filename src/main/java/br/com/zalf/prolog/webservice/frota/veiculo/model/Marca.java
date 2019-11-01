package br.com.zalf.prolog.webservice.frota.veiculo.model;

import java.util.List;

/**
 * @deprecated em 2019-10-11 por conta da criação de objetos específicos para o crud de bandas e modelos de pneu.
 * Esse objeto precisa ser refatorado.
 */
@Deprecated
public class Marca {

    private long codigo;
    private String nome;
    private List<Modelo> modelos;

    public Marca() {
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

    public List<Modelo> getModelos() {
        return modelos;
    }

    public void setModelos(List<Modelo> modelos) {
        this.modelos = modelos;
    }

    @Override
    public String toString() {
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", modelos=" + modelos +
                '}';
    }
}
