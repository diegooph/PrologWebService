package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created on 1/8/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface RelatorioEntregaDao {

    /**
     * Método utilizado para buscar os dados da aba acumulados, tela Relatórios.
     * Busca os dados pós clique de um card da aba Diário, mostrando o acumulado
     * do dia clicado, tela Relatórios, pilar Entrega.
     *
     * @param dataInicial um Long
     * @param dataFinal   um Long
     * @param codEmpresa  código da empresa a ser usado no filtro
     * @param codRegional código da regional a ser usado no filtro
     * @param codUnidade  código da unidade a ser usado no filtro
     * @param equipe      nome da equipe a ser usado no filtro
     * @return lista de {@link IndicadorAcumulado}
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial,
                                                     Long dataFinal,
                                                     String codEmpresa,
                                                     String codRegional,
                                                     String codUnidade,
                                                     String equipe) throws SQLException;

    List<Indicador> getExtratoIndicador(Long dataInicial,
                                        Long dataFinal,
                                        String codRegional,
                                        String codEmpresa,
                                        String codUnidade,
                                        String equipe,
                                        String cpf,
                                        String indicador) throws SQLException;

    /**
     * Busca os dados para a aba Diário da tela Relatórios, pilar Entrega
     *
     * @param dataInicial um Long
     * @param dataFinal   um Long
     * @param codEmpresa  código da empresa usado no filtro
     * @param codRegional código da regional usado no filtro
     * @param codUnidade  código da unidade usado no filtro
     * @param equipe      nome da equipe usado no filtro
     * @return lista de {@link ConsolidadoDia}
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<ConsolidadoDia> getConsolidadoDia(Long dataInicial,
                                           Long dataFinal,
                                           String codEmpresa,
                                           String codRegional,
                                           String codUnidade,
                                           String equipe,
                                           int limit,
                                           int offset) throws SQLException;

    /**
     * Estratifica os mapas de um dia, contém os dados da equipe, data e mapa, além dos indicadores
     * no formato de item e não de acumulado, chamado quando acontece um clique no FAB
     *
     * @param data        uma data, serão buscados apenas os mapas dessa data
     * @param codEmpresa  código da empresa a ser usado no filtro
     * @param codRegional código da regional a ser usado no filtro
     * @param codUnidade  código da unidade a ser usado no filtro
     * @param equipe      nome da equipe a ser usado no filtro
     * @return lista de {@link MapaEstratificado}
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<MapaEstratificado> getMapasEstratificados(Long data,
                                                   String codEmpresa,
                                                   String codRegional,
                                                   String codUnidade,
                                                   String equipe) throws SQLException;

    List<DadosGrafico> getDadosGrafico(Long dataInicial,
                                       Long dataFinal,
                                       String codEmpresa,
                                       String codRegional,
                                       String codUnidade,
                                       String equipe,
                                       String indicador) throws SQLException;

    void getEstratificacaoMapasCsv(Long codUnidade,
                                   Date dataInicial,
                                   Date dataFinal,
                                   OutputStream out) throws SQLException, IOException;

    Report getEstratificacaoMapasReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException;

    void getExtratoMapasIndicadorCsv(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                     Date dataInicial, Date dataFinal, String equipe, OutputStream out) throws SQLException, IOException;

    Report getExtratoMapasIndicadorReport(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                          Date dataInicial, Date dataFinal, String equipe) throws SQLException;

    void getConsolidadoMapasIndicadorCsv(@NotNull final OutputStream out,
                                         @NotNull final Long codEmpresa,
                                         @NotNull final String codRegional,
                                         @NotNull final String codUnidade,
                                         @NotNull final String equipe,
                                         @NotNull final String cpf,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getConsolidadoMapasIndicadorReport(@NotNull final Long codEmpresa,
                                              @NotNull final String codRegional,
                                              @NotNull final String codUnidade,
                                              @NotNull final String equipe,
                                              @NotNull final String cpf,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable;
}