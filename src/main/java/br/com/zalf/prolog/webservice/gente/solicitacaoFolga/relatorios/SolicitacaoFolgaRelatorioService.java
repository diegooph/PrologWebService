package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public class SolicitacaoFolgaRelatorioService {

    SolicitacaoFolgaRelatorioDao dao = new SolicitacaoFolgaRelatorioDaoImpl();
    private static final String TAG = SolicitacaoFolgaRelatorioService.class.getSimpleName();

    public void getResumoFolgasConcedidasCsv(Long codUnidade, OutputStream outputStream, long dataInicial, long dataFinal) {
        try {
            dao.getResumoFolgasConcedidasCsv(codUnidade, outputStream, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com o resumo de folgas concedidas (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, dataInicial, dataFinal), e);
        }
    }

    public Report getResumoFolgasConcedidasReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getResumoFolgasConcedidasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com o resumo de folgas concedidas (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, dataInicial, dataFinal), e);
            return null;
        }
    }

}
