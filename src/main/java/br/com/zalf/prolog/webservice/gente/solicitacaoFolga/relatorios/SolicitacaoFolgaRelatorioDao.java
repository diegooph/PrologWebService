package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public interface SolicitacaoFolgaRelatorioDao {

    void getResumoFolgasConcedidasCsv(@NotNull final Long codUnidade,
                                      @NotNull final OutputStream outputStream,
                                      @NotNull final Date dataInicial,
                                      @NotNull final Date dataFinal) throws IOException, SQLException;

    Report getResumoFolgasConcedidasReport(@NotNull final Long codUnidade,
                                           @NotNull final Date dataInicial,
                                           @NotNull final Date dataFinal) throws SQLException;
}
