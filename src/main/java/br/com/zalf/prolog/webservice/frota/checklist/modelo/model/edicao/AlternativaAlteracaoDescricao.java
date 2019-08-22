package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaAlteracaoDescricao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String novaDescricao;

    public AlternativaAlteracaoDescricao(@NotNull final Long codigo,
                                         @NotNull final String novaDescricao) {
        this.codigo = codigo;
        this.novaDescricao = novaDescricao;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNovaDescricao() {
        return novaDescricao;
    }
}