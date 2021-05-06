package br.com.zalf.prolog.webservice.gente.faleconosco.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public interface FaleConoscoRelatorioDao {

    void getResumoCsv(Long codUnidade, OutputStream outputStream, Date dataInicial, Date dataFinal) throws IOException, SQLException;

    Report getResumoReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException;

}
