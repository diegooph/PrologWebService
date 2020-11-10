package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
public class RelatorioEntregaService {
    private static final String TAG = RelatorioEntregaService.class.getSimpleName();
    private final RelatorioEntregaDao dao = Injection.provideRelatorioEntregaDao();

    public List<IndicadorAcumulado> getAcumuladoIndicadores(final Long dataInicial, final Long dataFinal, final String codEmpresa,
                                                            final String codRegional, final String codUnidade, final String equipe) {
        try {
            return dao.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o acumulado dos indicadores. \n" +
                            "Empresa: %s \n" +
                            "Regional: %s \n" +
                            "Unidade: %s\n" +
                            "Equipe: %s \n" +
                            "Período: %d a %d",
                    codEmpresa, codRegional, codUnidade, equipe, dataInicial, dataFinal), e);
            return null;
        }
    }

    public List<Indicador> getExtratoIndicador(final Long dataInicial, final Long dataFinal, final String codRegional, final String codEmpresa,
                                               final String codUnidade, final String equipe, final String cpf, final String indicador) {
        try {
            return dao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa, codUnidade, equipe, cpf, indicador);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o extrato do indicador.\n" +
                            "Empresa: %s \n" +
                            "Regional: %s \n" +
                            "Unidade: %s \n" +
                            "Equipe: %s \n" +
                            "cpf: %s \n" +
                            "Indicador: %s \n" +
                            "Período: %d a %d",
                    codEmpresa, codRegional, codUnidade, equipe, cpf, indicador, dataInicial, dataFinal), e);
            return null;
        }
    }

    public List<ConsolidadoDia> getConsolidadoDia(final Long dataInicial, final Long dataFinal, final String codEmpresa,
                                                  final String codRegional, final String codUnidade, final String equipe, final int limit, final int offset) {
        try {
            return dao.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, limit, offset);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o consolidadeo do dia. \n" +
                            "Empresa: %s \n" +
                            "Regional: %s \n" +
                            "Unidade: %s \n" +
                            "Equipe: %s \n" +
                            "Período: %d a %d",
                    codEmpresa, codRegional, codUnidade, equipe, dataInicial, dataFinal), e);
            return null;
        }
    }

    public List<MapaEstratificado> getMapasEstratificados(final Long data, final String codEmpresa, final String codRegional,
                                                          final String codUnidade, final String equipe) {
        try {
            return dao.getMapasEstratificados(data, codEmpresa, codRegional, codUnidade, equipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os mapas estratificados. \n" +
                            "Empresa: %s \n" +
                            "Regional: %s \n" +
                            "Unidade: %s \n" +
                            "Equipe %s \n" +
                            "Data: %d",
                    codEmpresa, codRegional, codUnidade, equipe, data), e);
            return null;
        }
    }

    public List<DadosGrafico> getDadosGrafico(final Long dataInicial, final Long dataFinal, final String codEmpresa,
                                              final String codRegional, final String codUnidade, final String equipe, final String indicador) {
        try {
            return dao.getDadosGrafico(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, indicador);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os dados para montagem do gráfico. \n" +
                            "Empresa: %s \n" +
                            "Regional: %s \n" +
                            "Unidade: %s \n" +
                            "Equipe: %s \n" +
                            "Indicador: %s \n" +
                            "Período: %d a %d",
                    codEmpresa, codRegional, codUnidade, equipe, indicador, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getEstratificacaoMapasCsv(final Long codUnidade, final Long dataInicial, final Long dataFinal, final OutputStream out) {
        try {
            dao.getEstratificacaoMapasCsv(codUnidade, new Date(dataInicial), new Date(dataFinal), out);
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os mapas (CSV). \n" +
                    "Unidade: %d \n" +
                    " Período: %d a %d", codUnidade, dataInicial, dataFinal), e);
        }
    }

    public Report getEstratificacaoMapasReport(final Long codUnidade, final Long dataInicial, final Long dataFinal) {
        try {
            return dao.getEstratificacaoMapasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os mapas (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %d a %d", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getExtratoMapasIndicadorCsv(final Long codEmpresa, final String codRegional, final String codUnidade, final String cpf,
                                            final Long dataInicial, final Long dataFinal, final String equipe, final OutputStream out) {
        try {
            dao.getExtratoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe, out);
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório que estratifica os indicadores de cada mapa. \n" +
                    "codEmpresa: %d \n" +
                    "codRegional: %s \n" +
                    "codUnidade: %s \n" +
                    "equipe: %s \n" +
                    "cpf: %s \n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, equipe, cpf, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public Report getExtratoMapasIndicadorReport(final Long codEmpresa, final String codRegional, final String codUnidade, final String cpf,
                                                 final Long dataInicial, final Long dataFinal, final String equipe) {
        try {
            return dao.getExtratoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório que estratifica os indicadores de cada mapa. \n" +
                    "codEmpresa: %d \n" +
                    "codRegional: %s \n" +
                    "codUnidade: %s \n" +
                    "equipe: %s \n" +
                    "cpf: %s \n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, equipe, cpf, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public void getConsolidadoMapasIndicadorCsv(final OutputStream out,
                                                final Long codEmpresa,
                                                final String codRegional,
                                                final String codUnidade,
                                                final String codEquipe,
                                                final String cpf,
                                                final Long dataInicial,
                                                final Long dataFinal) {
        try {
            dao.getConsolidadoMapasIndicadorCsv(
                    out,
                    codEmpresa,
                    codRegional,
                    codUnidade,
                    codEquipe,
                    cpf,
                    new java.sql.Date(dataInicial).toLocalDate(),
                    new java.sql.Date(dataFinal).toLocalDate());
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao gerar o relatório consolidado dos indicadores.\n" +
                    "codEmpresa: %d\n" +
                    "codRegional: %s\n" +
                    "codUnidade: %s\n" +
                    "codEquipe: %s\n" +
                    "cpf: %s\n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, codEquipe, cpf, dataInicial, dataFinal), t);
            throw new RuntimeException(t);
        }
    }

    @NotNull
    public Report getConsolidadoMapasIndicadorReport(final Long codEmpresa,
                                                     final String codRegional,
                                                     final String codUnidade,
                                                     final String codEquipe,
                                                     final String cpf,
                                                     final Long dataInicial,
                                                     final Long dataFinal) {
        try {
            return dao.getConsolidadoMapasIndicadorReport(
                    codEmpresa,
                    codRegional,
                    codUnidade,
                    codEquipe,
                    cpf,
                    new java.sql.Date(dataInicial).toLocalDate(),
                    new java.sql.Date(dataInicial).toLocalDate());
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao gerar o relatório consolidado dos indicadores.\n" +
                    "codEmpresa: %d\n" +
                    "codRegional: %s\n" +
                    "codUnidade: %s\n" +
                    "codEquipe: %s\n" +
                    "cpf: %s\n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, codEquipe, cpf, dataInicial, dataFinal), t);
            throw new RuntimeException(t);
        }
    }
}