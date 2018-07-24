package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.ControleIntervaloRelatorioService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.FolhaPontoRelatorio;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.init();
        final ControleIntervaloRelatorioService service = new ControleIntervaloRelatorioService();
        final List<FolhaPontoRelatorio> folhaPontoRelatorio = service.getFolhaPontoRelatorio(
                3L,
                "%",
                "%",
                "2018-01-01T10:00:00",
                "2019-07-01T10:00:00");
        System.out.println(GsonUtils.getGson().toJson(folhaPontoRelatorio));
    }
}