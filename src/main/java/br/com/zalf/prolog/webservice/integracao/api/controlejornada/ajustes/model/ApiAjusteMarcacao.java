package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAjusteMarcacao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codJustificativaAjuste;
    @NotNull
    private final String nomeJustificativaAjuste;
    @Nullable
    private final String observacaoAjuste;
    @NotNull
    private final ApiAcaoAjusteMarcacao acaoAjusteMarcacao;
    @NotNull
    private final String cpfColaboradorAjuste;
    @NotNull
    private final LocalDateTime dataHoraRealizacaoAjusteUtc;
    @NotNull
    private final ApiMarcacao marcacaoAjustada;

    public ApiAjusteMarcacao(@NotNull final Long codigo,
                             @NotNull final Long codJustificativaAjuste,
                             @NotNull final String nomeJustificativaAjuste,
                             @Nullable final String observacaoAjuste,
                             @NotNull final ApiAcaoAjusteMarcacao acaoAjusteMarcacao,
                             @NotNull final String cpfColaboradorAjuste,
                             @NotNull final LocalDateTime dataHoraRealizacaoAjusteUtc,
                             @NotNull final ApiMarcacao marcacaoAjustada) {
        this.codigo = codigo;
        this.codJustificativaAjuste = codJustificativaAjuste;
        this.nomeJustificativaAjuste = nomeJustificativaAjuste;
        this.observacaoAjuste = observacaoAjuste;
        this.acaoAjusteMarcacao = acaoAjusteMarcacao;
        this.cpfColaboradorAjuste = cpfColaboradorAjuste;
        this.dataHoraRealizacaoAjusteUtc = dataHoraRealizacaoAjusteUtc;
        this.marcacaoAjustada = marcacaoAjustada;
    }

    @NotNull
    public static ApiAjusteMarcacao getDummy() {
        return new ApiAjusteMarcacao(
                50L,
                2L,
                "ESQUECIMENTO",
                "Colaborador alegou que esqueceu de marcar",
                ApiAcaoAjusteMarcacao.ADICAO,
                "03383283194",
                Now.localDateTimeUtc(),
                ApiMarcacao.getDummy());
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Long getCodJustificativaAjuste() {
        return codJustificativaAjuste;
    }

    @NotNull
    public String getNomeJustificativaAjuste() {
        return nomeJustificativaAjuste;
    }

    @Nullable
    public String getObservacaoAjuste() {
        return observacaoAjuste;
    }

    @NotNull
    public ApiAcaoAjusteMarcacao getAcaoAjusteMarcacao() {
        return acaoAjusteMarcacao;
    }

    @NotNull
    public String getCpfColaboradorAjuste() {
        return cpfColaboradorAjuste;
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacaoAjusteUtc() {
        return dataHoraRealizacaoAjusteUtc;
    }

    @NotNull
    public ApiMarcacao getMarcacaoAjustada() {
        return marcacaoAjustada;
    }
}
