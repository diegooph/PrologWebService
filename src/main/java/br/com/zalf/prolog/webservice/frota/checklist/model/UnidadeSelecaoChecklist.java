package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-06-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class UnidadeSelecaoChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final Long codRegionalVinculada;

    public UnidadeSelecaoChecklist(@NotNull final Long codigo,
                                   @NotNull final String nome,
                                   @NotNull final Long codRegionalVinculada) {
        this.codigo = codigo;
        this.nome = nome;
        this.codRegionalVinculada = codRegionalVinculada;
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
    public Long getCodRegionalVinculada() {
        return codRegionalVinculada;
    }
}
