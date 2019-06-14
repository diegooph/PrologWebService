package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 6/14/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */

//TODO VERIFICAR TODAS PROPRIEDADES E VINCULAR COM A DAOIMPL
public class MarcacaoListagem {
    @NotNull
    private final String nomeTipoMarcacao;
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final LocalDateTime marcacaoInicio;
    @Nullable
    private final LocalDateTime marcacaoFim;
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

    public MarcacaoListagem(@NotNull final String nomeTipoMarcacao,
                                          @NotNull final String cpfColaborador,
                                          @NotNull final String nomeColaborador,
                                          @Nullable final LocalDateTime marcacaoInicio,
                                          @Nullable final LocalDateTime marcacaoFim,
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
    public LocalDateTime getMarcacaoInicio() {
        return marcacaoInicio;
    }

    @Nullable
    public LocalDateTime getMarcacaoFim() {
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
