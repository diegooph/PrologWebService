package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 17/01/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class OpcaoProblemaSocorroRotaStatus {

    /*
     * Código da empresa
     * */
    @NotNull
    private final Long codEmpresa;
    /*
     * Código da opção de problema
     * */
    @NotNull
    private final Long codOpcaoProblema;
    /*
     * Status da opção de problema
     * */
    private final boolean isStatusAtivo;

    public OpcaoProblemaSocorroRotaStatus(@NotNull final Long codEmpresa,
                                          @NotNull final Long codOpcaoProblema,
                                          final boolean isStatusAtivo) {
        this.codEmpresa = codEmpresa;
        this.codOpcaoProblema = codOpcaoProblema;
        this.isStatusAtivo = isStatusAtivo;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodOpcaoProblema() {
        return codOpcaoProblema;
    }

    public boolean isStatusAtivo() {
        return isStatusAtivo;
    }
}
