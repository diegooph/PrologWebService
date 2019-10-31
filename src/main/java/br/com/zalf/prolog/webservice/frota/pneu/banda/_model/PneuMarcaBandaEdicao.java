package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;

/**
 * Created on 2019-10-31
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuMarcaBandaEdicao {
    @NotNull
    private final Long codigo;
    @NotNull
    @NotBlank(message = "O nome da marca não pode estar vazio")
    private final String nome;

    public PneuMarcaBandaEdicao(@NotNull final Long codigo,
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

    @Override
    public String toString() {
        return "Marca{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
