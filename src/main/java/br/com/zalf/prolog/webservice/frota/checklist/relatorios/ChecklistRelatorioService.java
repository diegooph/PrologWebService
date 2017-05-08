package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.commons.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by luiz on 25/04/17.
 */


class ChecklistRelatorioService {
    private ChecklistRelatorioDao dao = new ChecklistRelatorioDaoImpl();

    void getCheckilistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                        @NotNull Long codUnidade,
                                        @NotNull long dataInicial,
                                        @NotNull long dataFinal) {
        try {
            dao.getChecklistsRealizadosDiaCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                            @NotNull long dataInicial,
                                            @NotNull long dataFinal) {
        try {
            return dao.getChecklistsRealizadosDiaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void getExtratoChecklistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long codUnidade,
                                              @NotNull long dataInicial,
                                              @NotNull long dataFinal) {
        try {
            dao.getExtratoChecklistsRealizadosDiaCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getExtratoChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                                   @NotNull long dataInicial,
                                                   @NotNull long dataFinal) {
        try {
            return dao.getExtratoChecklistsRealizadosDiaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void getTempoRealizacaoChecklistMotoristaCsv(@NotNull OutputStream outputStream,
                                                 @NotNull Long codUnidade,
                                                 @NotNull long dataInicial,
                                                 @NotNull long dataFinal) {
        try {
            dao.getTempoRealizacaoChecklistMotoristaCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getTempoRealizacaoChecklistMotoristaReport(@NotNull Long codUnidade,
                                                      @NotNull long dataInicial,
                                                      @NotNull long dataFinal) {
        try {
            return dao.getTempoRealizacaoChecklistMotoristaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}