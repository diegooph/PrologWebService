package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.QtdDiasAfericoesVencidas;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
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
     * Método para gerar um relatório contendo as quantidades e percentuais de pneus que estão aplicados (atualmente) e
     * foram aferidos em um range de data.
     * Para fins de exportação em CSV.
     * <p>
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
     */
    void getFarolAfericaoCsv(@NotNull final OutputStream outputStream,
                             @NotNull final List<Long> codUnidades,
                             @NotNull final LocalDate dataInicial,
                             @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo os pneus que possuem algum desgaste irregular, dentro dos desgastes
     * atualmente analisados pelo ProLog e respeitando as unidades e status filtrados.
     * <p>
     * Apenas pneus que possuírem algum tipo de desgaste irregular serão buscados.
     *
     * @param outputStream Stream onde os dados serão escritos para retorno.
     * @param codUnidades  {@link List<Long>} de códigos das {@link Unidade}s.
     * @param statusPneu   O {@link StatusPneu status do pneu} pelo qual se quer filtrar, ou <code>NULL</code> para
     *                     buscar pneus de todos os status.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    void getPneusComDesgasteIrregularCsv(@NotNull final OutputStream outputStream,
                                         @NotNull final List<Long> codUnidades,
                                         @Nullable final StatusPneu statusPneu) throws Throwable;

    /**
     * Método para gerar um relatório contendo os pneus que possuem algum desgaste irregular, dentro dos desgastes
     * atualmente analisados pelo ProLog e respeitando as unidades e status filtrados.
     * <p>
     * Apenas pneus que possuírem algum tipo de desgaste irregular serão buscados.
     *
     * @param codUnidades {@link List<Long>} de códigos das {@link Unidade}s.
     * @param statusPneu  O {@link StatusPneu status do pneu} pelo qual se quer filtrar, ou <code>NULL</code> para
     *                    buscar pneus de todos os status.
     * @return Um objeto {@link Report report} com os dados filtrados.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Report getPneusComDesgasteIrregularReport(@NotNull final List<Long> codUnidades,
                                              @Nullable final StatusPneu statusPneu) throws Throwable;

    /**
     * Método para gerar um relatório contendo o status atual (onde ele se encontra) de cada pneu das unidades buscadas.
     *
     * @param outputStream Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  {@link List<Long>} de códigos das {@link Unidade}s.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    void getStatusAtualPneusCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para gerar um relatório contendo o status atual (onde ele se encontra) de cada pneu das unidades buscadas.
     *
     * @param codUnidades {@link List<Long>} de códigos das {@link Unidade}s.
     * @return Um objeto {@link Report} com os dados filtrados.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Report getStatusAtualPneusReport(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para gerar um relatório contendo a quantidade de KM percorrido por pneu e por vida.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
     */
    void getKmRodadoPorPneuPorVidaCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para gerar um relatório contendo a quantidade de KM percorrido por pneu e por vida.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Report getKmRodadoPorPneuPorVidaReport(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para gerar um relatório contendo todas as aferições avulsas realizadas durante o período filtrado.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
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
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Report getAfericoesAvulsasReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método utilizado para listar os pneus com base na faixa de Sulco em que se encontram.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param status      - {@link List<String>} de status em que o {@link Pneu} pode se encontrar.
     * @return - {@link List<Faixa>} agrupando os {@link Pneu}s numa dada {@link Faixa}.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    void getDadosUltimaAfericaoCsv(@NotNull final OutputStream outputStream,
                                   @NotNull final List<Long> codUnidades) throws SQLException, IOException;

    /**
     * Método que gera um relatório listando a última aferição de cada pneu presente na listagem.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Report getDadosUltimaAfericaoReport(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que gera um relatório listando todos os dados dos pneus filtrados.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param status       - {@link List<String>} de status em que o {@link Pneu} pode se encontrar.
     * @throws IOException  - Se algum erro ocorrer na busca dos dados na escrita dos dados.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException Se algum erro ocorrer na busca dos dados
     */
    @Deprecated
    List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws Throwable;

    /**
     * busca uma lista de pneus com base em uma faixa de pressão
     *
     * @param codUnidades código da unidade
     * @param status      status do pneu
     * @return lista de faixas
     * @throws SQLException Se algum erro ocorrer na busca dos dados
     */
    @Deprecated
    List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws Throwable;

    /**
     * Busca a quantidade de pneus presentes em cada {@link StatusPneu}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo quantidade de pneus presentes em cada {@link StatusPneu}.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
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
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    List<QuantidadeAfericao> getQtdAfericoesByTipoByData(@NotNull final List<Long> codUnidades,
                                                         @NotNull final Date dataInicial,
                                                         @NotNull final Date dataFinal) throws Throwable;

    /**
     * Busca a quantidade de serviços abertos para cada {@link TipoServico}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de serviços abertos para cada {@link TipoServico}.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Map<TipoServico, Integer> getServicosEmAbertoByTipo(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca um consolidado sobre as placas que estão com a Aferição em dia e aquelas
     * cujo estão com a Aferição fora do prazo.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um objeto{@link StatusPlacasAfericao} contendo a quantidade de placas com a aferição em dia
     * e a quantidade de placas com a aferição fora do prazo.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    StatusPlacasAfericao getStatusPlacasAfericao(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Busca a média de tempo, em HORAS, de conserto para cada {@link TipoServico}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a média de tempo de conserto para cada {@link TipoServico}.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(@NotNull final List<Long> codUnidades) throws
            SQLException;

    /**
     * Busca a quantidade de quilometros rodados com serviços em abertos para cada placa.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de quilometros rodados com serviços em
     * abertos para cada placa.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Map<String, Integer> getQtdKmRodadoComServicoEmAberto(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca a quantidade de pneus com algum problema presente em cada placa.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo a quantidade de pneus com problemas, presentes em cada placa.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final List<Long> codUnidades) throws
            SQLException;

    /**
     * Método que busca a quantidade de pneus com pressão incorreta.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - A quantidade de pneus com pressão incorreta.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    int getQtdPneusPressaoIncorreta(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca, para cada {@link Pneu pneu}, o menor sulco e a pressão que ele possui.
     *
     * @param codUnidades Uma {@link List<Long> lista} de códigos das {@link Unidade unidades} pelas quais queremos
     *                    filtrar.
     * @return Uma {@link List lista} de {@link SulcoPressao} contendo o menor sulco e a pressão de cada pneu.
     * @throws Throwable Se qualquer erro acontecer.
     */
    @NotNull
    List<SulcoPressao> getMenorSulcoEPressaoPneus(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método que busca a quantidade de pneus descartados devido ao mesmo {@link MotivoDescarte}.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @return - Um {@link Map} contendo os {@link MotivoDescarte} e a quantidade de pneus descartados
     * para cada motivo.
     * @throws SQLException - Se algum erro ocorrer na busca dos dados.
     */
    Map<String, Integer> getQtdPneusDescartadosPorMotivo(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método que busca a quantidade de dias que uma aferição venceu.
     *
     * @param codUnidades {@link List<Long> Lista} de códigos das {@link Unidade unidades}.
     * @return Uma {@link List lista} de {@link QtdDiasAfericoesVencidas}.
     * @throws Throwable Se qualquer erro acontecer.
     */
    @NotNull
    List<QtdDiasAfericoesVencidas> getQtdDiasAfericoesVencidas(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método que busca a quantidade de aferições realizadas por tipo em um determinado período.
     *
     * @param codUnidades               {@link List<Long> Lista} de códigos das {@link Unidade unidades}.
     * @param diasRetroativosParaBuscar Dias para buscar.
     * @return Uma {@link List lista} de {@link QuantidadeAfericao}.
     * @throws Throwable Se qualquer erro acontecer.
     */
    @NotNull
    List<QuantidadeAfericao> getQtdAfericoesRealizadasPorDiaByTipo(
            @NotNull final List<Long> codUnidades,
            final int diasRetroativosParaBuscar) throws Throwable;

    /**
     * Método buscar o relatório que calcula a validade de um pneu através do DOT. {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    @NotNull
    Report getVencimentoDotReport(@NotNull final List<Long> codUnidades,
                                  @NotNull final String userToken) throws Throwable;

    /**
     * Método buscar o relatório que calcula a validade de um pneu através do DOT.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer na busca dos dados.
     */
    void getVencimentoDotCsv(@NotNull final OutputStream out,
                             @NotNull final List<Long> codUnidades,
                             @NotNull final String userToken) throws Throwable;

    /**
     * Método para gerar um relatório contendo o custo por km por marcas e modelos de pneus e bandas.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @throws Throwable - Se algum erro ocorrer na busca dos dados.
     */
    void getCustoPorKmCsv(@NotNull final OutputStream outputStream,
                          @NotNull final List<Long> codUnidades) throws Throwable;
}