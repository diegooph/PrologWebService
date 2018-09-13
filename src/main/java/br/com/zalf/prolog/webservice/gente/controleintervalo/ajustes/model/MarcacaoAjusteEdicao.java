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
public final class MarcacaoAjusteEdicao extends MarcacaoAjuste {
    private LocalDateTime dataHoraOriginal;
    private LocalDateTime dataHoraNovaInserida;

    public MarcacaoAjusteEdicao() {
        super(TipoMarcacaoAjuste.EDICAO);
    }

    @NotNull
    public static MarcacaoAjusteEdicao createDummy() {
        final MarcacaoAjusteEdicao ajusteEdicao = new MarcacaoAjusteEdicao();
        ajusteEdicao.setDataHoraOriginal(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteEdicao.setDataHoraNovaInserida(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteEdicao.setCodJustificativaAjuste(5L);
        ajusteEdicao.setObservacaoAjuste("Dummy Data");
        ajusteEdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return ajusteEdicao;
    }

    public LocalDateTime getDataHoraOriginal() {
        return dataHoraOriginal;
    }

    public void setDataHoraOriginal(final LocalDateTime dataHoraOriginal) {
        this.dataHoraOriginal = dataHoraOriginal;
    }

    public LocalDateTime getDataHoraNovaInserida() {
        return dataHoraNovaInserida;
    }

    public void setDataHoraNovaInserida(final LocalDateTime dataHoraNovaInserida) {
        this.dataHoraNovaInserida = dataHoraNovaInserida;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteEdicao{" +
                "dataHoraOriginal=" + dataHoraOriginal +
                ", dataHoraNovaInserida=" + dataHoraNovaInserida +
                '}';
    }
}
