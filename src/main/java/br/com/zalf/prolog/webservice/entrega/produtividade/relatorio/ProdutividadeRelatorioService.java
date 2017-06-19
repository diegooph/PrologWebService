package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by Zart on 18/05/2017.
 */
public class ProdutividadeRelatorioService {

    ProdutividadeRelatorioDao dao = new ProdutividadeRelatorioDaoImpl();

    public void getConsolidadoProdutividadeCsv(@NotNull OutputStream outputStream,
                                               @NotNull Long codUnidade,
                                               @NotNull long dataInicial,
                                               @NotNull long dataFinal) {
        try {
            dao.getConsolidadoProdutividadeCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getConsolidadoProdutividadeReport(@NotNull Long codUnidade,
                                                             @NotNull long dataInicial,
                                                             @NotNull long dataFinal) {
        try {
            return dao.getConsolidadoProdutividadeReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public Report getExtratoIndividualProdutividadeReport (@NotNull String cpf,
                                                           @NotNull Long codUnidade,
                                                           @NotNull long dataInicial,
                                                           @NotNull long dataFinal) {
        try {
            return dao.getExtratoIndividualProdutividadeReport(cpf, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
