package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model;

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
     * Código do colaborador
     * */
    @NotNull
    private final Long codColaborador;
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
                                          @NotNull final Long codColaborador,
                                          @NotNull final Long codOpcaoProblema,
                                          final boolean isStatusAtivo) {
        this.codEmpresa = codEmpresa;
        this.codColaborador = codColaborador;
        this.codOpcaoProblema = codOpcaoProblema;
        this.isStatusAtivo = isStatusAtivo;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }
    @NotNull
    public Long getCodOpcaoProblema() {
        return codOpcaoProblema;
    }

    public boolean isStatusAtivo() {
        return isStatusAtivo;
    }
}
