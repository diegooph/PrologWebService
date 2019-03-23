package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoNaoUtilizado {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int qtdPermissoesVinculadas;

    public CargoNaoUtilizado(@NotNull final Long codigo,
                             @NotNull final String nome,
                             final int qtdPermissoesVinculadas) {
        this.codigo = codigo;
        this.nome = nome;
        this.qtdPermissoesVinculadas = qtdPermissoesVinculadas;
    }

    @NotNull
    public static CargoNaoUtilizado createDummy() {
        return new CargoNaoUtilizado(
                1L,
                "Motorista",
                42);
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public int getQtdPermissoesVinculadas() {
        return qtdPermissoesVinculadas;
    }
}