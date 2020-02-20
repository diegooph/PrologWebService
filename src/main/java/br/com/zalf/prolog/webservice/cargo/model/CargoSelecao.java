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
    private final int qtdPermissoes;

    public CargoSelecao(@NotNull final Long codigo,
                        @NotNull final String nome,
                        final int qtdPermissoes) {
        this.codigo = codigo;
        this.nome = nome;
        this.qtdPermissoes = qtdPermissoes;
    }

    @NotNull
    public static CargoSelecao createDummy() {
        return new CargoSelecao(
                1L,
                "Motorista",
                2);
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public int getQtdPermissoes() {return qtdPermissoes;}
}