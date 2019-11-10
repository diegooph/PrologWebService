package br.com.zalf.prolog.webservice.frota.pneu.banda._model;

import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;

/**
 * Created on 2019-10-31
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuMarcaBandaInsercao {
    @NotNull
    private final Long codEmpresa;

    @NotNull
    @NotBlank(message = "O nome da marca não pode estar vazio")
    private final String nome;

    public PneuMarcaBandaInsercao(final @NotNull Long codEmpresa, @NotNull final String nome) {
        this.codEmpresa = codEmpresa;
        this.nome = nome;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }
}