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

    List<ConsolidadoDia> getConsolidadoDia(Long dataInicial,
                                           Long dataFinal,
                                           String codEmpresa,
                                           String codRegional,
                                           String codUnidade,
                                           String equipe,
                                           int limit,
                                           int offset) throws SQLException;

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

    void getExtratoMapasIndicadorCsv(@NotNull final OutputStream out,
                                     @NotNull final Long codEmpresa,
                                     @NotNull final String codUnidade,
                                     @NotNull final String codEquipe,
                                     @NotNull final String cpf,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    @NotNull
    Report getExtratoMapasIndicadorReport(@NotNull final Long codEmpresa,
                                          @NotNull final String codUnidade,
                                          @NotNull final String codEquipe,
                                          @NotNull final String cpf,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws SQLException;

    void getConsolidadoMapasIndicadorCsv(@NotNull final OutputStream out,
                                         @NotNull final Long codEmpresa,
                                         @NotNull final String codUnidade,
                                         @NotNull final String equipe,
                                         @NotNull final String cpf,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getConsolidadoMapasIndicadorReport(@NotNull final Long codEmpresa,
                                              @NotNull final String codUnidade,
                                              @NotNull final String equipe,
                                              @NotNull final String cpf,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable;
}