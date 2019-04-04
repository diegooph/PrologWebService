package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorChecklistOffline {
    @NotNull
    private final Long codUnidadeColaborador;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final LocalDate dataNascimentoColaborador;
    @NotNull
    private final Long codCargoColaborador;
    private final int codPermissaoColaborador;

    public ColaboradorChecklistOffline(@NotNull final Long codUnidadeColaborador,
                                       @NotNull final Long codColaborador,
                                       @NotNull final String cpfColaborador,
                                       @NotNull final LocalDate dataNascimentoColaborador,
                                       @NotNull final Long codCargoColaborador,
                                       final Integer codPermissaoColaborador) {
        this.codUnidadeColaborador = codUnidadeColaborador;
        this.codColaborador = codColaborador;
        this.cpfColaborador = cpfColaborador;
        this.dataNascimentoColaborador = dataNascimentoColaborador;
        this.codCargoColaborador = codCargoColaborador;
        this.codPermissaoColaborador = codPermissaoColaborador;
    }

    @NotNull
    public Long getCodUnidadeColaborador() {
        return codUnidadeColaborador;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public LocalDate getDataNascimentoColaborador() {
        return dataNascimentoColaborador;
    }

    @NotNull
    public Long getCodCargoColaborador() {
        return codCargoColaborador;
    }

    public int getCodPermissaoColaborador() {
        return codPermissaoColaborador;
    }
}