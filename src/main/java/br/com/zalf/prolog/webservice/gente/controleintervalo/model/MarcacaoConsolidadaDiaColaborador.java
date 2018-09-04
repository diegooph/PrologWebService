package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoConsolidadaDiaColaborador {

    private Long codColaborador;
    private String nomeColaborador;
    private int qtdMarcacoesDia;
    private int qtdInconsistenciasDia;

    public MarcacaoConsolidadaDiaColaborador() {
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
