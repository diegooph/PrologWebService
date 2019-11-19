package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuDadosTabelaImport {
    @NotNull
    private Long codDadosAutorImport;
    @NotNull
    private String nomeTabelaImport;

    public PneuDadosTabelaImport(@NotNull final Long codDadosAutorImport,
                                    @NotNull final String nomeTabelaImport) {
        this.codDadosAutorImport = codDadosAutorImport;
        this.nomeTabelaImport = nomeTabelaImport;
    }

    public final Long getCodDadosAutorImport() {
        return codDadosAutorImport;
    }

    public final String getNomeTabelaImport() {
        return nomeTabelaImport;
    }
}
