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
    private final Long codColaborador;
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final LocalDateTime dataNascimentoColaborador;
    @NotNull
    private final Long codCargoColaborador;

    public ColaboradorChecklistOffline(@NotNull final Long codColaborador,
                                       @NotNull final String cpfColaborador,
                                       @NotNull final LocalDateTime dataNascimentoColaborador,
                                       @NotNull final Long codCargoColaborador) {
        this.codColaborador = codColaborador;
        this.cpfColaborador = cpfColaborador;
        this.dataNascimentoColaborador = dataNascimentoColaborador;
        this.codCargoColaborador = codCargoColaborador;
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
}