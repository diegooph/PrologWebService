package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/06/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class CargoListagemEmpresa {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Long qtdColaboradoresVinculados;

    public CargoListagemEmpresa(@NotNull final Long codigo,
                                @NotNull final String nome,
                                @NotNull final Long qtdColaboradoresVinculados) {
        this.codigo = codigo;
        this.nome = nome;
        this.qtdColaboradoresVinculados = qtdColaboradoresVinculados;
    }

    @NotNull
    public static CargoListagemEmpresa createDummy() {
        return new CargoListagemEmpresa(
                1L,
                "Motorista",
                2L);
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
    public Long getQtdColaboradoresVinculados() {
        return qtdColaboradoresVinculados;
    }
}