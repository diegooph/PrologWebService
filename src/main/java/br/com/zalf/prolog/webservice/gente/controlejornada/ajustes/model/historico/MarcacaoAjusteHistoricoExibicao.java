package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.historico;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.justificativa.JustificativaAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.TipoAcaoAjuste;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Classe utilizada para representar o histórico de um ajuste que temos salvo em banco, seja ele uma edição, ativação
 * ou qualquer outro.
 *
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteHistoricoExibicao {
    /**
     * Nome do {@link Colaborador colaborador} responsável por realizar o ajuste.
     */
    private String nomeColaboradorAjuste;

    /**
     * O nome da {@link JustificativaAjuste justificativa} que foi utilizada para embasar o ajuste realizado.
     */
    private String nomeJustificativaAjuste;

    /**
     * A observação que foi adicionada no momento do ajuste.
     */
    private String observacaoAjuste;

    /**
     * Data e hora da realização do ajuste.
     */
    private LocalDateTime dataHoraAjuste;

    /**
     * Data e hora antiga da marcação, que existia antes do ajuste acontecer.
     */
    private LocalDateTime dataHoraAntiga;

    /**
     * Data e hora nova da marcação, fornecida no momento do ajuste ou a mesma de {@link #dataHoraAntiga} caso o ajuste
     * não tenha sido uma {@link TipoAcaoAjuste#EDICAO edição}.
     */
    private LocalDateTime dataHoraNova;

    /**
     * O {@link TipoInicioFim tipo} da marcação que foi ajustada.
     */
    private TipoInicioFim tipoInicioFimMarcacao;

    /**
     * A {@link TipoAcaoAjuste ação de ajuste} que foi executada na marcação.
     */
    private TipoAcaoAjuste tipoAcaoAjuste;

    /**
     * Uma descrição, humanamente legível, especificando o ajuste que foi realizado.
     */
    private String descricaoAcaoRealizada;

    public MarcacaoAjusteHistoricoExibicao() {

    }

    @NotNull
    public static MarcacaoAjusteHistoricoExibicao createDummy() {
        final MarcacaoAjusteHistoricoExibicao ajusteHistorico = new MarcacaoAjusteHistoricoExibicao();
        ajusteHistorico.setNomeColaboradorAjuste("Zalf Sistemas");
        ajusteHistorico.setNomeJustificativaAjuste("Esqueceu");
        ajusteHistorico.setObservacaoAjuste("Precisei atualizar a hora de marcação");
        ajusteHistorico.setDataHoraAntiga(LocalDateTime.now());
        ajusteHistorico.setDataHoraNova(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
        ajusteHistorico.setDataHoraAjuste(LocalDateTime.now().plus(1, ChronoUnit.DAYS));
        ajusteHistorico.setTipoAcaoAjuste(TipoAcaoAjuste.EDICAO);
        ajusteHistorico.setTipoInicioFimMarcacao(TipoInicioFim.MARCACAO_INICIO);
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

    public LocalDateTime getDataHoraAntiga() {
        return dataHoraAntiga;
    }

    public void setDataHoraAntiga(final LocalDateTime dataHoraAntiga) {
        this.dataHoraAntiga = dataHoraAntiga;
    }

    public LocalDateTime getDataHoraNova() {
        return dataHoraNova;
    }

    public void setDataHoraNova(final LocalDateTime dataHoraNova) {
        this.dataHoraNova = dataHoraNova;
    }

    public TipoInicioFim getTipoInicioFimMarcacao() {
        return tipoInicioFimMarcacao;
    }

    public void setTipoInicioFimMarcacao(final TipoInicioFim tipoInicioFimMarcacao) {
        this.tipoInicioFimMarcacao = tipoInicioFimMarcacao;
    }

    public TipoAcaoAjuste getTipoAcaoAjuste() {
        return tipoAcaoAjuste;
    }

    public void setTipoAcaoAjuste(final TipoAcaoAjuste tipoAcaoAjuste) {
        this.tipoAcaoAjuste = tipoAcaoAjuste;
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
