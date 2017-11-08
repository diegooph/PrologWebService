package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleIntervaloRelatoriosDao {

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     * @param out OutputStream
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @param cpf cpf (opcional)
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os intervalos realizadas em um período, uma linha para cada intervalo
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @param cpf cpf (opcional)
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     * @param out OutputStream
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica todos os mapas, seus colaboradores e intervalos realizados por cada um
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     * @param out OutputStream
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    /**
     * Relatório que estratifica a aderência por dia, mostrando valores totais, por motorista e por ajudante
     * @param codUnidade código da unidade
     * @param dataInicial data inicial
     * @param dataFinal data final
     * @return um Report
     * @throws SQLException
     * @throws IOException
     */
    @NotNull
    Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;


}
