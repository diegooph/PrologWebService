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
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoInicio;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoFim;
    @NotNull
    @SerializedName("tempoDecorridoEntreInicioFimEmSegundos")
    private final Duration tempoDecorridoEntreInicioFim;
    @NotNull
    @SerializedName("tempoRecomendadoTipoMarcacaoEmSegundos")
    private final Duration tempoRecomendadoTipoMarcacao;
    @Nullable
    private final String justificativaEstouro;
    private final boolean temJustificativaEstouro;
    @Nullable
    private final String justificativaTempoRecomendado;
    private final boolean temJustificativaTempoRecomendado;

    public MarcacaoAgrupadaAcompanhamento(@NotNull final String nomeTipoMarcacao,
                                          @NotNull final String cpfColaborador,
                                          @NotNull final String nomeColaborador,
                                          @Nullable final MarcacaoAcompanhamento marcacaoInicio,
                                          @Nullable final MarcacaoAcompanhamento marcacaoFim,
                                          @NotNull final Duration tempoDecorridoEntreInicioFim,
                                          @NotNull final Duration tempoRecomendadoTipoMarcacao,
                                          @Nullable final String justificativaEstouro,
                                          @Nullable final String justificativaTempoRecomendado) {
        this.nomeTipoMarcacao = nomeTipoMarcacao;
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.marcacaoInicio = marcacaoInicio;
        this.marcacaoFim = marcacaoFim;
        this.tempoDecorridoEntreInicioFim = tempoDecorridoEntreInicioFim;
        this.tempoRecomendadoTipoMarcacao = tempoRecomendadoTipoMarcacao;
        this.justificativaEstouro = justificativaEstouro;
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
        this.temJustificativaEstouro = justificativaEstouro != null;
        this.temJustificativaTempoRecomendado = justificativaTempoRecomendado != null;
    }

    @NotNull
    public static MarcacaoAgrupadaAcompanhamento createDummy(@Nullable final TipoInicioFim tipoInicioFim) {
        if (tipoInicioFim == null) {
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refei????o",
                    "3383283194",
                    "Jo??o Carlos de Souza",
                    MarcacaoAcompanhamento.createDummy(true),
                    MarcacaoAcompanhamento.createDummy(false),
                    Duration.ofHours(3),
                    Duration.ofHours(1),
                    "Esqueci de finalizar",
                    null);
        } else if (tipoInicioFim.equals(TipoInicioFim.MARCACAO_INICIO)) {
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refei????o",
                    "3383283194",
                    "Jo??o Carlos de Souza",
                    MarcacaoAcompanhamento.createDummy(true),
                    null,
                    Duration.ZERO,
                    Duration.ofHours(1),
                    "Esqueci de finalizar",
                    null);
        } else {
            // Apenas de fim.
            return new MarcacaoAgrupadaAcompanhamento(
                    "Refei????o",
                    "3383283194",
                    "Jo??o Carlos de Souza",
                    null,
                    MarcacaoAcompanhamento.createDummy(true),
                    Duration.ZERO,
                    Duration.ofHours(1),
                    "Esqueci de finalizar",
                    null);
        }
    }

    @NotNull
    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    @NotNull
    public String getCpfColaborador() {
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

    @NotNull
    public Duration getTempoDecorridoEntreInicioFim() {
        return tempoDecorridoEntreInicioFim;
    }

    @NotNull
    public Duration getTempoRecomendadoTipoMarcacao() {
        return tempoRecomendadoTipoMarcacao;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    public boolean isTemJustificativaEstouro() {
        return temJustificativaEstouro;
    }

    public boolean isTemJustificativaTempoRecomendado() {
        return temJustificativaTempoRecomendado;
    }
}