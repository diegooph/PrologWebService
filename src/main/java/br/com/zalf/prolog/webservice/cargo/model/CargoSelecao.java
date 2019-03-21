package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoSelecao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    @NotNull
    public static CargoSelecao createDummy() {
        return new CargoSelecao(
                1L,
                "Motorista");
    }

    public CargoSelecao(@NotNull final Long codigo,
                        @NotNull final String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }
}