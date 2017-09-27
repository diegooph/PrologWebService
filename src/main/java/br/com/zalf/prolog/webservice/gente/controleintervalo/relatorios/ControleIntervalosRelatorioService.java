package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleIntervalosRelatorioService {

    private ControleIntervaloRelatoriosDao dao = new ControleIntervaloRelatorioDaoImpl();

    public void getIntervalosCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            dao.getIntervalosCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

}
