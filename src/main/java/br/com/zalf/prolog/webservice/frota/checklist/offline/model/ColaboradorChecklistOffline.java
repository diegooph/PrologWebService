package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorChecklistOffline {
    @NotNull
    private final Long codEmpresaColaborador;
    @NotNull
    private final Long codRegionalColaborador;
    @NotNull
    private final Long codUnidadeColaborador;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final LocalDateTime dataNascimentoColaborador;
    @NotNull
    private final Long codCargoColaborador;
    @NotNull
    private final Long codPermissaoColaborador;

    public ColaboradorChecklistOffline(@NotNull final Long codEmpresaColaborador,
                                       @NotNull final Long codRegionalColaborador,
                                       @NotNull final Long codUnidadeColaborador,
                                       @NotNull final Long codColaborador,
                                       @NotNull final String cpfColaborador,
                                       @NotNull final LocalDateTime dataNascimentoColaborador,
                                       @NotNull final Long codCargoColaborador,
                                       @NotNull final Long codPermissaoColaborador) {
        this.codEmpresaColaborador = codEmpresaColaborador;
        this.codRegionalColaborador = codRegionalColaborador;
        this.codUnidadeColaborador = codUnidadeColaborador;
        this.codColaborador = codColaborador;
        this.cpfColaborador = cpfColaborador;
        this.dataNascimentoColaborador = dataNascimentoColaborador;
        this.codCargoColaborador = codCargoColaborador;
        this.codPermissaoColaborador = codPermissaoColaborador;
    }

    @NotNull
    public Long getCodEmpresaColaborador() {
        return codEmpresaColaborador;
    }

    @NotNull
    public Long getCodRegionalColaborador() {
        return codRegionalColaborador;
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
    public LocalDateTime getDataNascimentoColaborador() {
        return dataNascimentoColaborador;
    }

    @NotNull
    public Long getCodCargoColaborador() {
        return codCargoColaborador;
    }

    @NotNull
    public Long getCodPermissaoColaborador() {
        return codPermissaoColaborador;
    }
}