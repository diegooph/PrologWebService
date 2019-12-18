package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaListagem {
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final String nomeResponsavelAberturaSocorro;
    @NotNull
    private final String descricaoOpcaoProblemaAberturaSocorro;
    @NotNull
    private final Date dataHoraAberturaSocorro;
    @Nullable
    private final String enderecoAutomaticoAberturaSocorro;
    @NotNull
    private final StatusSocorroRota statusAtualSocorroRota;

    public SocorroRotaListagem(@NotNull final String placaVeiculo,
                               @NotNull final String nomeResponsavelAberturaSocorro,
                               @NotNull final String descricaoOpcaoProblemaAberturaSocorro,
                               @NotNull final Date dataHoraAberturaSocorro,
                               @Nullable final String enderecoAutomaticoAberturaSocorro,
                               @NotNull final StatusSocorroRota statusAtualSocorroRota) {
        this.placaVeiculo = placaVeiculo;
        this.nomeResponsavelAberturaSocorro = nomeResponsavelAberturaSocorro;
        this.descricaoOpcaoProblemaAberturaSocorro = descricaoOpcaoProblemaAberturaSocorro;
        this.dataHoraAberturaSocorro = dataHoraAberturaSocorro;
        this.enderecoAutomaticoAberturaSocorro = enderecoAutomaticoAberturaSocorro;
        this.statusAtualSocorroRota = statusAtualSocorroRota;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public String getNomeResponsavelAberturaSocorro() {
        return nomeResponsavelAberturaSocorro;
    }

    @NotNull
    public String getDescricaoOpcaoProblemaAberturaSocorro() {
        return descricaoOpcaoProblemaAberturaSocorro;
    }

    @NotNull
    public Date getDataHoraAberturaSocorro() {
        return dataHoraAberturaSocorro;
    }

    @Nullable
    public String getEnderecoAutomaticoAberturaSocorro() {
        return enderecoAutomaticoAberturaSocorro;
    }

    @NotNull
    public StatusSocorroRota getStatusAtualSocorroRota() {
        return statusAtualSocorroRota;
    }
}
