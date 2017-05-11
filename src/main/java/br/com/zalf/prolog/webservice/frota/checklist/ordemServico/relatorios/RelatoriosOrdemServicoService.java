package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by luiz on 26/04/17.
 */
public class RelatoriosOrdemServicoService {
    private RelatoriosOrdemServicoDao dao = new RelatoriosOrdemServicoDaoImpl();

    void getItensMaiorQuantidadeNokCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull long dataInicial,
                                       @NotNull long dataFinal) {
        try {
            dao.getItensMaiorQuantidadeNokCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull Long codUnidade,
                                            @NotNull long dataInicial,
                                            @NotNull long dataFinal) {
        try {
            return dao.getItensMaiorQuantidadeNokReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void getMediaTempoConsertoItemCsv(@NotNull OutputStream outputStream,
                                      @NotNull Long codUnidade,
                                      @NotNull long dataInicial,
                                      @NotNull long dataFinal) {
        try {
            dao.getMediaTempoConsertoItemCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getMediaTempoConsertoItemReport(@NotNull Long codUnidade,
                                           @NotNull long dataInicial,
                                           @NotNull long dataFinal) {
        try {
            return dao.getMediaTempoConsertoItemReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void getProdutividadeMecanicosCsv(@NotNull OutputStream outputStream,
                                      @NotNull Long codUnidade,
                                      @NotNull long dataInicial,
                                      @NotNull long dataFinal) {
        try {
            dao.getProdutividadeMecanicosCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getProdutividadeMecanicosReport(@NotNull Long codUnidade,
                                           @NotNull long dataInicial,
                                           @NotNull long dataFinal) {
        try {
            return dao.getProdutividadeMecanicosReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}