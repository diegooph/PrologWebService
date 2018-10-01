package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao.*;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        ConsolidadoMarcacoesDia consolidado = null;
        boolean primeiraLinha = true;
        while (rSet.next()) {
            final LocalDate dia = rSet.getObject("DIA", LocalDate.class);
            if (primeiraLinha) {
                consolidado = new ConsolidadoMarcacoesDia();
                consolidado.setDia(dia);
                consolidado.setTotalInconsistenciasDia(-1);
                consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                consolidado.setMarcacoesColaboradores(new ArrayList<>());
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
                dias.add(consolidado);
            } else {
                if (!consolidado.getDia().equals(dia)) {
                    // Cria novo dia.
                    consolidado = new ConsolidadoMarcacoesDia();
                    consolidado.setDia(dia);
                    consolidado.setTotalInconsistenciasDia(-1);
                    consolidado.setTotalMarcacoesDia(rSet.getInt("TOTAL_MARCACOES_GERAL_DIA"));
                    consolidado.setMarcacoesColaboradores(new ArrayList<>());
                    dias.add(consolidado);
                }
                consolidado.getMarcacoesColaboradores().add(createMarcacoesColaborador(rSet));
            }
            primeiraLinha = false;
        }
        return dias;
    }

    @NotNull
    static MarcacaoColaboradorAjuste createMarcacaoColaboradorAjuste(@NotNull final ResultSet rSet) throws Throwable {
        final MarcacaoColaboradorAjuste marcacoesColab = new MarcacaoColaboradorAjuste();
        marcacoesColab.setCodTipoMarcacao(rSet.getLong("COD_TIPO_MARCACAO"));
        marcacoesColab.setNomeTipoMarcacao(rSet.getString("NOME_TIPO_MARCACAO"));
        marcacoesColab.setJaFoiAjustada(
                rSet.getBoolean("FOI_EDITADO_INICIO")
                || rSet.getBoolean("FOI_EDITADO_FIM"));
        marcacoesColab.setMarcacoes(createMarcacaoAgrupadaAjusteExibicao(rSet));
        return marcacoesColab;
    }

    @NotNull
    private static MarcacaoAgrupadaAjusteExibicao createMarcacaoAgrupadaAjusteExibicao(@NotNull final ResultSet rSet)
            throws Throwable {
        MarcacaoAjusteExibicao inicio = null;
        final LocalDateTime dataHoraInicio = rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class);
        if (dataHoraInicio != null) {
            inicio = new MarcacaoAjusteExibicao();
            inicio.setAtiva(rSet.getBoolean("STATUS_ATIVO_INICIO"));
            inicio.setCodMarcacao(rSet.getLong("COD_MARCACAO_INICIO"));
            inicio.setJaFoiAjustada(rSet.getBoolean("FOI_EDITADO_INICIO"));
            inicio.setDataHoraMarcacao(dataHoraInicio);
        }

        MarcacaoAjusteExibicao fim = null;
        final LocalDateTime dataHoraFim = rSet.getObject("DATA_HORA_FIM", LocalDateTime.class);
        if (dataHoraFim != null) {
            fim = new MarcacaoAjusteExibicao();
            fim.setAtiva(rSet.getBoolean("STATUS_ATIVO_FIM"));
            fim.setCodMarcacao(rSet.getLong("COD_MARCACAO_FIM"));
            fim.setJaFoiAjustada(rSet.getBoolean("FOI_EDITADO_FIM"));
            fim.setDataHoraMarcacao(dataHoraFim);
        }

        return new MarcacaoAgrupadaAjusteExibicao(inicio, fim);
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