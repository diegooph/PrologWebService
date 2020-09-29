package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface VeiculoRelatorioDao {

    /**
     * Método que busca a contagem de veículos ativos de uma listagem de unidades.
     *
     * @param codUnidades - Códigos das unidades que serão filtradas.
     * @return - total de veículos ativos entre as unidades.
     * @throws SQLException - Se algum erro ocorrer na filtragem.
     */
    int getQtdVeiculosAtivos(@NotNull final List<Long> codUnidades) throws SQLException;

    /**
     * Método para buscar o relatório de listagem de veiculos em CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades para as quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getListagemVeiculosByUnidadeCsv(@NotNull final OutputStream out,
                                         @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para buscar o relatório de listagem de veiculos  em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades para as quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getListagemVeiculosByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para buscar o relatório de evolução de KM em CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codEmpresa  Código da empresa para a qual as informações serão filtradas.
     * @param placa       Placa do veículo para o qual as informações serão filtradas.
     * @throws Throwable  Se algum erro ocorrer.
     */
    void getEvolucaoKmCsv(@NotNull final OutputStream out,
                          @NotNull final Long codEmpresa,
                          @NotNull final String placa) throws Throwable;

    /**
     * Método para buscar o relatório de evolução de KM em formato {@link Report report}.
     *
     * @param codEmpresa  Código da empresa para a qual as informações serão filtradas.
     * @param placa       Placa do veículo para o qual as informações serão filtradas.
     * @throws Throwable  Se algum erro ocorrer.
     */
    @NotNull
    Report getEvolucaoKmReport(@NotNull final Long codEmpresa,
                               @NotNull final String placa) throws Throwable;



}