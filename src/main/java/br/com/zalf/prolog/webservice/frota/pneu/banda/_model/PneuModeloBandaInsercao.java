package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

/**
 * Created on 25/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloBandaInsercao {
    @NotNull
    private final Long codEmpresa;

    @NotNull
    private final Long codMarca;

    @NotNull
    @NotBlank(message = "O nome do modelo não pode estar vazio")
    private final String nome;

    @Range(min = 1, max = 6, message = "A quantidade de sulcos aceita é entre 1 e 6")
    private final int quantidadeSulcos;

    @DecimalMin(value = "1.0", message = "A altura dos sulcos deve ser, pelo menos, 1.0")
    private final Double alturaSulcos;

    public PneuModeloBandaInsercao(@NotNull final Long codEmpresa,
                                   @NotNull final Long codMarca,
                                   @NotNull final String nome,
                                   final int quantidadeSulcos,
                                   final double alturaSulcos) {
        this.codEmpresa = codEmpresa;
        this.codMarca = codMarca;
        this.nome = nome;
        this.quantidadeSulcos = quantidadeSulcos;
        this.alturaSulcos = alturaSulcos;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodMarca() {
        return codMarca;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public double getAlturaSulcos() {
        return alturaSulcos;
    }
}
