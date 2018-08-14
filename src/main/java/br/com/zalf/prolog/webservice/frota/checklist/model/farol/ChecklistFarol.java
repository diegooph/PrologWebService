package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Representa um checklist realizado para ser utilizado pelo {@link FarolChecklist}.
 *
 * Created on 01/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistFarol {
    @NotNull
    private final Long codigoChecklist;
    @NotNull
    private final String nomeColaboradorRealizacao;
    @NotNull
    private final LocalDateTime dataHoraRealizacao;
    private final char tipoChecklist;

    public ChecklistFarol(@NotNull final Long codigoChecklist,
                          @NotNull final String nomeColaboradorRealizacao,
                          @NotNull final LocalDateTime dataHoraRealizacao,
                          final char tipoChecklist) {
        this.codigoChecklist = codigoChecklist;
        this.nomeColaboradorRealizacao = nomeColaboradorRealizacao;
        this.dataHoraRealizacao = dataHoraRealizacao;
        this.tipoChecklist = tipoChecklist;
    }

    @NotNull
    public Long getCodigoChecklist() {
        return codigoChecklist;
    }

    @NotNull
    public String getNomeColaboradorRealizacao() {
        return nomeColaboradorRealizacao;
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    public char getTipoChecklist() {
        return tipoChecklist;
    }
}