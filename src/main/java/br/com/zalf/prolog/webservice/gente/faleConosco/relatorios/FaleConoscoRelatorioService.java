package br.com.zalf.prolog.webservice.gente.faleConosco.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public class FaleConoscoRelatorioService {

    FaleConoscoRelatorioDao dao = new FaleConoscoRelatorioDaoImpl();

    public void getResumoCsv(Long codUnidade, OutputStream outputStream, Date dataInicial, Date dataFinal) {
        try {
            dao.getResumoCsv(codUnidade, outputStream, dataInicial, dataFinal);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getResumoReport(Long codUnidade, Date dataInicial, Date dataFinal) {
        try {
            return dao.getResumoReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
