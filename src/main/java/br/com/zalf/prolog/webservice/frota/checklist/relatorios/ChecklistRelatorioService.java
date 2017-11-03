package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

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

    void getResumoChecklistCsv(@NotNull OutputStream outputStream,
                               @NotNull Long codUnidade,
                               @NotNull Long dataInicial,
                               @NotNull Long dataFinal,
                               @NotNull String placa) {
        try {
            dao.getResumoChecklistCsv(outputStream, codUnidade, new Date(dataInicial), new Date(dataFinal), placa);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    Report getResumoChecklistReport(@NotNull Long codUnidade,
                                    @NotNull Long dataInicial,
                                    @NotNull Long dataFinal,
                                    @NotNull String placa) {
        try {
            return dao.getResumoChecklistReport(codUnidade, new Date(dataInicial), new Date(dataFinal), placa);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull
    public Report getEstratificacaoRespostasNokChecklistReport(@NotNull Long codUnidade, @NotNull String placa,
                                            @NotNull Long dataInicial, @NotNull Long dataFinal) {
        try {
            return dao.getEstratificacaoRespostasNokChecklistReport(codUnidade, placa, new Date(dataInicial), new Date(dataFinal));
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getEstratificacaoRespostasNokChecklistCsv(@NotNull OutputStream outputStream, @NotNull Long codUnidade, @NotNull String placa,
                                       @NotNull Long dataInicial, @NotNull Long dataFinal) {
        try {
            dao.getEstratificacaoRespostasNokChecklistCsv(outputStream, codUnidade, placa, new Date(dataInicial), new Date(dataFinal));
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }
}