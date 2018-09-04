package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteAdicao extends MarcacaoAjuste {

    private Long codMarcacaoVinculo;
    private Long codColaborador;
    private Long codTipoIntervaloMarcado;
    private LocalDateTime dataHoraInserida;

    public MarcacaoAjusteAdicao() {
        super(MarcacaoAjuste.MARCACAO_AJUSTE_ADICAO);
    }

    public Long getCodMarcacaoVinculo() {
        return codMarcacaoVinculo;
    }

    public void setCodMarcacaoVinculo(final Long codMarcacaoVinculo) {
        this.codMarcacaoVinculo = codMarcacaoVinculo;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public void setCodColaborador(final Long codColaborador) {
        this.codColaborador = codColaborador;
    }

    public Long getCodTipoIntervaloMarcado() {
        return codTipoIntervaloMarcado;
    }

    public void setCodTipoIntervaloMarcado(final Long codTipoIntervaloMarcado) {
        this.codTipoIntervaloMarcado = codTipoIntervaloMarcado;
    }

    public LocalDateTime getDataHoraInserida() {
        return dataHoraInserida;
    }

    public void setDataHoraInserida(final LocalDateTime dataHoraInserida) {
        this.dataHoraInserida = dataHoraInserida;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAdicao{" +
                "codMarcacaoVinculo=" + codMarcacaoVinculo +
                ", codColaborador=" + codColaborador +
                ", codTipoIntervaloMarcado=" + codTipoIntervaloMarcado +
                ", dataHoraInserida=" + dataHoraInserida +
                '}';
    }
}
