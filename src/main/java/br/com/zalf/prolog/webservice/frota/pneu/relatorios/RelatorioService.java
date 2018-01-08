package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.ResumoServicos;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe RelatorioService responsavel por comunicar-se com a interface DAO
 */
public class RelatorioService {

    private RelatorioDao dao = new RelatorioDaoImpl();
    private static final String TAG = RelatorioService.class.getSimpleName();

    public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaSulco(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de sulco", e);
            return new ArrayList<>();
        }
    }

    public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaPressao(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de pressão", e);
            return new ArrayList<>();
        }
    }

    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) {
        try {
            return dao.getAderenciaByUnidade(ano, mes, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência por unidade. \n" +
                    "Unidade: %d \n" +
                    "Ano: %d \n" +
                    "Mês: %d", codUnidade, ano, mes), e);
            return new ArrayList<>();
        }
    }

    public List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) {
        try {
            return dao.getResumoServicosByUnidades(ano, mes, codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com o resumo dos serviços por unidade. \n" +
                    "Unidades: %s \n" +
                    "Ano: %d \n" +
                    "Mês: %d", codUnidades.toString(), ano, mes), e);
            return new ArrayList<>();
        }
    }

    public void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws RuntimeException {
        try {
            dao.getPrevisaoTrocaCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (CSV). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException();
        }
    }

    public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getPrevisaoTrocaReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws RuntimeException {
        try {
            dao.getPrevisaoTrocaConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca consolidado (CSV). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException();
        }
    }

    public Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getPrevisaoTrocaConsolidadoReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca consolidado (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAerenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws RuntimeException {
        try {
            dao.getAderenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (CSV). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException();
        }
    }

    public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public List<Pneu> getPneusByFaixaSulco(double inicioFaixa, double fimFaixa, Long codEmpresa,
                                           String codUnidade, long limit, long offset) {
        try {
            return dao.getPneusByFaixaSulco(inicioFaixa, fimFaixa, codEmpresa, codUnidade, limit, offset);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de pneus de uma faixa de sulco. \n" +
                    "Empresa: %d \n" +
                    "Unidade: %s \n" +
                    "Limit: %d \n" +
                    "Offset: %d \n" +
                    "Inicio faixa: %e \n" +
                    "Fim faixa: %e", codEmpresa, codUnidade, limit, offset, inicioFaixa, fimFaixa), e);
            return new ArrayList<>();
        }
    }

    public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream) throws RuntimeException {
        try {
            dao.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (CSV). \n" +
                    "Unidade: %d", codUnidade), e);
            throw new RuntimeException();
        }
    }

    public Report getDadosUltimaAfericaoReport(Long codUnidade) {
        try {
            return dao.getDadosUltimaAfericaoReport(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (REPORT). \n" +
                    "Unidade: %d", codUnidade), e);
            return null;
        }
    }

    public Report getEstratificacaoServicosFechadosReport(Long codUnidade, long dataInicial,
                                                          long dataFinal) {
        try {
            return dao.getEstratificacaoServicosFechadosReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getEstratificacaoServicosFechadosCsv(Long codUnidade, OutputStream outputStream, long dataInicial,
                                                     long dataFinal) throws RuntimeException {
        try {
            dao.getEstratificacaoServicosFechadosCsv(codUnidade, outputStream, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório que estratifica os serviços fechados (CSV). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException();
        }
    }

    public Map<String,Long> getQtPneusByStatus(List<Long> codUnidades) {
        try {
            return dao.getQtPneusByStatus(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os pneus agrupados por status. \n" +
                    "Unidades: %s", codUnidades.toString()), e);
            throw new RuntimeException();
        }
    }
}
