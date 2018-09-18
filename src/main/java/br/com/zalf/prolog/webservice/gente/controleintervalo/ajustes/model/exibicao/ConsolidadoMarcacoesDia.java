package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Agrupa todas as marcações realizadas em um dia. Contendo o total de marcações naquele dia bem como o total de
 * inconsistências.
 *
 * Este objeto contém uma lista de {@link MarcacoesDiaColaborador marcações} por colaborador no dia do qual ele
 * representa.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConsolidadoMarcacoesDia {
    /**
     * O dia de refência pela qual as marcações estão agrupadas.
     */
    private LocalDate data;

    /**
     * O total de marcações no dia de referência.
     */
    private int totalMarcacoesDia;

    /**
     * O total de inconsistências no dia de referência.
     */
    private int totalInconsistenciasDia;

    /**
     * As marcações de cada que cada colaborador realizou no dia de referência.
     */
    private List<MarcacoesDiaColaborador> marcacoesColaboradores;

    public ConsolidadoMarcacoesDia() {

    }

    @NotNull
    public static ConsolidadoMarcacoesDia createDummy() {
        final ConsolidadoMarcacoesDia consolidada = new ConsolidadoMarcacoesDia();
        consolidada.setData(ProLogDateParser.toLocalDate("2018-09-04"));
        consolidada.setTotalMarcacoesDia(10);
        consolidada.setTotalInconsistenciasDia(5);
        final List<MarcacoesDiaColaborador> consolidadaColaboradores = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            consolidadaColaboradores.add(MarcacoesDiaColaborador.createDummy());
        }
        consolidada.setMarcacoesColaboradores(consolidadaColaboradores);
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

    public List<MarcacoesDiaColaborador> getMarcacoesColaboradores() {
        return marcacoesColaboradores;
    }

    public void setMarcacoesColaboradores(final List<MarcacoesDiaColaborador> marcacoesColaboradores) {
        this.marcacoesColaboradores = marcacoesColaboradores;
    }

    @Override
    public String toString() {
        return "MarcacaoConsolidada{" +
                "data=" + data +
                ", totalMarcacoesDia=" + totalMarcacoesDia +
                ", totalInconsistenciasDia=" + totalInconsistenciasDia +
                ", marcacoesColaboradores=" + marcacoesColaboradores +
                '}';
    }
}