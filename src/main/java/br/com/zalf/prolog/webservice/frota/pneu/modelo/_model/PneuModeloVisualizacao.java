package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloVisualizacao {

    @NotNull
    private final Long codMarca;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int quantidadeSulcos;
    @NotNull
    private final Double alturaSulcos;


    public PneuModeloVisualizacao(@NotNull final Long codMarca,
                                  @NotNull final Long codigo,
                                  @NotNull final String nome,
                                  final int quantidadeSulcos,
                                  @NotNull final Double alturaSulcos) {
        this.codMarca = codMarca;
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    public Long getCodMarca() {
        return codMarca;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}
