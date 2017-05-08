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
public interface ChecklistRelatorioDao {

    void getChecklistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull Date dataInicial,
                                       @NotNull Date dataFinal) throws SQLException, IOException;
    @NotNull
    Report getChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                            @NotNull Date dataInicial,
                                            @NotNull Date dataFinal) throws SQLException;

    void getExtratoChecklistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long codUnidade,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException;
    @NotNull
    Report getExtratoChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                                   @NotNull Date dataInicial,
                                                   @NotNull Date dataFinal) throws SQLException;

    void getTempoRealizacaoChecklistMotoristaCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull Date dataInicial,
                                       @NotNull Date dataFinal) throws SQLException, IOException;
    @NotNull
    Report getTempoRealizacaoChecklistMotoristaReport(@NotNull Long codUnidade,
                                            @NotNull Date dataInicial,
                                            @NotNull Date dataFinal) throws SQLException;
}