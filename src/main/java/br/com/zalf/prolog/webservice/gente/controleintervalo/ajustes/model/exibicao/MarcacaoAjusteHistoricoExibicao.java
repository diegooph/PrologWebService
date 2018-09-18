package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa.JustificativaAjuste;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

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
     * Uma descrição, humanamente legível, especificando o ajuste que foi realizado.
     */
    private String descricaoAcaoRealizada;

    public MarcacaoAjusteHistoricoExibicao() {
        // TODO - Utilizar uma Factory para dado um ajuste, gerar a descrição correta.
    }

    @NotNull
    public static MarcacaoAjusteHistoricoExibicao createDummy() {
        final MarcacaoAjusteHistoricoExibicao ajusteHistorico = new MarcacaoAjusteHistoricoExibicao();
        ajusteHistorico.setNomeColaboradorAjuste("Zalf Sistemas");
        ajusteHistorico.setNomeJustificativaAjuste("Esqueceu");
        ajusteHistorico.setObservacaoAjuste("Precisei atualizar a hora de marcação");
        ajusteHistorico.setDataHoraAjuste(LocalDateTime.now());
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
