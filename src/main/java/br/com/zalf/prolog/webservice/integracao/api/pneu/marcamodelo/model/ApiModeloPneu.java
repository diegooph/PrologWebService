package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiModeloPneu {
    @NotNull
    private final Long codMarcaPneu;
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

    public ApiModeloPneu(@NotNull final Long codMarcaPneu,
                         @NotNull final Long codigo,
                         @NotNull final String nome,
                         @NotNull final Integer quantidadeSulcos,
                         @NotNull final Double alturaSulcoQuandoNovo,
                         @NotNull final Boolean statusAtivo) {
        this.codMarcaPneu = codMarcaPneu;
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcoQuandoNovo = alturaSulcoQuandoNovo;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    static ApiModeloPneu getApiModeloPneuDummy() {
        return new ApiModeloPneu(
                1L,
                5L,
                "G685",
                4,
                15.0,
                true);
    }

    @NotNull
    public Long getCodMarcaPneu() {
        return codMarcaPneu;
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
