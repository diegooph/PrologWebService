package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class InconsistenciaFimAntesInicio extends MarcacaoInconsistencia {
    private Long codMarcacaoInicio;
    private LocalDateTime dataHoraMarcacaoInicio;
    private Long codMarcacaoFim;
    private LocalDateTime dataHoraMarcacaoFim;
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
    }

    public String getNomeColaboradorMarcacao() {
        return nomeColaboradorMarcacao;
    }

    public void setNomeColaboradorMarcacao(final String nomeColaboradorMarcacao) {
        this.nomeColaboradorMarcacao = nomeColaboradorMarcacao;
    }
}