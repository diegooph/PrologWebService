package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 11/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class FolhaPontoMarcacaoDb {
    @NotNull
    private final String cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final Long codTipoMarcacaoPorUnidade;
    @Nullable
    private final LocalDateTime dataHoraMarcacaoInicio;
    @Nullable
    private final LocalDateTime dataHoraMarcacaoFim;
    @Nullable
    private final Long diferencaoInicioFimEmSegundos;
    private final long tempoNoturnoEmSegundos;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;
    private final boolean trocouDia;
    private final boolean tipoJornada;

    public FolhaPontoMarcacaoDb(@NotNull final String cpfColaborador,
                                @NotNull final String nomeColaborador,
                                @NotNull final Long codTipoMarcacao,
                                @NotNull final Long codTipoMarcacaoPorUnidade,
                                @Nullable final LocalDateTime dataHoraMarcacaoInicio,
                                @Nullable final LocalDateTime dataHoraMarcacaoFim,
                                @Nullable final Long diferencaoInicioFimEmSegundos,
                                final long tempoNoturnoEmSegundos,
                                final boolean marcacaoInicioAjustada,
                                final boolean marcacaoFimAjustada,
                                final boolean trocouDia,
                                final boolean tipoJornada) {
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.dataHoraMarcacaoInicio = dataHoraMarcacaoInicio;
        this.dataHoraMarcacaoFim = dataHoraMarcacaoFim;
        this.diferencaoInicioFimEmSegundos = diferencaoInicioFimEmSegundos;
        this.tempoNoturnoEmSegundos = tempoNoturnoEmSegundos;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
        this.trocouDia = trocouDia;
        this.tipoJornada = tipoJornada;
    }

    @NotNull
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public Long getCodTipoMarcacaoPorUnidade() {
        return codTipoMarcacaoPorUnidade;
    }

    @Nullable
    public LocalDateTime getDataHoraMarcacaoInicio() {
        return dataHoraMarcacaoInicio;
    }

    @Nullable
    public LocalDateTime getDataHoraMarcacaoFim() {
        return dataHoraMarcacaoFim;
    }

    @Nullable
    public Long getDiferencaoInicioFimEmSegundos() {
        return diferencaoInicioFimEmSegundos;
    }

    public long getTempoNoturnoEmSegundos() {
        return tempoNoturnoEmSegundos;
    }

    public boolean isMarcacaoInicioAjustada() {
        return marcacaoInicioAjustada;
    }

    public boolean isMarcacaoFimAjustada() {
        return marcacaoFimAjustada;
    }

    public boolean isTrocouDia() {
        return trocouDia;
    }

    public boolean isTipoJornada() {
        return tipoJornada;
    }
}
