package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoInicioFim;

import java.time.LocalDateTime;

/**
 * Created on 18/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class InconsistenciaSemVinculo extends MarcacaoInconsistencia {
    private Long codMarcacaoSemVinculo;
    private TipoInicioFim tipoMarcacao;
    private LocalDateTime dataHoraMarcacao;
    private String nomeColaboradorMarcacao;

    public InconsistenciaSemVinculo() {
        super(TipoInconsistenciaMarcacao.SEM_VINCULO);
    }

    public Long getCodMarcacaoSemVinculo() {
        return codMarcacaoSemVinculo;
    }

    public void setCodMarcacaoSemVinculo(final Long codMarcacaoSemVinculo) {
        this.codMarcacaoSemVinculo = codMarcacaoSemVinculo;
    }

    public TipoInicioFim getTipoMarcacao() {
        return tipoMarcacao;
    }

    public void setTipoMarcacao(final TipoInicioFim tipoMarcacao) {
        this.tipoMarcacao = tipoMarcacao;
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