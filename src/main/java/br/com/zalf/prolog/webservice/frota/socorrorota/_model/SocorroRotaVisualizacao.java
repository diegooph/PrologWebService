package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaVisualizacao {
    @NotNull
    private final Long codSocorroRota;
    @NotNull
    private final StatusSocorroRota statusAtualSocorroRota;
    @NotNull
    private final SocorroRotaAberturaVisualizacao aberturaVisualizacao;
    @Nullable
    private final SocorroRotaAtendimentoVisualizacao atendimentoVisualizacao;
    @Nullable
    private final SocorroRotaFinalizacaoVisualizacao finalizacaoVisualizacao;
    @Nullable
    private final SocorroRotaInvalidacaoVisualizacao invalidacaoVisualizacao;

    public SocorroRotaVisualizacao(@NotNull final Long codSocorroRota,
                                   @NotNull final StatusSocorroRota statusAtualSocorroRota,
                                   @NotNull final SocorroRotaAberturaVisualizacao aberturaVisualizacao,
                                   @Nullable final SocorroRotaAtendimentoVisualizacao atendimentoVisualizacao,
                                   @Nullable final SocorroRotaFinalizacaoVisualizacao finalizacaoVisualizacao,
                                   @Nullable final SocorroRotaInvalidacaoVisualizacao invalidacaoVisualizacao) {
        this.codSocorroRota = codSocorroRota;
        this.statusAtualSocorroRota = statusAtualSocorroRota;
        this.aberturaVisualizacao = aberturaVisualizacao;
        this.atendimentoVisualizacao = atendimentoVisualizacao;
        this.finalizacaoVisualizacao = finalizacaoVisualizacao;
        this.invalidacaoVisualizacao = invalidacaoVisualizacao;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @NotNull
    public StatusSocorroRota getStatusAtualSocorroRota() {
        return statusAtualSocorroRota;
    }

    @NotNull
    public SocorroRotaAberturaVisualizacao getAberturaVisualizacao() {
        return aberturaVisualizacao;
    }

    @Nullable
    public SocorroRotaAtendimentoVisualizacao getAtendimentoVisualizacao() {
        return atendimentoVisualizacao;
    }

    @Nullable
    public SocorroRotaFinalizacaoVisualizacao getFinalizacaoVisualizacao() {
        return finalizacaoVisualizacao;
    }

    @Nullable
    public SocorroRotaInvalidacaoVisualizacao getInvalidacaoVisualizacao() {
        return invalidacaoVisualizacao;
    }

    @NotNull
    public String getLatitudeAbertura() {
        return aberturaVisualizacao.getLocalizacaoAberturaScorro().getLatitude();
    }

    @NotNull
    public String getLongitudeAbertura() {
        return aberturaVisualizacao.getLocalizacaoAberturaScorro().getLongitude();
    }

    @NotNull
    public Long getCodColaboradorAberturaSocorro() {
        return aberturaVisualizacao.getCodColaboradorResponsavelAbertura();
    }

    public boolean isAberto() {
        return statusAtualSocorroRota.equals(StatusSocorroRota.ABERTO);
    }

    public boolean isEmAtendimento() {
        return statusAtualSocorroRota.equals(StatusSocorroRota.EM_ATENDIMENTO);
    }
}
