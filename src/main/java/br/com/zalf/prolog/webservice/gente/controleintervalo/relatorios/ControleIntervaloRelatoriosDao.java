package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleIntervaloRelatoriosDao {

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @param cpf         cpf (opcional)
     * @throws SQLException
     * @throws IOException
     */
    void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @param cpf         cpf (opcional)
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por colaborador
     *
     * @param out         OutputStream
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @throws SQLException
     * @throws IOException
     */
    void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal,
                                              String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por colaborador
     *
     * @param codUnidade  código da unidade
     * @param dataInicial data inicial
     * @param dataFinal   data final
     * @return um Report
     * @throws SQLException
     */
    @NotNull
    Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException;


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
                                                     @NotNull final LocalDate dataFinal) throws SQLException;

    @NotNull
    Report getMarcacoesComparandoEscalaDiariaReport(@NotNull final Long codUnidade,
                                                    @NotNull final Long codTipoIntervalo,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal)
            throws SQLException;

    void getMarcacoesComparandoEscalaDiariaCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Long codTipoIntervalo,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal)
            throws SQLException, IOException;
}