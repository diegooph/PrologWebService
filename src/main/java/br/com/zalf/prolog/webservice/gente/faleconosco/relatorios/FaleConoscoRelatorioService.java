package br.com.zalf.prolog.webservice.gente.faleconosco.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public class FaleConoscoRelatorioService {
    private static final String TAG = FaleConoscoRelatorioService.class.getSimpleName();
    private final FaleConoscoRelatorioDao dao = Injection.provideFaleConoscoRelatorioDao();

    public void getResumoCsv(final Long codUnidade, final OutputStream outputStream, final Date dataInicial, final Date dataFinal) {
        try {
            dao.getResumoCsv(codUnidade, outputStream, dataInicial, dataFinal);
        } catch (final SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar relatório de resumo dos " +
                    "fale conosco da unidade: %d\n" +
                    "Período: %s à %s", codUnidade, dataInicial.toString(), dataFinal.toString()), e);
            throw new RuntimeException(e);
        }
    }

    public Report getResumoReport(final Long codUnidade, final Date dataInicial, final Date dataFinal) {
        try {
            return dao.getResumoReport(codUnidade, dataInicial, dataFinal);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar relatório de resumo dos " +
                    "fale conosco da unidade: %d\n" +
                    "Período: %s à %s", codUnidade, dataInicial.toString(), dataFinal.toString()), e);
            throw new RuntimeException(e);
        }
    }
}