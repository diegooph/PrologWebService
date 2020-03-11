package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ServicoRelatorioDao {

    /**
     * Método que busca um relatório contendo a estratificação de {@link Servico}s fechados no período filtrado.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getEstratificacaoServicosFechadosReport(@NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que busca um relatório contendo a estratificação de {@link Servico}s fechados no período filtrado.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     */
    void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidades,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    /**
     * Método que busca um relatório contendo a estratificação de {@link Servico}s abertos no período filtrado.
     * Para fins de visualização na aplicação.
     *
     * @param codUnidades - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @return - Um objeto {@link Report} com os dados filtrados.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     */
    @NotNull
    Report getEstratificacaoServicosAbertosReport(@NotNull final List<Long> codUnidades,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Método que busca um relatório contendo a estratificação de {@link Servico}s abertos no período filtrado.
     * Para fins de exportação em CSV.
     *
     * @param outputStream - Arquivo onde os dados serão armazenados para retornar.
     * @param codUnidades  - {@link List<Long>} de códigos das {@link Unidade}s.
     * @param dataInicial  - Data inicial do período de filtro.
     * @param dataFinal    - Data final do período de filtro.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     */
    void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws IOException, SQLException;
}