package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleJornadaRelatoriosDao {

    void getMarcacoesDiariasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    @NotNull
    Report getMarcacoesDiariasReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException;

    void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    @NotNull
    Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    @NotNull
    Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    void getAderenciaMarcacoesColaboradoresCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codUnidade,
                                               @Nullable final Long cpf,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getAderenciaMarcacoesColaboradoresReport(@NotNull final Long codUnidade,
                                                    @Nullable final Long cpf,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws Throwable;


    void getRelatorioPadraoPortaria1510Csv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long codTipoIntervalo,
                                           @NotNull final String cpf,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    @NotNull
    List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final String cpf,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     final boolean apenasColaboradoresAtivos) throws Throwable;

    @NotNull
    List<FolhaPontoJornadaRelatorio> getFolhaPontoJornadaRelatorio(@NotNull final Long codUnidade,
                                                                   @NotNull final String codTipoIntervalo,
                                                                   @NotNull final String cpf,
                                                                   @NotNull final LocalDate dataInicial,
                                                                   @NotNull final LocalDate dataFinal,
                                                                   final boolean apenasColaboradoresAtivos) throws Throwable;

    @NotNull
    Report getMarcacoesComparandoEscalaDiariaReport(@NotNull final Long codUnidade,
                                                    @NotNull final Long codTipoIntervalo,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws SQLException;

    void getMarcacoesComparandoEscalaDiariaCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Long codTipoIntervalo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    void getTotalTempoByTipoIntervaloCsv(@NotNull final OutputStream out,
                                         @NotNull final Long codUnidade,
                                         @NotNull final String codTipoIntervalo,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    void getMarcacoesExportacaoGenericaCsv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           final Long codTipoIntervalo,
                                           final Long codColaborador,
                                           final boolean apenasMarcacoesAtivas,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable;
}