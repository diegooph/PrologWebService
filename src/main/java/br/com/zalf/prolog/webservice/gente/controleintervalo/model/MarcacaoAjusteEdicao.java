package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteEdicao extends MarcacaoAjuste {

    private LocalDateTime dataHoraOriginal;
    private LocalDateTime dataHoraNova;

    public MarcacaoAjusteEdicao() {
        super(MarcacaoAjuste.MARCACAO_AJUSTE_EDICAO);
    }

    public LocalDateTime getDataHoraOriginal() {
        return dataHoraOriginal;
    }

    public void setDataHoraOriginal(final LocalDateTime dataHoraOriginal) {
        this.dataHoraOriginal = dataHoraOriginal;
    }

    public LocalDateTime getDataHoraNova() {
        return dataHoraNova;
    }

    public void setDataHoraNova(final LocalDateTime dataHoraNova) {
        this.dataHoraNova = dataHoraNova;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteEdicao{" +
                "dataHoraOriginal=" + dataHoraOriginal +
                ", dataHoraNova=" + dataHoraNova +
                '}';
    }
}
