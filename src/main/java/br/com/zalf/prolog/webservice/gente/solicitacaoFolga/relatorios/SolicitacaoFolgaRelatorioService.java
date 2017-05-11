package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.webservice.report.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public class SolicitacaoFolgaRelatorioService {

    SolicitacaoFolgaRelatorioDao dao = new SolicitacaoFolgaRelatorioDaoImpl();

    public void getResumoFolgasConcedidasCsv(Long codUnidade, OutputStream outputStream, long dataInicial, long dataFinal) {
        try {
            dao.getResumoFolgasConcedidasCsv(codUnidade, outputStream, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getResumoFolgasConcedidasReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getResumoFolgasConcedidasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
