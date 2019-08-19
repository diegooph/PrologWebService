package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiModeloBanda {
    @NotNull
    private final Long codMarcaBanda;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Integer quantidadeSulcos;
    @NotNull
    private final Double alturaSulcoQuandoNovo;
    @NotNull
    private final Boolean statusAtivo;

    public ApiModeloBanda(@NotNull final Long codMarcaBanda,
                          @NotNull final Long codigo,
                          @NotNull final String nome,
                          @NotNull final Integer quantidadeSulcos,
                          @NotNull final Double alturaSulcoQuandoNovo,
                          @NotNull final Boolean statusAtivo) {
        this.codMarcaBanda = codMarcaBanda;
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcoQuandoNovo = alturaSulcoQuandoNovo;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    static ApiModeloBanda getApiModeloBandaDummy() {
        return new ApiModeloBanda(
                25L,
                57L,
                "vlw110",
                3,
                16.0,
                true);
    }

    @NotNull
    public Long getCodMarcaBanda() {
        return codMarcaBanda;
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
    public Integer getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    @NotNull
    public Double getAlturaSulcoQuandoNovo() {
        return alturaSulcoQuandoNovo;
    }

    @NotNull
    public Boolean getStatusAtivo() {
        return statusAtivo;
    }
}
