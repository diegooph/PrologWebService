package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

/**
 * Representa a quantidade de marcações que um colaborador tem em um dado dia. É utilizado em conjunto com o
 * {@link ConsolidadoMarcacoesDia consolidado} para compor a listagem exibida no Sistema Web.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacoesDiaColaborador {
    /**
     * Código do {@link Colaborador colaborador} do qual as informações são referentes.
     */
    private Long codColaborador;

    /**
     * Nome do {@link Colaborador colaborador} do qual as informações são referentes.
     */
    private String nomeColaborador;

    /**
     * Quantidade de marcações que o colaborador marcou (ou que foram marcadas em seu nome) no dia.
     */
    private int qtdMarcacoesDia;

    /**
     * Quantidade de inconsistências geradas no dia atual.
     */
    private int qtdInconsistenciasDia;

    public MarcacoesDiaColaborador() {

    }

    @NotNull
    public static MarcacoesDiaColaborador createDummy() {
        final MarcacoesDiaColaborador consolidadaDiaColaborador = new MarcacoesDiaColaborador();
        consolidadaDiaColaborador.setCodColaborador(2272L);
        consolidadaDiaColaborador.setNomeColaborador("Zalf Sistemas");
        consolidadaDiaColaborador.setQtdMarcacoesDia(10);
        consolidadaDiaColaborador.setQtdInconsistenciasDia(5);
        return consolidadaDiaColaborador;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public void setCodColaborador(final Long codColaborador) {
        this.codColaborador = codColaborador;
    }

    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public void setNomeColaborador(final String nomeColaborador) {
        this.nomeColaborador = nomeColaborador;
    }

    public int getQtdMarcacoesDia() {
        return qtdMarcacoesDia;
    }

    public void setQtdMarcacoesDia(final int qtdMarcacoesDia) {
        this.qtdMarcacoesDia = qtdMarcacoesDia;
    }

    public int getQtdInconsistenciasDia() {
        return qtdInconsistenciasDia;
    }

    public void setQtdInconsistenciasDia(final int qtdInconsistenciasDia) {
        this.qtdInconsistenciasDia = qtdInconsistenciasDia;
    }

    @Override
    public String toString() {
        return "MarcacaoConsolidadaDiaColaborador{" +
                "codColaborador=" + codColaborador +
                ", nomeColaborador='" + nomeColaborador + '\'' +
                ", qtdMarcacoesDia=" + qtdMarcacoesDia +
                ", qtdInconsistenciasDia=" + qtdInconsistenciasDia +
                '}';
    }
}