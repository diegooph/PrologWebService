package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

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
    @NotNull
    private final Long codIdioma;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final LocalDateTime dataHoraCadastro;


    public PneuNomenclaturaItem(@NotNull final Long codDiagrama,
                                @NotNull final Long codEmpresa,
                                @NotNull final Long codUnidade,
                                @NotNull final Long posicaoProlog,
                                @NotNull final String nomenclatura,
                                @NotNull final Long codIdioma,
                                @NotNull final Long codColaborador,
                                @NotNull final LocalDateTime dataHoraCadastro) {
        this.codDiagrama = codDiagrama;
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.posicaoProlog = posicaoProlog;
        this.nomenclatura = nomenclatura;
        this.codIdioma = codIdioma;
        this.codColaborador = codColaborador;
        this.dataHoraCadastro = dataHoraCadastro;
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

    @NotNull
    public Long getCodIdioma() {
        return codIdioma;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public LocalDateTime getDataHoraCadastro() {
        return dataHoraCadastro;
    }
}