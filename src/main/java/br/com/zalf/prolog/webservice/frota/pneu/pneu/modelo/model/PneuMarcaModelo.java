package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 27/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModelo {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<PneuModeloVisualizacao> modelos;

    public PneuMarcaModelo(@NotNull final Long codigo,
                           @NotNull final String nome,
                           @NotNull final List<PneuModeloVisualizacao> modelos) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public List<PneuModeloVisualizacao> getModelos() {
        return modelos;
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