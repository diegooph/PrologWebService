package br.com.zalf.prolog.webservice.seguranca.relato.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 20/11/2017.
 */
public interface RelatoRelatorioDao {

    void getRelatosEstratificadosCsv(Long codUnidade, Date dataInicial, Date dataFinal, String equipe, OutputStream out)
            throws SQLException, IOException;

    @NotNull
    Report getRelatosEstratificadosReport(Long codUnidade, Date dataInicial, Date dataFinal, String equipe)
            throws SQLException;


    int getQtdRelatosRealizadosHoje(@NotNull final List<Long> codUnidades) throws SQLException;
}