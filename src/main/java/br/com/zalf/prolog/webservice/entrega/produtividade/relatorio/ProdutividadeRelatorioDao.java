package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by Zart on 18/05/2017.
 */
public interface ProdutividadeRelatorioDao {

    void getConsolidadoProdutividadeCsv(@NotNull OutputStream outputStream,
                                        @NotNull Long codUnidade,
                                        @NotNull Date dataInicial,
                                        @NotNull Date dataFinal) throws SQLException, IOException;

    Report getConsolidadoProdutividadeReport(@NotNull Long codUnidade,
                                             @NotNull Date dataInicial,
                                             @NotNull Date dataFinal) throws SQLException;

    void getExtratoIndividualProdutividadeCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long cpf,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException;

    Report getExtratoIndividualProdutividadeReport(@NotNull Long cpf,
                                         @NotNull Date dataInicial,
                                         @NotNull Date dataFinal) throws SQLException;

}
