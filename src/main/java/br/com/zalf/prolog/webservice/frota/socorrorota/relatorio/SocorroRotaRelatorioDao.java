package br.com.zalf.prolog.webservice.frota.socorrorota.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 12/02/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface SocorroRotaRelatorioDao {

    /**
     * Método para gerar um relatório contendo todos os dados de socorros em rota em arquivo CSV.
     *
     * @param out                 Streaming onde os dados serão escritos.
     * @param codUnidades         Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial         Data inicial do período de filtro.
     * @param dataFinal           Data final do período de filtro.
     * @param statusSocorrosRotas Status dos socorros para o filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getDadosGeraisSocorrosRotasCsv(@NotNull final OutputStream out,
                                        @NotNull final List<Long> codUnidades,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        @NotNull final List<String> statusSocorrosRotas) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em formato {@link Report report}.
     *
     * @param codUnidades         Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial         Data inicial do período de filtro.
     * @param dataFinal           Data final do período de filtro.
     * @param statusSocorrosRotas Status dos socorros para o filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getDadosGeraisSocorrosRotasReport(@NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal,
                                             @NotNull final List<String> statusSocorrosRotas) throws Throwable;

}
