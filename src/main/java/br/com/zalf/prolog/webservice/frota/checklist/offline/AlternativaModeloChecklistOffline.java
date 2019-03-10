package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistOffline {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String descricao;
    private final boolean tipoOutros;
    private final int ordemExibicao;

    public AlternativaModeloChecklistOffline(@NotNull final Long codigo,
                                             @NotNull final String descricao,
                                             final boolean tipoOutros,
                                             final int ordemExibicao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoOutros = tipoOutros;
        this.ordemExibicao = ordemExibicao;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }
}