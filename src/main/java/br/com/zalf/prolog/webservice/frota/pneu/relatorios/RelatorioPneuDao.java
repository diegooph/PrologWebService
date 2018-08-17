package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.MotivoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by didi on 9/15/16.
 */
public interface RelatorioPneuDao {

    /**
     * Método para gerar um relatório contendo todas as aferições avulsas realizadas durante o período filtrado.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws Throwable - Se algum erro na busca dos dados ocorrer.
     */
    void getAfericoesAvulsasCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo todas as aferições avulsas realizadas durante o período filtrado.
     * Para fins de exibição na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws Throwable - Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getAfericoesAvulsasReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo as aferições avulsas realizadas pelo colaborador
     * especificado pelo {@code cpfColaborador}.
     *
     * @param cpfColaborador - {@link Colaborador#cpf} para filtrar os dados da busca.
     * @param codUnidade     - Código da {@link Unidade} que os dados serão buscados.
     * @param dataInicial    - Data inicial do período de filtro.
     * @param dataFinal      - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws Throwable - Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getAfericoesAvulsasReportByColaborador(@NotNull final Long cpfColaborador,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable;

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
     * Método para gerar o relatório de previsão de troca de um pneu, com dados baseados no histórico de aferições.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     */
    void getPrevisaoTrocaEstratificadoCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidades,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    /**
     * Método para gerar o relatório de previsão de troca de um pneu, com dados baseados no histórico de aferições.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getPrevisaoTrocaEstratificadoReport(@NotNull final List<Long> codUnidades,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que gera um relatório consolidado contendo a previsão de trocas de pneus, baseado no relatório
     * estratificado. Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    void getPrevisaoTrocaConsolidadoCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final List<Long> codUnidades,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    /**
     * Método que gera um relatório consolidado contendo a previsão de trocas de pneus, baseado no relatório
     * estratificado. Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getPrevisaoTrocaConsolidadoReport(@NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que gera um relatório identificando a aderência das aferições para as placas das unidades
     * filtradas. Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    void getAderenciaPlacasCsv(@NotNull final OutputStream outputStream,
                               @NotNull final List<Long> codUnidades,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    /**
     * Método que gera um relatório identificando a aderência das aferições para as placas das unidades
     * filtradas. Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getAderenciaPlacasReport(@NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que gera um relatório identificando os pneus que foram descartados nas unidades filtradas.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getPneusDescartadosReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que gera um relatório identificando os pneus que foram descartados nas unidades filtradas.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    void getPneusDescartadosCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    /**
     * Método que gera um relatório listando a última aferição de cada pneu presente na listagem.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    void getDadosUltimaAfericaoCsv(@NotNull final OutputStream outputStream,
                                   @NotNull final List<Long> codUnidades) throws SQLException, IOException;

    /**
     * Método que gera um relatório listando a última aferição de cada pneu presente na listagem.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getDadosUltimaAfericaoReport(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que gera um relatório listando todos os dados dos pneus filtrados.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param status       - {@link List<String>} de status em que o {@link Pneu} pode se encontrar.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    void getResumoGeralPneusCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @Nullable final String status) throws SQLException, IOException;

    /**
     * Método que gera um relatório listando todos os dados dos pneus filtrados.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param status      - {@link List<String>} de status em que o {@link Pneu} pode se encontrar.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Report getResumoGeralPneusReport(@NotNull final List<Long> codUnidades,
                                     @Nullable final String status) throws SQLException;

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
    List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws Throwable;

    /**
     * busca uma lista de pneus com base em uma faixa de pressão
     *
     * @param codUnidades código da unidade
     * @param status      status do pneu
     * @return lista de faixas
     * @throws SQLException se ocorrer erro no banco de dados
     */
    @Deprecated
    List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws Throwable;

    /**
     * Busca a quantidade de pneus presentes em cada {@link StatusPneu}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo quantidade de pneus presentes em cada {@link StatusPneu}.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<StatusPneu, Integer> getQtdPneusByStatus(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método para buscar a quantidade de aferições realizadas para cada {@link TipoMedicaoColetadaAfericao},
     * dado um período de filtragem e as unidades selecionadas.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um {@link List} contendo a {@link QuantidadeAfericao} para cada dia do filtro aplicado.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    List<QuantidadeAfericao> getQtdAfericoesByTipoByData(@NotNull final List<Long> codUnidades,
                                                         @NotNull final Date dataInicial,
                                                         @NotNull final Date dataFinal) throws SQLException;

    /**
     * Busca a quantidade de serviços abertos para cada {@link TipoServico}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de serviços abertos para cada {@link TipoServico}.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<TipoServico, Integer> getServicosEmAbertoByTipo(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca um consolidado sobre as placas que estão com a Aferição em dia e aquelas
     * cujo estão com a Aferição fora do prazo.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um objeto{@link StatusPlacasAfericao} contendo a quantidade de placas com a aferição em dia
     * e a quantidade de placas com a aferição fora do prazo.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    StatusPlacasAfericao getStatusPlacasAfericao(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Busca a média de tempo, em HORAS, de conserto para cada {@link TipoServico}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a média de tempo de conserto para cada {@link TipoServico}.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(@NotNull final List<Long> codUnidades) throws
            SQLException;

    /**
     * Busca a quantidade de quilometros rodados com serviços em abertos para cada placa.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de quilometros rodados com serviços em
     * abertos para cada placa.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<String, Integer> getQtdKmRodadoComServicoEmAberto(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca a quantidade de pneus com algum problema presente em cada placa.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de pneus com problemas, presentes em cada placa.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final List<Long> codUnidades) throws
            SQLException;

    /**
     * Método que busca a quantidade de pneus com pressão incorreta.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - A quantidade de pneus com pressão incorreta.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    int getQtdPneusPressaoIncorreta(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca, para cada {@link Pneu}, o menor sulco e a menor pressão nele presente.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link List} de {@link SulcoPressao} contendo o menor sulco e a menor pressão
     * de cada pneu.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    List<SulcoPressao> getMenorSulcoEPressaoPneus(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca a quantidade de pneus descartados devido ao mesmo {@link MotivoDescarte}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo os {@link MotivoDescarte} e a quantidade de pneus descartados
     * para cada motivo.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    Map<String, Integer> getQtdPneusDescartadosPorMotivo(@NotNull final List<Long> codUnidades) throws SQLException;
}