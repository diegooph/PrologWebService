package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 01/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModelosBanda {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final List<PneuModeloBandaVisualizacao> modelos;

    public PneuMarcaModelosBanda(@NotNull final Long codigo,
                                 @NotNull final String nome,
                                 @NotNull final List<PneuModeloBandaVisualizacao> modelos) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelos = modelos;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public List<PneuModeloBandaVisualizacao> getModelos() {
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
