package br.com.zalf.prolog.webservice.frota.pneu.modelo._model;

import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloEdicao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codMarca;

    @NotNull
    @NotBlank(message = "O nome do modelo não pode estar vazio")
    private final String nome;

    @Range(min = 0, max = 6, message = "A quantidade de sulcos aceita é entre 1 e 6")
    private final int quantidadeSulcos;

    @NotNull
    @DecimalMin(value = "1.0", message = "A altura dos sulcos deve ser, pelo menos, 1.0")
    private final Double alturaSulcos;

    public PneuModeloEdicao(@NotNull final Long codEmpresa,
                            @NotNull final Long codigo,
                            @NotNull final Long codMarca,
                            @NotNull final String nome,
                            final int quantidadeSulcos,
                            @NotNull final Double alturaSulcos) {
        this.codEmpresa = codEmpresa;
        this.codigo = codigo;
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
    public Long getCodigo() {
        return codigo;
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

    @NotNull
    public Double getAlturaSulcos() {
        return alturaSulcos;
    }
}
