package br.com.zalf.prolog.webservice.frota.socorrorota.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.StatusSocorroRota;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/02/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface SocorroRotaRelatorioDao {

    void getDadosGeraisSocorrosRotasCsv(@NotNull final OutputStream out,
                                        @NotNull final List<Long> codUnidades,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        @NotNull final List<String> statusSocorrosRotas) throws Throwable;

    @NotNull
    Report getDadosGeraisSocorrosRotasReport(@NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal,
                                             @NotNull final List<String> statusSocorrosRotas) throws Throwable;

    @NotNull
    Map<StatusSocorroRota, Integer> getSocorrosPorStatus(@NotNull final List<Long> codUnidades) throws Throwable;

}
