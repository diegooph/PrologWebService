package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoRelatorio1510 {
    @NotNull
    private final Long codMarcacao;
    @NotNull
    private final String nsr;
    @NotNull
    private final String tipoRegistro;
    @NotNull
    private final String dataMarcacao;
    @NotNull
    private final String horaMarcacao;
    @NotNull
    private final String pisColaborador;

    public ApiMarcacaoRelatorio1510(@NotNull final Long codMarcacao,
                                    @NotNull final String nsr,
                                    @NotNull final String tipoRegistro,
                                    @NotNull final String dataMarcacao,
                                    @NotNull final String horaMarcacao,
                                    @NotNull final String pisColaborador) {
        this.codMarcacao = codMarcacao;
        this.nsr = nsr;
        this.tipoRegistro = tipoRegistro;
        this.dataMarcacao = dataMarcacao;
        this.horaMarcacao = horaMarcacao;
        this.pisColaborador = pisColaborador;
    }

    @NotNull
    public Long getCodMarcacao() {
        return codMarcacao;
    }

    @NotNull
    public String getNsr() {
        return nsr;
    }

    @NotNull
    public String getTipoRegistro() {
        return tipoRegistro;
    }

    @NotNull
    public String getDataMarcacao() {
        return dataMarcacao;
    }

    @NotNull
    public String getHoraMarcacao() {
        return horaMarcacao;
    }

    @NotNull
    public String getPisColaborador() {
        return pisColaborador;
    }
}
