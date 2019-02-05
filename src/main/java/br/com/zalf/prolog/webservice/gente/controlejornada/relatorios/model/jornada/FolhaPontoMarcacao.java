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
    private final boolean descontaJornadaBruta;
    @Exclude
    private final boolean descontaJornadaLiquida;
    @Exclude
    private final long diferencaInicioFimEmSegundos;
    @Exclude
    private final long tempoNoturnoEmSegundos;

    public FolhaPontoMarcacao(@Nullable final LocalDateTime dataHoraInicio,
                              @Nullable final LocalDateTime dataHoraFim,
                              @NotNull final Long codTipoMarcacao,
                              @NotNull final Long codTipoMarcacaoPorUnidade,
                              final boolean trocouDia,
                              final boolean marcacaoInicioAjustada,
                              final boolean marcacaoFimAjustada,
                              final boolean descontaJornadaBruta,
                              final boolean descontaJornadaLiquida,
                              final long diferencaInicioFimEmSegundos,
                              final long tempoNoturnoEmSegundos) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.trocouDia = trocouDia;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
        this.descontaJornadaBruta = descontaJornadaBruta;
        this.descontaJornadaLiquida = descontaJornadaLiquida;
        this.diferencaInicioFimEmSegundos = diferencaInicioFimEmSegundos;
        this.tempoNoturnoEmSegundos = tempoNoturnoEmSegundos;
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
                false,
                true,
                false,
                21000,
                1000);
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

    public long getDiferencaInicioFimEmSegundos() {
        return diferencaInicioFimEmSegundos;
    }

    public long getTempoNoturnoEmSegundos() {
        return tempoNoturnoEmSegundos;
    }

    public boolean isDescontaJornadaBruta() {
        return descontaJornadaBruta;
    }

    public boolean isDescontaJornadaLiquida() {
        return descontaJornadaLiquida;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean fitIn(@NotNull final FolhaPontoJornada jornada) {
        return this.dataHoraInicio.isAfter(jornada.getDataHoraInicioJornada())
                && this.dataHoraFim.isBefore(jornada.getDataHoraFimJornada());
    }
}