package br.com.zalf.prolog.webservice.cargo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoEmUso {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    private final int qtdColaboradoresVinculados;
    private final int qtdPermissoesVinculadas;

    @NotNull
    public static CargoEmUso createDummy() {
        return new CargoEmUso(
                1L,
                "Motorista",
                10,
                42);
    }

    public CargoEmUso(@NotNull final Long codigo,
                      @NotNull final String nome,
                      final int qtdColaboradoresVinculados,
                      final int qtdPermissoesVinculadas) {
        this.codigo = codigo;
        this.nome = nome;
        this.qtdColaboradoresVinculados = qtdColaboradoresVinculados;
        this.qtdPermissoesVinculadas = qtdPermissoesVinculadas;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    public int getQtdColaboradoresVinculados() {
        return qtdColaboradoresVinculados;
    }

    public int getQtdPermissoesVinculadas() {
        return qtdPermissoesVinculadas;
    }
}