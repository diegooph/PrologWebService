package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleIntervaloRelatoriosDao {

    @NotNull
    void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    @NotNull
    Report getIntervalosReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

    @NotNull
    void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

    @NotNull
    Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException;

}
