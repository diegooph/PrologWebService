package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ServicoRelatorioDao {

    @NotNull
    Report getEstratificacaoServicosFechadosReport(@NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws SQLException;

    void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidades,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    @NotNull
    Report getEstratificacaoServicosAbertosReport(@NotNull final List<Long> codUnidades,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException;

    void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws IOException, SQLException;
}