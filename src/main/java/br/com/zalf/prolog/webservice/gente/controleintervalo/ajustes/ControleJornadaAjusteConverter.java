package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.ConsolidadoMarcacoesDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.MarcacoesDiaColaborador;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 26/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ControleJornadaAjusteConverter {

    public ControleJornadaAjusteConverter() {
        throw new IllegalStateException(ControleJornadaAjusteConverter.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    static List<ConsolidadoMarcacoesDia> createConsolidadoMarcacoesDia(@NotNull final ResultSet rSet) throws Throwable {
        final List<ConsolidadoMarcacoesDia> dias = new ArrayList<>();
        ConsolidadoMarcacoesDia consolidado = new ConsolidadoMarcacoesDia();
        boolean primeiraLinha = true;
        while (rSet.next()) {
            final LocalDate dia = rSet.getObject("DIA", LocalDate.class);
            if (primeiraLinha) {
                consolidado.setDia(dia);
                consolidado.setTotalInconsistenciasDia(-1);
                consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                consolidado.setMarcacoesColaboradores(new ArrayList<>());
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
            } else {
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
                if (!consolidado.getDia().equals(dia)) {
                    dias.add(consolidado);

                    // Cria novo dia.
                    consolidado = new ConsolidadoMarcacoesDia();
                    consolidado.setDia(dia);
                    consolidado.setTotalInconsistenciasDia(-1);
                    consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                    consolidado.setMarcacoesColaboradores(new ArrayList<>());
                }
            }
            primeiraLinha = false;
        }
        return dias;
    }

    @NotNull
    private static MarcacoesDiaColaborador createMarcacoesColaborador(@NotNull final ResultSet rSet) throws Throwable {
        final MarcacoesDiaColaborador marcacoes = new MarcacoesDiaColaborador();
        marcacoes.setCodColaborador(rSet.getLong("COD_COLABORADOR"));
        marcacoes.setNomeColaborador(rSet.getString("NOME_COLABORADOR"));
        marcacoes.setQtdInconsistenciasDia(-1);
        marcacoes.setQtdMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_COLABORADOR_DIA"));
        return marcacoes;
    }
}