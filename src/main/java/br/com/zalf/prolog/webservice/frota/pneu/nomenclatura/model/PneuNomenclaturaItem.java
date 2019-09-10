package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 29/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaItem {

    @NotNull
    private final Long codDiagrama;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long posicaoProlog;
    @NotNull
    private final String nomenclatura;


    public PneuNomenclaturaItem(@NotNull final Long codDiagrama,
                                @NotNull final Long codEmpresa,
                                @NotNull final Long codUnidade,
                                @NotNull final Long posicaoProlog,
                                @NotNull final String nomenclatura) {
        this.codDiagrama = codDiagrama;
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.posicaoProlog = posicaoProlog;
        this.nomenclatura = nomenclatura;
    }

    @NotNull
    public Long getCodDiagrama() {
        return codDiagrama;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getPosicaoProlog() {
        return posicaoProlog;
    }

    @NotNull
    public String getNomenclatura() {
        return nomenclatura;
    }
}