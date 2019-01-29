package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAgrupadaAcompanhamento {
    @NotNull
    private final String nomeTipoMarcacao;
    @NotNull
    private final Long cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoInicio;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoFim;
    @Nullable
    @SerializedName("tempoDecorridoEntreInicioFimEmSegundos")
    private final Duration tempoDecorridoEntreInicioFim;
    @Nullable
    private final String justificativaEstouro;
    @Nullable
    private final String justificativaTempoRecomendado;

    public MarcacaoAgrupadaAcompanhamento(@NotNull final String nomeTipoMarcacao,
                                          @NotNull final Long cpfColaborador,
                                          @NotNull final String nomeColaborador,
                                          @Nullable final MarcacaoAcompanhamento marcacaoInicio,
                                          @Nullable final MarcacaoAcompanhamento marcacaoFim,
                                          @Nullable final Duration tempoDecorridoEntreInicioFim,
                                          @Nullable final String justificativaEstouro,
                                          @Nullable final String justificativaTempoRecomendado) {
        this.nomeTipoMarcacao = nomeTipoMarcacao;
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.marcacaoInicio = marcacaoInicio;
        this.marcacaoFim = marcacaoFim;
        this.tempoDecorridoEntreInicioFim = tempoDecorridoEntreInicioFim;
        this.justificativaEstouro = justificativaEstouro;
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    @NotNull
    public static MarcacaoAgrupadaAcompanhamento createDummy(@Nullable final TipoInicioFim tipoInicioFim) {
        if (tipoInicioFim == null) {
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refeição",
                    3383283194L,
                    "João Carlos de Souza",
                    MarcacaoAcompanhamento.createDummy(true),
                    MarcacaoAcompanhamento.createDummy(false),
                    Duration.ofHours(3),
                    "Esqueci de finalizar",
                    null);
        } else if (tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)) {
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refeição",
                    3383283194L,
                    "João Carlos de Souza",
                    MarcacaoAcompanhamento.createDummy(true),
                    null,
                    null,
                    "Esqueci de finalizar",
                    null);
        } else {
            // Apenas de fim.
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refeição",
                    3383283194L,
                    "João Carlos de Souza",
                    null,
                    MarcacaoAcompanhamento.createDummy(true),
                    null,
                    "Esqueci de finalizar",
                    null);
        }
    }

    @NotNull
    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    @NotNull
    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @Nullable
    public MarcacaoAcompanhamento getMarcacaoInicio() {
        return marcacaoInicio;
    }

    @Nullable
    public MarcacaoAcompanhamento getMarcacaoFim() {
        return marcacaoFim;
    }

    @Nullable
    public Duration getTempoDecorridoEntreInicioFim() {
        return tempoDecorridoEntreInicioFim;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }
}