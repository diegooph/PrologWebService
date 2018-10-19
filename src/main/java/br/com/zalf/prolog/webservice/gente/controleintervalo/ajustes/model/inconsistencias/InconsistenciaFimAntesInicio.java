package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Classe utilizada para representar uma inconsistência onde o fim de uma marcação tem data e hora anterior a marcação
 * de início vinculada.
 *
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class InconsistenciaFimAntesInicio extends MarcacaoInconsistencia {
    /**
     * O código da marcação de início.
     */
    private Long codMarcacaoInicio;

    /**
     * A data e hora da marcação de início. Essa data e hora deve ser sempre posterior a {@link #dataHoraMarcacaoFim}.
     */
    private LocalDateTime dataHoraMarcacaoInicio;

    /**
     * O código da marcação de fim.
     */
    private Long codMarcacaoFim;

    /**
     * A data e hora da marcação de fim. Essa data e hora deve ser sempre anterior a {@link #dataHoraMarcacaoInicio}.
     */
    private LocalDateTime dataHoraMarcacaoFim;

    /**
     * O nome do colaborador que realizou a marcação.
     */
    private String nomeColaboradorMarcacao;

    public InconsistenciaFimAntesInicio() {
        super(TipoInconsistenciaMarcacao.FIM_ANTES_INICIO);
    }

    @NotNull
    public static InconsistenciaFimAntesInicio createDummy() {
        final InconsistenciaFimAntesInicio inconsistencia = new InconsistenciaFimAntesInicio();
        inconsistencia.setCodMarcacaoInicio(10L);
        inconsistencia.setDataHoraMarcacaoInicio(LocalDateTime.now());
        inconsistencia.setCodMarcacaoFim(11L);
        inconsistencia.setDataHoraMarcacaoFim(LocalDateTime.now().minus(1, ChronoUnit.HOURS));
        inconsistencia.setNomeColaboradorMarcacao("João da Silva");
        inconsistencia.setDescricaoInconsistencia("ESTA É UMA INCONSISTÊNCIA CAUSADA POR UM FIM ANTES DO INÍCIO");
        return inconsistencia;
    }

    public Long getCodMarcacaoInicio() {
        return codMarcacaoInicio;
    }

    public void setCodMarcacaoInicio(final Long codMarcacaoInicio) {
        this.codMarcacaoInicio = codMarcacaoInicio;
    }

    public LocalDateTime getDataHoraMarcacaoInicio() {
        return dataHoraMarcacaoInicio;
    }

    public void setDataHoraMarcacaoInicio(final LocalDateTime dataHoraMarcacaoInicio) {
        this.dataHoraMarcacaoInicio = dataHoraMarcacaoInicio;

        if (dataHoraMarcacaoFim != null) {
            garanteInconsistencia();
        }
    }

    public Long getCodMarcacaoFim() {
        return codMarcacaoFim;
    }

    public void setCodMarcacaoFim(final Long codMarcacaoFim) {
        this.codMarcacaoFim = codMarcacaoFim;
    }

    public LocalDateTime getDataHoraMarcacaoFim() {
        return dataHoraMarcacaoFim;
    }

    public void setDataHoraMarcacaoFim(final LocalDateTime dataHoraMarcacaoFim) {
        this.dataHoraMarcacaoFim = dataHoraMarcacaoFim;

        if (dataHoraMarcacaoInicio != null) {
            garanteInconsistencia();
        }
    }

    public String getNomeColaboradorMarcacao() {
        return nomeColaboradorMarcacao;
    }

    public void setNomeColaboradorMarcacao(final String nomeColaboradorMarcacao) {
        this.nomeColaboradorMarcacao = nomeColaboradorMarcacao;
    }

    private void garanteInconsistencia() {
        if (!dataHoraMarcacaoFim.isBefore(dataHoraMarcacaoInicio)) {
            throw new IllegalStateException("A data/hora de fim: " + dataHoraMarcacaoFim + " precisa ser anterior da " +
                    "data/hora de início: " + dataHoraMarcacaoInicio);
        }
    }
}