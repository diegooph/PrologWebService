package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class CargoEdicao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;

    public CargoEdicao(@NotNull final Long codEmpresa,
                       @NotNull final Long codigo,
                        @NotNull final String nome) {
        this.codEmpresa = codEmpresa;
        this.codigo = codigo;
        this.nome = nome;
    }

    @NotNull
    public static CargoEdicao createDummy() {
        return new CargoEdicao(
                1L,
                1L,
                "Motorista");
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
    public String getNome() {
        return nome;
    }
}
