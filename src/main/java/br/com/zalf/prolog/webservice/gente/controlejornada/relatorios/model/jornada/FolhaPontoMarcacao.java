package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoMarcacao {
    @Nullable
    private final LocalDateTime dataHoraInicio;
    @Nullable
    private final LocalDateTime dataHoraFim;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final Long codTipoMarcacaoPorUnidade;
    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;

    @Exclude
    @Nullable
    private Long diferencaoInicioFimEmSegundos;
    @Exclude
    @Nullable
    private Long tempoNoturnoEmSegundos;

    public FolhaPontoMarcacao(@Nullable final LocalDateTime dataHoraInicio,
                              @Nullable final LocalDateTime dataHoraFim,
                              @NotNull final Long codTipoMarcacao,
                              @NotNull final Long codTipoMarcacaoPorUnidade,
                              final boolean trocouDia,
                              final boolean marcacaoInicioAjustada,
                              final boolean marcacaoFimAjustada) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.trocouDia = trocouDia;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
    }

    @NotNull
    static FolhaPontoMarcacao getDummy() {
        return new FolhaPontoMarcacao(
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L,
                false,
                true,
                false);
    }

    @Nullable
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    @Nullable
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public Long getCodTipoMarcacaoPorUnidade() {
        return codTipoMarcacaoPorUnidade;
    }

    public boolean isTrocouDia() {
        return trocouDia;
    }

    public boolean isMarcacaoInicioAjustada() {
        return marcacaoInicioAjustada;
    }

    public boolean isMarcacaoFimAjustada() {
        return marcacaoFimAjustada;
    }

    @Nullable
    public Long getDiferencaoInicioFimEmSegundos() {
        return diferencaoInicioFimEmSegundos;
    }

    public void setDiferencaoInicioFimEmSegundos(@Nullable final Long diferencaoInicioFimEmSegundos) {
        this.diferencaoInicioFimEmSegundos = diferencaoInicioFimEmSegundos;
    }

    @Nullable
    public Long getTempoNoturnoEmSegundos() {
        return tempoNoturnoEmSegundos;
    }

    public void setTempoNoturnoEmSegundos(@Nullable final Long tempoNoturnoEmSegundos) {
        this.tempoNoturnoEmSegundos = tempoNoturnoEmSegundos;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean fitIn(@NotNull final FolhaPontoJornada jornada) {
        return this.dataHoraInicio.isAfter(jornada.getDataHoraInicioJornada())
                && this.dataHoraFim.isBefore(jornada.getDataHoraFimJornada());
    }
}