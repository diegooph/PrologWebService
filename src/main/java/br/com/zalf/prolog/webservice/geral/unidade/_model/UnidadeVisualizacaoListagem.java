package br.com.zalf.prolog.webservice.geral.unidade._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class UnidadeVisualizacaoListagem {
    @NotNull
    public final Long codUnidade;
    @NotNull
    public final String nomeUnidade;
    public final int totalColaboradores;
    @NotNull
    public final Long codRegional;
    @NotNull
    public final String nomeRegional;
    @NotNull
    public final String timezoneUnidade;
    @NotNull
    public final LocalDateTime dataHoraCadastroUnidade;
    public final boolean unidadeAtiva;
    @Nullable
    public final String codAuxiliar;
    @Nullable
    public final String latitudeUnidade;
    @Nullable
    public final String longitudeUnidade;
}
