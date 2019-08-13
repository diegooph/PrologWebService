package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class CargoInsercao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String nome;

    public CargoInsercao(@NotNull final Long codEmpresa,
                         @NotNull final String nome) {
        this.codEmpresa = codEmpresa;
        this.nome = nome;
    }

    @NotNull
    public static CargoInsercao createDummy() {
        return new CargoInsercao(
                1L,
                "Vendedor");
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public String getNome() {
        return nome;
    }
}
