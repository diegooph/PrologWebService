package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistItensNok {
    private Long codUnidadeChecklist;
    private Long codChecklistRealizado;
    private String cpfColaboradorRealizacao;
    private String placaVeiculoChecklist;
    private Long kmColetadoChecklist;
    private String tipoChecklist;
    private LocalDateTime dataHoraRealizacaoUtc;
    private List<PerguntasNok> perguntasNok;

    public ChecklistItensNok() {
    }

    @NotNull
    public static ChecklistItensNok getDummy() {
        final ChecklistItensNok checklistItensNok = new ChecklistItensNok();
        checklistItensNok.setCodUnidadeChecklist(5L);
        checklistItensNok.setCodChecklistRealizado(13873L);
        checklistItensNok.setCpfColaboradorRealizacao("03383283194");
        checklistItensNok.setPlacaVeiculoChecklist("PRO0001");
        checklistItensNok.setKmColetadoChecklist(54920L);
        checklistItensNok.setTipoChecklist("SAIDA");
        checklistItensNok.setDataHoraRealizacaoUtc(Now.localDateTimeUtc());
        final List<PerguntasNok> respostasNok = new ArrayList<>();
        respostasNok.add(PerguntasNok.getDummy());
        checklistItensNok.setPerguntasNok(respostasNok);
        return checklistItensNok;
    }

    public Long getCodUnidadeChecklist() {
        return codUnidadeChecklist;
    }

    public void setCodUnidadeChecklist(final Long codUnidadeChecklist) {
        this.codUnidadeChecklist = codUnidadeChecklist;
    }

    public Long getCodChecklistRealizado() {
        return codChecklistRealizado;
    }

    public void setCodChecklistRealizado(final Long codChecklistRealizado) {
        this.codChecklistRealizado = codChecklistRealizado;
    }

    public String getCpfColaboradorRealizacao() {
        return cpfColaboradorRealizacao;
    }

    public void setCpfColaboradorRealizacao(final String cpfColaboradorRealizacao) {
        this.cpfColaboradorRealizacao = cpfColaboradorRealizacao;
    }

    public String getPlacaVeiculoChecklist() {
        return placaVeiculoChecklist;
    }

    public void setPlacaVeiculoChecklist(final String placaVeiculoChecklist) {
        this.placaVeiculoChecklist = placaVeiculoChecklist;
    }

    public Long getKmColetadoChecklist() {
        return kmColetadoChecklist;
    }

    public void setKmColetadoChecklist(final Long kmColetadoChecklist) {
        this.kmColetadoChecklist = kmColetadoChecklist;
    }

    public String getTipoChecklist() {
        return tipoChecklist;
    }

    public void setTipoChecklist(final String tipoChecklist) {
        this.tipoChecklist = tipoChecklist;
    }

    public LocalDateTime getDataHoraRealizacaoUtc() {
        return dataHoraRealizacaoUtc;
    }

    public void setDataHoraRealizacaoUtc(final LocalDateTime dataHoraRealizacaoUtc) {
        this.dataHoraRealizacaoUtc = dataHoraRealizacaoUtc;
    }

    public List<PerguntasNok> getPerguntasNok() {
        return perguntasNok;
    }

    public void setPerguntasNok(final List<PerguntasNok> perguntasNok) {
        this.perguntasNok = perguntasNok;
    }
}
