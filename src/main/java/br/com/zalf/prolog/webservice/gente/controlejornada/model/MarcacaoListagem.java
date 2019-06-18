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
public class MarcacaoListagem {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nomeTipoMarcacao;
    @NotNull
    private final String iconeTipoMarcacao;
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final boolean foiAjustadoInicio;
    @Nullable
    private final boolean foiAjustadoFim;
    @Nullable
    private final boolean statusAtivoInicio;
    @Nullable
    private final boolean statusAtivoFim;
    @Nullable
    private final Long codMarcacaoInicio;
    @Nullable
    private final Long codMarcacaoFim;
    @Nullable
    private final LocalDateTime dataMarcacaoInicio;
    @Nullable
    private final LocalDateTime dataMarcacaoFim;
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

    public MarcacaoListagem(@NotNull final Long codUnidade,
                            @NotNull final String nomeTipoMarcacao,
                            @NotNull final String iconeTipoMarcacao,
                            @NotNull final String cpfColaborador,
                            @NotNull final String nomeColaborador,
                            @Nullable final boolean foiAjustadoInicio,
                            @Nullable final boolean foiAjustadoFim,
                            @Nullable final boolean statusAtivoInicio,
                            @Nullable final boolean statusAtivoFim,
                            @Nullable final Long codMarcacaoInicio,
                            @Nullable final Long codMarcacaoFim,
                            @Nullable final LocalDateTime dataMarcacaoInicio,
                            @Nullable final LocalDateTime dataMarcacaoFim,
                            @NotNull final Duration tempoDecorridoEntreInicioFim,
                            @NotNull final Duration tempoRecomendadoTipoMarcacao,
                            @Nullable final String justificativaEstouro,
                            @Nullable final String justificativaTempoRecomendado) {
        this.codUnidade = codUnidade;
        this.nomeTipoMarcacao = nomeTipoMarcacao;
        this.iconeTipoMarcacao = iconeTipoMarcacao;
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.foiAjustadoInicio = foiAjustadoInicio;
        this.foiAjustadoFim = foiAjustadoFim;
        this.statusAtivoInicio = statusAtivoInicio;
        this.statusAtivoFim = statusAtivoFim;
        this.codMarcacaoInicio = codMarcacaoInicio;
        this.codMarcacaoFim = codMarcacaoFim;
        this.dataMarcacaoInicio = dataMarcacaoInicio;
        this.dataMarcacaoFim = dataMarcacaoFim;
        this.tempoDecorridoEntreInicioFim = tempoDecorridoEntreInicioFim;
        this.tempoRecomendadoTipoMarcacao = tempoRecomendadoTipoMarcacao;
        this.justificativaEstouro = justificativaEstouro;
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
        this.temJustificativaEstouro = justificativaEstouro != null;
        this.temJustificativaTempoRecomendado = justificativaTempoRecomendado != null;
    }

    @NotNull
    public Long getCodUnidade() { return codUnidade; }

    @NotNull
    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    @NotNull
    public String getIconeTipoMarcacao() { return iconeTipoMarcacao; }

    @NotNull
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @Nullable
    public boolean isFoiAjustadoInicio() { return foiAjustadoInicio; }

    @Nullable
    public boolean isFoiAjustadoFim() { return foiAjustadoFim; }

    @Nullable
    public boolean isStatusAtivoInicio() { return statusAtivoInicio; }

    @Nullable
    public boolean isStatusAtivoFim() { return statusAtivoFim; }

    @Nullable
    public Long getCodMarcacaoInicio() { return codMarcacaoInicio; }

    @Nullable
    public Long getCodMarcacaoFim() { return codMarcacaoFim; }

    @Nullable
    public LocalDateTime getDataMarcacaoInicio() {
        return dataMarcacaoInicio;
    }

    @Nullable
    public LocalDateTime getDataMarcacaoFim() {
        return dataMarcacaoFim;
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
