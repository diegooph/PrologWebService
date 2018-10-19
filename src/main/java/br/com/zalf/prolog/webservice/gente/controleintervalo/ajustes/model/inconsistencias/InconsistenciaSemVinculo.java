package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Classe utilizada para representar uma inconsistência onde uma marcação de início não tem fim, ou de fim não tem
 * início.
 *
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class InconsistenciaSemVinculo extends MarcacaoInconsistencia {
    /**
     * O código da marcação que não possui vínculo.
     */
    private Long codMarcacaoSemVinculo;

    /**
     * Indica se a marcação que não possui vínculo é de início ou fim.
     */
    private TipoInicioFim tipoInicioFim;

    /**
     * A data e hora da marcação que não possui vínculo.
     */
    private LocalDateTime dataHoraMarcacao;

    /**
     * O nome do colaborador que realizou a marcação.
     */
    private String nomeColaboradorMarcacao;

    public InconsistenciaSemVinculo() {
        super(TipoInconsistenciaMarcacao.SEM_VINCULO);
    }

    @NotNull
    public static InconsistenciaSemVinculo createDummy() {
        final InconsistenciaSemVinculo inconsistencia = new InconsistenciaSemVinculo();
        inconsistencia.setCodMarcacaoSemVinculo(10L);
        inconsistencia.setTipoInicioFim(TipoInicioFim.MARCACAO_INICIO);
        inconsistencia.setDataHoraMarcacao(LocalDateTime.now());
        inconsistencia.setNomeColaboradorMarcacao("João da Silva");
        inconsistencia.setDescricaoInconsistencia("ESTA É UMA INCONSISTÊNCIA CAUSADA PELA MARCAÇÃO NÃO TER VÍNCULO");
        return inconsistencia;
    }

    public Long getCodMarcacaoSemVinculo() {
        return codMarcacaoSemVinculo;
    }

    public void setCodMarcacaoSemVinculo(final Long codMarcacaoSemVinculo) {
        this.codMarcacaoSemVinculo = codMarcacaoSemVinculo;
    }

    public TipoInicioFim getTipoInicioFim() {
        return tipoInicioFim;
    }

    public void setTipoInicioFim(final TipoInicioFim tipoInicioFim) {
        this.tipoInicioFim = tipoInicioFim;
    }

    public LocalDateTime getDataHoraMarcacao() {
        return dataHoraMarcacao;
    }

    public void setDataHoraMarcacao(final LocalDateTime dataHoraMarcacao) {
        this.dataHoraMarcacao = dataHoraMarcacao;
    }

    public String getNomeColaboradorMarcacao() {
        return nomeColaboradorMarcacao;
    }

    public void setNomeColaboradorMarcacao(final String nomeColaboradorMarcacao) {
        this.nomeColaboradorMarcacao = nomeColaboradorMarcacao;
    }
}