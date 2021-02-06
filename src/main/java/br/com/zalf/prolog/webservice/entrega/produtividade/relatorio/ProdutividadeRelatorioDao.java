package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Zart on 18/05/2017.
 */
public interface ProdutividadeRelatorioDao {

    void getConsolidadoProdutividadeCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final Long codUnidade,
                                        @NotNull final Date dataInicial,
                                        @NotNull final Date dataFinal) throws SQLException, IOException;

    Report getConsolidadoProdutividadeReport(@NotNull final Long codUnidade,
                                             @NotNull final Date dataInicial,
                                             @NotNull final Date dataFinal) throws SQLException;

    void getExtratoIndividualProdutividadeCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final String cpf,
                                              @NotNull final Long codUnidade,
                                              @NotNull final Date dataInicial,
                                              @NotNull final Date dataFinal) throws SQLException, IOException;

    Report getExtratoIndividualProdutividadeReport(@NotNull final String cpf,
                                                   @NotNull final Long codUnidade,
                                                   @NotNull final Date dataInicial,
                                                   @NotNull final Date dataFinal) throws SQLException;

    void getAcessosProdutividadeCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final String cpf,
                                    @NotNull final Long codUnidade,
                                    @NotNull final Date dataInicial,
                                    @NotNull final Date dataFinal) throws SQLException, IOException;

    Report getAcessosProdutividadeReport(@NotNull final String cpf,
                                         @NotNull final Long codUnidade,
                                         @NotNull final Date dataInicial,
                                         @NotNull final Date dataFinal) throws SQLException;

    @NotNull
    List<ProdutividadeColaboradorRelatorio> getRelatorioProdutividadeColaborador(
            @NotNull final Long codUnidade,
            @NotNull final String cpf,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws SQLException;
}
