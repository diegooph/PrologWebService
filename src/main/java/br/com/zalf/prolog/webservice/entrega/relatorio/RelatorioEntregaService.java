package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;

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

    public List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial, Long dataFinal, String codEmpresa,
                                                            String codRegional, String codUnidade, String equipe) {
        try {
            return dao.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
        } catch (SQLException e) {
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

    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, String codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador) {
        try {
            return dao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa, codUnidade, equipe, cpf, indicador);
        } catch (SQLException e) {
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

    public List<ConsolidadoDia> getConsolidadoDia(Long dataInicial, Long dataFinal, String codEmpresa,
                                                  String codRegional, String codUnidade, String equipe, int limit, int offset) {
        try {
            return dao.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, limit, offset);
        } catch (SQLException e) {
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

    public List<MapaEstratificado> getMapasEstratificados(Long data, String codEmpresa, String codRegional,
                                                          String codUnidade, String equipe) {
        try {
            return dao.getMapasEstratificados(data, codEmpresa, codRegional, codUnidade, equipe);
        } catch (SQLException e) {
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

    public List<DadosGrafico> getDadosGrafico(Long dataInicial, Long dataFinal, String codEmpresa,
                                              String codRegional, String codUnidade, String equipe, String indicador) {
        try {
            return dao.getDadosGrafico(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, indicador);
        } catch (SQLException e) {
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

    public void getEstratificacaoMapasCsv(Long codUnidade, Long dataInicial, Long dataFinal, OutputStream out) {
        try {
            dao.getEstratificacaoMapasCsv(codUnidade, new Date(dataInicial), new Date(dataFinal), out);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os mapas (CSV). \n" +
                    "Unidade: %d \n" +
                    " Período: %d a %d", codUnidade, dataInicial, dataFinal), e);
        }
    }

    public Report getEstratificacaoMapasReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getEstratificacaoMapasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os mapas (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %d a %d", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getExtratoMapasIndicadorCsv(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                            Long dataInicial, Long dataFinal, String equipe, OutputStream out) {
        try {
            dao.getExtratoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe, out);
        } catch (SQLException | IOException e) {
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

    public Report getExtratoMapasIndicadorReport(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                                 Long dataInicial, Long dataFinal, String equipe) {
        try {
            return dao.getExtratoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe);
        } catch (SQLException e) {
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

    public void getConsolidadoMapasIndicadorCsv(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                            Long dataInicial, Long dataFinal, String equipe, OutputStream out) {
        try {
            dao.getConsolidadoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe, out);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório consolidado dos indicadores. \n" +
                    "codEmpresa: %d \n" +
                    "codRegional: %s \n" +
                    "codUnidade: %s \n" +
                    "equipe: %s \n" +
                    "cpf: %s \n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, equipe, cpf, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }

    public Report getConsolidadoMapasIndicadorReport(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                                 Long dataInicial, Long dataFinal, String equipe) {
        try {
            return dao.getConsolidadoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, new Date(dataInicial), new Date(dataFinal), equipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório consolidado dos indicadores. \n" +
                    "codEmpresa: %d \n" +
                    "codRegional: %s \n" +
                    "codUnidade: %s \n" +
                    "equipe: %s \n" +
                    "cpf: %s \n" +
                    "Período: %d a %d", codEmpresa, codRegional, codUnidade, equipe, cpf, dataInicial, dataFinal), e);
            throw new RuntimeException();
        }
    }
}