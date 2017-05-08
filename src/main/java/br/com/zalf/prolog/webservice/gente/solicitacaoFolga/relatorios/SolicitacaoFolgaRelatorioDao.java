package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.commons.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public interface SolicitacaoFolgaRelatorioDao {

    void getResumoFolgasConcedidasCsv(Long codUnidade, OutputStream outputStream, Date dataInicial, Date dataFinal)
            throws IOException, SQLException;

    Report getResumoFolgasConcedidasReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException;
}
