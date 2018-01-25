package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QtAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.ResumoServicos;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by didi on 9/15/16.
 */
public interface RelatorioPneuDao {

    /**
     * seleciona pneus com base no sulco
     *
     * @param codUnidades código da unidade
     * @param status      status do pneu
     * @return lista de faixas
     * @throws SQLException se ocorrer erro no banco de dados
     */
    List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status) throws SQLException;

    /**
     * busca uma lista de aderencias com base em um filtro
     *
     * @param ano        ano à ser buscadp
     * @param mes        mes a ser buscado
     * @param codUnidade código da unidade
     * @return lista de aderencias
     * @throws SQLException se ocorrer erro no banco de dados
     */
    List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException;

    /**
     * busca uma lista de pneus com base em uma faixa de pressão
     *
     * @param codUnidades código da unidade
     * @param status      status do pneu
     * @return lista de faixas
     * @throws SQLException se ocorrer erro no banco de dados
     */
    List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws SQLException;

    /**
     * busca um resumo de serviços com base em um filtro
     *
     * @param ano         ano a ser buscado
     * @param mes         mes a ser buscado
     * @param codUnidades código da unidade
     * @return lista de serviços
     * @throws SQLException se ocorrer erro no banco de dados
     */
    List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) throws SQLException;

    /**
     * /**
     * Relatório que gera a previsão de troca de um pneu, dados baseados no histórico de aferições
     *
     * @param codUnidade   código da unidade a ser buscada
     * @param dataInicial  data inicial da troca
     * @param dataFinal    data final da troca
     * @param outputStream um OutputStream
     * @throws IOException
     * @throws SQLException
     */
    void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

    /**
     * Relatório que gera a previsão de troca de um pneu, dados baseados no histórico de aferições
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return
     * @throws SQLException
     */
    Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

    void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

    Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

    void getAderenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException;

    Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException;

    void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream) throws SQLException, IOException;

    Report getDadosUltimaAfericaoReport(Long codUnidade) throws SQLException;

    Report getEstratificacaoServicosFechadosReport(Long codUnidade, Date dataInicial,
                                                   Date dataFinal) throws SQLException;

    void getEstratificacaoServicosFechadosCsv(Long codUnidade, OutputStream outputStream, Date dataInicial,
                                              Date dataFinal) throws IOException, SQLException;

    Map<StatusPneu, Integer> getQtPneusByStatus(List<Long> codUnidades) throws SQLException;

    List<QtAfericao> getQtAfericoesByTipoByData(Date dataInicial, Date dataFinal, List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getServicosEmAbertoByTipo(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getQtdPlacasAfericaoVencida(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getMdTempoConsertoServicoPorTipo(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getQtKmRodadoServicoAberto(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(List<Long> codUnidades) throws SQLException;

    int getQtdPneusPressaoIncorreta(List<Long> codUnidades) throws SQLException;

    Map<String, Double> getMenorSulcoPneu(List<Long> codUnidades) throws SQLException;
}