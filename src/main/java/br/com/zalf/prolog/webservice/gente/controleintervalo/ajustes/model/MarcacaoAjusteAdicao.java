package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteAdicao extends MarcacaoAjuste {
    private Long codMarcacaoVinculo;
    private Long codColaborador;
    private Long codTipoIntervaloMarcado;
    private LocalDateTime dataHoraInserida;

    public MarcacaoAjusteAdicao() {
        super(TipoMarcacaoAjuste.ADICAO);
    }

    @NotNull
    public static MarcacaoAjusteAdicao createDummy() {
        final MarcacaoAjusteAdicao ajusteAdicao = new MarcacaoAjusteAdicao();
        ajusteAdicao.setCodMarcacaoVinculo(101010L);
        ajusteAdicao.setCodColaborador(2272L);
        ajusteAdicao.setCodTipoIntervaloMarcado(10L);
        ajusteAdicao.setDataHoraInserida(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setCodJustificativaAjuste(5L);
        ajusteAdicao.setObservacaoAjuste("Dummy Data");
        ajusteAdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return ajusteAdicao;
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
