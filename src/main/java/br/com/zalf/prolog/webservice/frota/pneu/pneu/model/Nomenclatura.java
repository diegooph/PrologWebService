package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 29/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class Nomenclatura {

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
    private final String colaborador;
    @NotNull
    private final LocalDateTime dataHoraCadastro;


    public Nomenclatura(@NotNull final Long codDiagrama,
                        @NotNull final Long codEmpresa,
                        @NotNull final Long codUnidade,
                        @NotNull final Long posicaoProlog,
                        @NotNull final String nomenclatura,
                        @NotNull final Long codIdioma,
                        @NotNull final String colaborador,
                        @NotNull final LocalDateTime dataHoraCadastro) {
        this.codDiagrama = codDiagrama;
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.posicaoProlog = posicaoProlog;
        this.nomenclatura = nomenclatura;
        this.codIdioma = codIdioma;
        this.colaborador = colaborador;
        this.dataHoraCadastro = dataHoraCadastro;
    }

    @NotNull
    public static Nomenclatura createDummy() {
        return new Nomenclatura(
                1L,
                3L,
                5L,
                112L,
                "TDI",
                1L,
                "Cleiton",
                LocalDateTime.now());
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
    public String getColaborador() {
        return colaborador;
    }

    @NotNull
    public LocalDateTime getDataHoraCadastro() {
        return dataHoraCadastro;
    }
}
