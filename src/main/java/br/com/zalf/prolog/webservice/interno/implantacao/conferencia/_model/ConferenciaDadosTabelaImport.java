package br.com.zalf.prolog.webservice.interno.implantacao.conferencia._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/12/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ConferenciaDadosTabelaImport {
    @NotNull
    private Long codDadosAutorImport;
    @NotNull
    private String nomeTabelaImport;

    public ConferenciaDadosTabelaImport(@NotNull final Long codDadosAutorImport,
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
