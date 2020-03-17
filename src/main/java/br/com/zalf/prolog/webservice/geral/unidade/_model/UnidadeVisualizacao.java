package br.com.zalf.prolog.webservice.geral.unidade._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class UnidadeVisualizacao {

    @NotNull
    public Long codUnidade;

    @NotNull
    public String nomeUnidade;

    @NotNull
    public Integer totalColaboradores;

    @NotNull
    public Regional regional;

    @NotNull
    public String timezoneUnidade;

    @NotNull
    public LocalDateTime dataHoraCadastroUnidade;

    @NotNull
    public Boolean unidadeAtiva;

    @Nullable
    public String codAuxiliar;

    @Nullable
    public String latitudeUnidade;

    @Nullable
    public String longitudeUnidade;

    public UnidadeVisualizacao(
            @NotNull final Long codUnidade,
            @NotNull final String nome,
            @NotNull final Integer totalColaboradores,
            @NotNull final Regional regional,
            @NotNull final String timezoneUnidade,
            @NotNull final LocalDateTime dataHoraCadastroUnidade,
            @NotNull final Boolean unidadeAtiva,
            @Nullable final String codAuxiliar,
            @Nullable final String latitudeUnidade,
            @Nullable final String longitudeUnidade) {
        this.codUnidade = codUnidade;
        this.nomeUnidade = nome;
        this.totalColaboradores = totalColaboradores;
        this.regional = regional;
        this.timezoneUnidade = timezoneUnidade;
        this.dataHoraCadastroUnidade = dataHoraCadastroUnidade;
        this.unidadeAtiva = unidadeAtiva;
        this.codAuxiliar = codAuxiliar;
        this.latitudeUnidade = latitudeUnidade;
        this.longitudeUnidade = longitudeUnidade;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(@NotNull final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(@NotNull final String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    @NotNull
    public Integer getTotalColaboradores() {
        return totalColaboradores;
    }

    public void setTotalColaboradores(@NotNull final Integer totalColaboradores) {
        this.totalColaboradores = totalColaboradores;
    }

    @NotNull
    public Regional getRegional() {
        return regional;
    }

    public void setRegional(@NotNull final Regional regional) {
        this.regional = regional;
    }

    @NotNull
    public String getTimezoneUnidade() {
        return timezoneUnidade;
    }

    public void setTimezoneUnidade(@NotNull final String timezoneUnidade) {
        this.timezoneUnidade = timezoneUnidade;
    }

    @NotNull
    public LocalDateTime getDataHoraCadastroUnidade() {
        return dataHoraCadastroUnidade;
    }

    public void setDataHoraCadastroUnidade(@NotNull final LocalDateTime dataHoraCadastroUnidade) {
        this.dataHoraCadastroUnidade = dataHoraCadastroUnidade;
    }

    @NotNull
    public Boolean getUnidadeAtiva() {
        return unidadeAtiva;
    }

    public void setUnidadeAtiva(@NotNull final Boolean unidadeAtiva) {
        this.unidadeAtiva = unidadeAtiva;
    }

    @Nullable
    public String getCodAuxiliar() {
        return codAuxiliar;
    }

    public void setCodAuxiliar(@Nullable final String codAuxiliar) {
        this.codAuxiliar = codAuxiliar;
    }

    @Nullable
    public String getLatitudeUnidade() {
        return latitudeUnidade;
    }

    public void setLatitudeUnidade(@Nullable final String latitudeUnidade) {
        this.latitudeUnidade = latitudeUnidade;
    }

    @Nullable
    public String getLongitudeUnidade() {
        return longitudeUnidade;
    }

    public void setLongitudeUnidade(@Nullable final String longitudeUnidade) {
        this.longitudeUnidade = longitudeUnidade;
    }

}
