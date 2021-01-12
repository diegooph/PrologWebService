package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zart on 18/05/2017.
 */
public class ProdutividadeRelatorioService {
    private static final String TAG = ProdutividadeRelatorioService.class.getSimpleName();
    private final ProdutividadeRelatorioDao dao = Injection.provideProdutividadeRelatorioDao();

    public void getConsolidadoProdutividadeCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final Long codUnidade,
                                               final long dataInicial,
                                               final long dataFinal) {
        try {
            dao.getConsolidadoProdutividadeCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório da produtividade consolidada (CSV)", e);
        }
    }

    public Report getConsolidadoProdutividadeReport(@NotNull final Long codUnidade,
                                                    final long dataInicial,
                                                    final long dataFinal) {
        try {
            return dao.getConsolidadoProdutividadeReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório da produtividade consolidada (REPORT)", e);
            return null;
        }
    }

    public void getExtratoIndividualProdutividadeCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final String cpf,
                                                     @NotNull final Long codUnidade,
                                                     final long dataInicial,
                                                     final long dataFinal) {
        try {
            dao.getExtratoIndividualProdutividadeCsv(outputStream, cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório extrato individual da produtividade (CSV)", e);
        }
    }

    public Report getExtratoIndividualProdutividadeReport(@NotNull final String cpf,
                                                          @NotNull final Long codUnidade,
                                                          final long dataInicial,
                                                          final long dataFinal) {
        try {
            return dao.getExtratoIndividualProdutividadeReport(cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório extrato individual da produtividade (REPORT)", e);
            return null;
        }
    }

    public void getAcessosProdutividadeCsv(@NotNull final OutputStream outputStream,
                                           @NotNull final String cpf,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Long dataInicial,
                                           @NotNull final Long dataFinal) {
        try {
            dao.getAcessosProdutividadeCsv(outputStream, cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório de acessos a produtividade (CSV)", e);
            throw new RuntimeException("Erro ao buscar os dados");
        }

    }

    public Report getAcessosProdutividadeReport(@NotNull final String cpf,
                                                @NotNull final Long codUnidade,
                                                @NotNull final Long dataInicial,
                                                @NotNull final Long dataFinal) {
        try {
            return dao.getAcessosProdutividadeReport(cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de acessos a produtividade (REPORT)", e);
            return null;
        }
    }

    public List<ProdutividadeColaboradorRelatorio> getRelatorioProdutividadeColaborador(
            @NotNull final Long codUnidade,
            @NotNull final String cpfColaborador,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal) {
        try {
            return dao.getRelatorioProdutividadeColaborador(
                    codUnidade,
                    cpfColaborador,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de produtividade dos colaboradores", e);
            throw new RuntimeException(e);
        }
    }
}
