package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoConsolidada {

    private LocalDate data;
    private int totalMarcacoesDia;
    private int totalInconsistenciasDia;
    private List<MarcacaoConsolidadaDiaColaborador> consolidadaColaboradores;

    public MarcacaoConsolidada() {
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(final LocalDate data) {
        this.data = data;
    }

    public int getTotalMarcacoesDia() {
        return totalMarcacoesDia;
    }

    public void setTotalMarcacoesDia(final int totalMarcacoesDia) {
        this.totalMarcacoesDia = totalMarcacoesDia;
    }

    public int getTotalInconsistenciasDia() {
        return totalInconsistenciasDia;
    }

    public void setTotalInconsistenciasDia(final int totalInconsistenciasDia) {
        this.totalInconsistenciasDia = totalInconsistenciasDia;
    }

    public List<MarcacaoConsolidadaDiaColaborador> getConsolidadaColaboradores() {
        return consolidadaColaboradores;
    }

    public void setConsolidadaColaboradores(final List<MarcacaoConsolidadaDiaColaborador> consolidadaColaboradores) {
        this.consolidadaColaboradores = consolidadaColaboradores;
    }

    @Override
    public String toString() {
        return "MarcacaoConsolidada{" +
                "data=" + data +
                ", totalMarcacoesDia=" + totalMarcacoesDia +
                ", totalInconsistenciasDia=" + totalInconsistenciasDia +
                ", consolidadaColaboradores=" + consolidadaColaboradores +
                '}';
    }
}
