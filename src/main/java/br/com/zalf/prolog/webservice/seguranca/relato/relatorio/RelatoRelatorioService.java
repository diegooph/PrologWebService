package br.com.zalf.prolog.webservice.seguranca.relato.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 20/11/2017.
 */
public class RelatoRelatorioService {

    private RelatoRelatorioDao relatorioDao = new RelatoRelatorioDaoImpl();
    private static final String TAG = RelatoRelatorioService.class.getSimpleName();

    public void getRelatosEstratificadosCsv(Long codUnidade, Long dataInicial, Long dataFinal, String equipe, OutputStream out) {
        try {
            relatorioDao.getRelatosEstratificadosCsv(codUnidade, new Date(dataInicial), new Date(dataFinal), equipe, out);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório de estratificação dos relatos (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s \n" +
                    "equipe: %s \n", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString(), equipe), e);
        }
    }

    public Report getRelatosEstratificadosReport(Long codUnidade, Long dataInicial, Long dataFinal, String equipe) {
        try {
            return relatorioDao.getRelatosEstratificadosReport(codUnidade, new Date(dataInicial), new Date(dataFinal), equipe);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao gerar o relatório de estratificação dos relatos (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s \n" +
                    "equipe: %s \n", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString(), equipe), e);
            return null;
        }
    }
}
