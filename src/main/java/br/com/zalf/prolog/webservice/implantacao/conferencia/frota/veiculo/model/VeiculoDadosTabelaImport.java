package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 18/10/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoDadosTabelaImport {
    @NotNull
    private Long codDadosAutorImport;
    @NotNull
    private String nomeTabelaImport;

    public VeiculoDadosTabelaImport(@NotNull final Long codDadosAutorImport,
                                    @NotNull final String nomeTabelaImport) {
        this.codDadosAutorImport = codDadosAutorImport;
        this.nomeTabelaImport = nomeTabelaImport;
    }

    public VeiculoDadosTabelaImport() {

    }

    public final Long getCodDadosAutorImport() {
        return codDadosAutorImport;
    }

    public final String getNomeTabelaImport() {
        return nomeTabelaImport;
    }
}
