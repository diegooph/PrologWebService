package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleIntervalosRelatorioService {

    private ControleIntervaloRelatoriosDao dao = new ControleIntervaloRelatorioDaoImpl();
    private static final String TAG = ControleIntervalosRelatorioService.class.getSimpleName();

    public void getIntervalosCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            dao.getIntervalosCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
        }
    }

    public Report getIntervalosReport(Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            return dao.getIntervalosReport(codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "cpf: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, cpf, dataInicial, dataFinal), e);
            return null;
        }
    }

    public void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            dao.getIntervalosMapasCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getIntervalosMapasReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getIntervalosMapasReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com os intervalos por mapas realizados (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            dao.getAderenciaIntervalosDiariaCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getAderenciaIntervalosDiariaReport(Long codUnidade, Long dataInicial, Long dataFinal) {
        try {
            return dao.getAderenciaIntervalosDiariaReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência diária(REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            dao.getAderenciaIntervalosColaboradorCsv(out, codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Long dataInicial, Long dataFinal, String cpf) {
        try {
            return dao.getAderenciaIntervalosColaboradorReport(codUnidade, new Date(dataInicial), new Date(dataFinal), cpf);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a aderência por colaborador(REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }
}
