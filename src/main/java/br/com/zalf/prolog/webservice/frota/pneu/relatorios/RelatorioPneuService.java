package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe responsável por comunicar-se com a interface DAO.
 */
public class RelatorioPneuService {
    private static final String TAG = RelatorioPneuService.class.getSimpleName();
    private final RelatorioPneuDao dao = Injection.provideRelatorioPneuDao();

    public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaSulco(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de sulco", e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaPressao(codUnidades, status);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de faixas de pressão", e);
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) {
        try {
            return dao.getAderenciaByUnidade(ano, mes, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência por unidade. \n" +
                    "Unidade: %d \n" +
                    "Ano: %d \n" +
                    "Mês: %d", codUnidade, ano, mes), e);
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getPrevisaoTrocaReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de previsão de troca (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public void getAderenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
            throws RuntimeException {
        try {
            dao.getAderenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (CSV). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de aderência das placas (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Período: %s a %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException(e);
        }
    }

    public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream) throws RuntimeException {
        try {
            dao.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (CSV). \n" +
                    "Unidade: %d", codUnidade), e);
            throw new RuntimeException(e);
        }
    }

    public Report getDadosUltimaAfericaoReport(Long codUnidade) {
        try {
            return dao.getDadosUltimaAfericaoReport(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os dados da última aferição (REPORT). \n" +
                    "Unidade: %d", codUnidade), e);
            throw new RuntimeException(e);
        }
    }

    public void getResumoGeralPneusCsv(Long codUnidade, OutputStream outputStream) throws RuntimeException {
        try {
            dao.getResumoGeralPneus(codUnidade, outputStream);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de resumo geral dos pneus (CSV). \n" +
                    "Unidade: %d", codUnidade), e);
            throw new RuntimeException(e);
        }
    }

    public Report getResumoGeralPneusReport(Long codUnidade) {
        try {
            return dao.getResumoGeralPneus(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de resumo geral dos pneus (REPORT). \n" +
                    "Unidade: %d", codUnidade), e);
            throw new RuntimeException(e);
        }
    }

    public Report getPneusDescartadosReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getPneusDescartadosReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de pneus descartados (REPORT). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException(e);
        }
    }

    public void getPneusDescartadosCsv(OutputStream outputStream, Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            dao.getPneusDescartadosCsv(outputStream, codUnidade, dataInicial, dataFinal);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório de pneus descartados (CSV). \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %s \n" +
                    "Data Final: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Map<StatusPneu, Integer> getQtPneusByStatus(List<Long> codUnidades) {
        try {
            return dao.getQtPneusByStatus(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os pneus agrupados por status. \n" +
                    "Unidades: %s", codUnidades.toString()), e);
            throw new RuntimeException();
        }
    }

    public List<QuantidadeAfericao> getQtAfericoesByTipoByData(Date dataInicial, Date dataFinal, List<Long> codUnidades) {
        try {
            return dao.getQtAfericoesByTipoByData(DateUtils.toSqlDate(dataInicial), DateUtils.toSqlDate(dataFinal), codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de aferições realizadas por data, agrupadas por tipo. \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s \n" +
                    "unidades: %s", dataInicial.toString(), dataFinal, codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Map<TipoServico, Integer> getServicosEmAbertoByTipo(List<Long> codUnidades) {
        try {
            return dao.getServicosEmAbertoByTipo(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os serivços em aberto agrupados por tipo. \n" +
                    "unidades:" + codUnidades.toString(), e);
            throw new RuntimeException(e);
        }
    }

    public StatusPlacasAfericao getStatusPlacasAfericao(List<Long> codUnidades) {
        try {
            return dao.getStatusPlacasAfericao(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de placas com aferição vencida. \n" +
                    "unidade: %s", codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(List<Long> codUnidades) {
        try {
            return dao.getMediaTempoConsertoServicoPorTipo(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a média de tempo de conserto dos serviços (pneus). \n" +
                    "unidades: %s.", codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getQtKmRodadoServicoAberto(List<Long> codUnidades) {
        try {
            return dao.getQtdKmRodadoComServicoEmAberto(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o total de km percorrido com serviço em aberto por placa. \n" +
                    "unidades: %s", codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(List<Long> codUnidades) {
        try {
            return dao.getPlacasComPneuAbaixoLimiteMilimetragem(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a lista com as placas e qtd de pneus abaixo do limite. \n" +
                    "unidades: %s", codUnidades.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public int getQtdPneusPressaoIncorreta(List<Long> codUnidades) {
        try {
            return dao.getQtdPneusPressaoIncorreta(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a quantidade de pneus com pressão incorreta. \n" +
                    "unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }

    public List<SulcoPressao> getMenorSulcoPneus(List<Long> codUnidades) {
        try {
            return dao.getMenorSulcoEPressaoPneus(codUnidades);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar a lista com o menor sulco de cada pneu. \n" +
                    "unidades: %s", codUnidades), e);
            throw new RuntimeException(e);
        }
    }
}
