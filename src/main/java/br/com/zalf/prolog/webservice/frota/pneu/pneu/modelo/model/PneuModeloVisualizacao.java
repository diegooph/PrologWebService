package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 26/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloVisualizacao {

    //Nullable por conta das integrações.
    @Nullable
    private Long codMarca;
    @NotNull
    private Long codigo;

    @NotNull
    private String nome;

    private int quantidadeSulcos;

    @NotNull
    private Double alturaSulcos;


    public PneuModeloVisualizacao(@Nullable final Long codMarca,
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


    public PneuModeloVisualizacao() {
    }

    public void setQuantidadeSulcos(final int quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
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

    public void setCodigo(@NotNull final Long codModeloPneu) {
    }
}
