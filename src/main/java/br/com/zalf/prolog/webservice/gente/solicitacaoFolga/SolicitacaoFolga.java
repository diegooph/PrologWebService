package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.util.Date;

/**
 * Created by luiz on 1/27/16.
 * Solicitação de solicitacao_folga com os dados do solicitante e do responsável
 */
public class SolicitacaoFolga {
    public static final String STATUS_PENDENTE = "PENDENTE";
    public static final String STATUS_REJEITADA = "REJEITADA";
    public static final String STATUS_AUTORIZADA = "AUTORIZADA";
    public static final String PERIODO_DIA_TODO = "DIA_TODO";
    public static final String PERIODO_MANHA = "MANHA";
    public static final String PERIODO_TARDE = "TARDE";
    public static final String PERIODO_NOITE = "NOITE";

    private Long codigo;

    /**
     * Colaborador que fez a solicitação
     */
    private Colaborador colaborador;

    /**
     * Colaborador que realizou o feedback
     */
    private Colaborador colaboradorFeedback;

    private Date dataSolicitacao;
    private Date dataFolga;
    private Date dataFeedback;
    private String motivoFolga;
    private String justificativaFeedback;
    private String status;
    private String periodo;

    public SolicitacaoFolga() {

    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Colaborador getColaboradorFeedback() {
        return colaboradorFeedback;
    }

    public void setColaboradorFeedback(Colaborador colaboradorFeedback) {
        this.colaboradorFeedback = colaboradorFeedback;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Date getDataFolga() {
        return dataFolga;
    }

    public void setDataFolga(Date dataFolga) {
        this.dataFolga = dataFolga;
    }

    public Date getDataFeedback() {
        return dataFeedback;
    }

    public void setDataFeedback(Date dataFeedback) {
        this.dataFeedback = dataFeedback;
    }

    public String getMotivoFolga() {
        return motivoFolga;
    }

    public void setMotivoFolga(String motivoFolga) {
        this.motivoFolga = motivoFolga;
    }

    public String getJustificativaFeedback() {
        return justificativaFeedback;
    }

    public void setJustificativaFeedback(String justificativaFeedback) {
        this.justificativaFeedback = justificativaFeedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "SolicitacaoFolga{" +
                "codigo=" + codigo +
                ", colaborador=" + colaborador +
                ", colaboradorFeedback=" + colaboradorFeedback +
                ", dataSolicitacao=" + dataSolicitacao +
                ", dataFolga=" + dataFolga +
                ", dataFeedback=" + dataFeedback +
                ", motivoFolga='" + motivoFolga + '\'' +
                ", justificativaFeedback='" + justificativaFeedback + '\'' +
                ", status='" + status + '\'' +
                ", periodo='" + periodo + '\'' +
                '}';
    }
}