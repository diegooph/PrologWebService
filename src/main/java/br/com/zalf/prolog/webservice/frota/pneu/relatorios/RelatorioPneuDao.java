package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import org.jetbrains.annotations.NotNull;

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
     * Método utilizado para listar os pneus com base na faixa de Sulco em que se encontram.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param status      - {@link List<String>} de status em que o {@link Pneu} pode se encontrar.
     * @return - {@link List<Faixa>} agrupando os {@link Pneu}s numa dada {@link Faixa}.
     * @throws SQLException - Se ocorrer erro no banco de dados.
     */
    List<Faixa> getQtdPneusByFaixaSulco(@NotNull final List<Long> codUnidades,
                                        @NotNull final List<String> status) throws SQLException;

    /**
     * busca uma lista de aderencias com base em um filtro
     *
     * @param ano        ano à ser buscadp
     * @param mes        mes a ser buscado
     * @param codUnidade código da unidade
     * @return lista de aderencias
     * @throws SQLException se ocorrer erro no banco de dados
     */
    @Deprecated
    List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException;

    /**
     * busca uma lista de pneus com base em uma faixa de pressão
     *
     * @param codUnidades código da unidade
     * @param status      status do pneu
     * @return lista de faixas
     * @throws SQLException se ocorrer erro no banco de dados
     */
    @Deprecated
    List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws SQLException;

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

    void getResumoGeralPneus(Long codUnidade, String status, OutputStream outputStream) throws SQLException, IOException;

    Report getResumoGeralPneus(Long codUnidade, String status) throws SQLException;

    Report getPneusDescartadosReport(Long codUnidade,
                                     Long dataInicial,
                                     Long dataFinal) throws SQLException;

    void getPneusDescartadosCsv(OutputStream outputStream, Long codUnidade, Long dataInicial,
                                Long dataFinal) throws IOException, SQLException;

    Map<StatusPneu, Integer> getQtPneusByStatus(List<Long> codUnidades) throws SQLException;

    List<QuantidadeAfericao> getQtAfericoesByTipoByData(Date dataInicial, Date dataFinal, List<Long> codUnidades) throws SQLException;

    Map<TipoServico, Integer> getServicosEmAbertoByTipo(List<Long> codUnidades) throws SQLException;

    StatusPlacasAfericao getStatusPlacasAfericao(List<Long> codUnidades) throws SQLException;

    Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getQtdKmRodadoComServicoEmAberto(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(List<Long> codUnidades) throws SQLException;

    int getQtdPneusPressaoIncorreta(List<Long> codUnidades) throws SQLException;

    List<SulcoPressao> getMenorSulcoEPressaoPneus(List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getQuantidadePneusDescartadosPorMotivo(List<Long> codUnidades) throws SQLException;
}