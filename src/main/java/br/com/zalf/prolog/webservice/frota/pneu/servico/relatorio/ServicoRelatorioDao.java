package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;

public interface ServicoRelatorioDao {
    @NotNull
    Report getEstratificacaoServicosFechadosReport(@NotNull final Long codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws SQLException;

    void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final Long codUnidade,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    @NotNull
    Report getEstratificacaoServicosAbertosReport(@NotNull final Long codUnidade,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException;

    void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final Long codUnidade,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws IOException, SQLException;
}