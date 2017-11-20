package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by Zart on 18/05/2017.
 */
public class ProdutividadeRelatorioService {

    private ProdutividadeRelatorioDao dao = new ProdutividadeRelatorioDaoImpl();
    private static final String TAG = ProdutividadeRelatorioService.class.getSimpleName();

    public void getConsolidadoProdutividadeCsv(@NotNull OutputStream outputStream,
                                               @NotNull Long codUnidade,
                                               @NotNull long dataInicial,
                                               @NotNull long dataFinal) {
        try {
            dao.getConsolidadoProdutividadeCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório da produtividade consolidada (CSV)", e);
        }
    }

    public Report getConsolidadoProdutividadeReport(@NotNull Long codUnidade,
                                                             @NotNull long dataInicial,
                                                             @NotNull long dataFinal) {
        try {
            return dao.getConsolidadoProdutividadeReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório da produtividade consolidada (REPORT)", e);
            return null;
        }
    }

    public void getExtratoIndividualProdutividadeCsv(@NotNull OutputStream outputStream,
                                                     @NotNull String cpf,
                                                     @NotNull Long codUnidade,
                                                     @NotNull long dataInicial,
                                                     @NotNull long dataFinal) {
        try {
            dao.getExtratoIndividualProdutividadeCsv(outputStream, cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório extrato individual da produtividade (CSV)", e);
        }
    }

    public Report getExtratoIndividualProdutividadeReport (@NotNull String cpf,
                                                           @NotNull Long codUnidade,
                                                           @NotNull long dataInicial,
                                                           @NotNull long dataFinal) {
        try {
            return dao.getExtratoIndividualProdutividadeReport(cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório extrato individual da produtividade (REPORT)", e);
            return null;
        }
    }

    public void getAcessosProdutividadeCsv( OutputStream outputStream,
                                     String cpf,
                                     Long codUnidade,
                                     Long dataInicial,
                                     Long dataFinal) {
        try {
            dao.getAcessosProdutividadeCsv(outputStream, cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, "Erro ao buscar o relatório de acessos a produtividade (CSV)", e);
            throw new RuntimeException("Erro ao buscar os dados");
        }

    }

    public Report getAcessosProdutividadeReport( String cpf,
                                          Long codUnidade,
                                          Long dataInicial,
                                          Long dataFinal) {
        try {
            return dao.getAcessosProdutividadeReport(cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o relatório de acessos a produtividade (REPORT)", e);
            return null;
        }
    }
}
