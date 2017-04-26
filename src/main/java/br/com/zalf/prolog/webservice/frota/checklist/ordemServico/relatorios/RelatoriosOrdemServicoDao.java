package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.commons.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by luiz on 26/04/17.
 */
public interface RelatoriosOrdemServicoDao {

    void getItensMaiorQuantidadeNokCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull Date dataInicial,
                                       @NotNull Date dataFinal) throws SQLException, IOException;
    @NotNull
    Report getItensMaiorQuantidadeNokReport(@NotNull Long codUnidade,
                                            @NotNull Date dataInicial,
                                            @NotNull Date dataFinal) throws SQLException;
}