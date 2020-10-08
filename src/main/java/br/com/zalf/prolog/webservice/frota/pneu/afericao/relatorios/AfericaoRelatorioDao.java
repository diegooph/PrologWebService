package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoExportacaoProtheus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AfericaoRelatorioDao {
    /**
     * Método para buscar o relatório de cronograma das aferições de placas em CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getCronogramaAfericoesPlacasCsv(@NotNull final OutputStream out,
                                         @NotNull final List<Long> codUnidades,
                                         @NotNull final String userToken) throws Throwable;

    /**
     * Método para buscar o relatório de cronograma das aferições de placas em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getCronogramaAfericoesPlacasReport(@NotNull final List<Long> codUnidades,
                                              @NotNull final String userToken) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em arquivo CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial Data inicial do período de filtro.
     * @param dataFinal   Data final do período de filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getDadosGeraisAfericoesCsv(@NotNull final OutputStream out,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial Data inicial do período de filtro.
     * @param dataFinal   Data final do período de filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getDadosGeraisAfericoesReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método utilizado para buscar um objeto no padrão do Protheus para possibilitar a importação das aferições dentro
     * do sistema Protheus. Nesta busca, permitimos o filtro de data. Contudo, se qualquer uma das datas forem nulas,
     * o filtro de data será desconsiderado e buscaremos as aferições para todas as datas.
     *
     * @param codUnidades uma lista de unidades a serem usadas no filtro da busca.
     * @param codVeiculos uma lista de veículos a serem usados no filtro da busca.
     * @param dataInicial a data inicial a ser usada no filtro, podendo ser nula.
     * @param dataFinal   a data final a ser usada no filtro, podendo ser nula.
     * @return um objeto no padrão do Protheus com as informações das aferições.
     * @throws Throwable se qualquer erro ocorrer.
     */
    @NotNull
    List<AfericaoExportacaoProtheus> getExportacaoAfericoesProtheus(@NotNull final List<Long> codUnidades,
                                                                    @NotNull final List<Long> codVeiculos,
                                                                    @Nullable final LocalDate dataInicial,
                                                                    @Nullable final LocalDate dataFinal) throws Throwable;
}