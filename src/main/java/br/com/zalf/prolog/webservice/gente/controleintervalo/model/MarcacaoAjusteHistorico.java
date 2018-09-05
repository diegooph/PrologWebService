package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteHistorico {
    private String nomeColaboradorAjuste;
    private String nomeJustificativaAjuste;
    private String observacaoAjuste;
    private LocalDateTime dataHoraAjuste;
    private String descricaoAcaoRealizada;

    public MarcacaoAjusteHistorico() {
        // TODO - Utilizar uma Factory para dado um ajuste, gerar a descrição correta.
    }

    @NotNull
    public static MarcacaoAjusteHistorico createDummy() {
        final MarcacaoAjusteHistorico ajusteHistorico = new MarcacaoAjusteHistorico();
        ajusteHistorico.setNomeColaboradorAjuste("Zalf Sistemas");
        ajusteHistorico.setNomeJustificativaAjuste("Esqueceu");
        ajusteHistorico.setObservacaoAjuste("Precisei atualizar a hora de marcação");
        ajusteHistorico.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteHistorico.setDescricaoAcaoRealizada("atualizou a marcação do colaborador");
        return ajusteHistorico;
    }

    public String getNomeColaboradorAjuste() {
        return nomeColaboradorAjuste;
    }

    public void setNomeColaboradorAjuste(final String nomeColaboradorAjuste) {
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
    }

    public String getNomeJustificativaAjuste() {
        return nomeJustificativaAjuste;
    }

    public void setNomeJustificativaAjuste(final String nomeJustificativaAjuste) {
        this.nomeJustificativaAjuste = nomeJustificativaAjuste;
    }

    public String getObservacaoAjuste() {
        return observacaoAjuste;
    }

    public void setObservacaoAjuste(final String observacaoAjuste) {
        this.observacaoAjuste = observacaoAjuste;
    }

    public LocalDateTime getDataHoraAjuste() {
        return dataHoraAjuste;
    }

    public void setDataHoraAjuste(final LocalDateTime dataHoraAjuste) {
        this.dataHoraAjuste = dataHoraAjuste;
    }

    public String getDescricaoAcaoRealizada() {
        return descricaoAcaoRealizada;
    }

    public void setDescricaoAcaoRealizada(final String descricaoAcaoRealizada) {
        this.descricaoAcaoRealizada = descricaoAcaoRealizada;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteHistorico{" +
                "nomeColaboradorAjuste='" + nomeColaboradorAjuste + '\'' +
                ", nomeJustificativaAjuste='" + nomeJustificativaAjuste + '\'' +
                ", observacaoAjuste='" + observacaoAjuste + '\'' +
                ", dataHoraAjuste=" + dataHoraAjuste +
                ", descricaoAcaoRealizada='" + descricaoAcaoRealizada + '\'' +
                '}';
    }
}
