package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @NotNull
    public static MarcacaoConsolidada createDummy() {
        final MarcacaoConsolidada consolidada = new MarcacaoConsolidada();
        consolidada.setData(ProLogDateParser.toLocalDate("2018-09-04"));
        consolidada.setTotalMarcacoesDia(10);
        consolidada.setTotalInconsistenciasDia(5);
        final List<MarcacaoConsolidadaDiaColaborador> consolidadaColaboradores = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            consolidadaColaboradores.add(MarcacaoConsolidadaDiaColaborador.createDummy());
        }
        consolidada.setConsolidadaColaboradores(consolidadaColaboradores);
        return consolidada;
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
