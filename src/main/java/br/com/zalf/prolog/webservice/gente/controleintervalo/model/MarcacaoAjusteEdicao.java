package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteEdicao extends MarcacaoAjuste {
    private LocalDateTime dataHoraOriginal;
    private LocalDateTime dataHoraNovaInserida;

    public MarcacaoAjusteEdicao() {
        super(TipoMarcacaoAjuste.EDICAO);
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
