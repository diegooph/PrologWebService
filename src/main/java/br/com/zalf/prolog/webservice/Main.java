package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.ControleJornadaAjusteService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.historico.MarcacaoAjusteHistoricoExibicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias.MarcacaoInconsistencia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.inconsistencias.TipoInconsistenciaMarcacao;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DatabaseManager.init();
        ControleJornadaAjusteService service = new ControleJornadaAjusteService();
        inconsistencias(service);
    }

    private static void inconsistencias(final ControleJornadaAjusteService service) {
        try {
            final List<MarcacaoInconsistencia> inconsistencias = service.getInconsistenciasColaboradorDia(
                    2272L,
                    "2018-02-27",
                    TipoInconsistenciaMarcacao.SEM_VINCULO.asString());
            System.out.println(GsonUtils.getGson().toJson(inconsistencias));
        } catch (ProLogException e) {
            e.printStackTrace();
        }
    }

    private static void historico(final ControleJornadaAjusteService service) {
        final List<Long> codMarcacoes = new ArrayList<>();
        codMarcacoes.add(66381L);
        codMarcacoes.add(59397L);
        codMarcacoes.add(66389L);
        try {
            final List<MarcacaoAjusteHistoricoExibicao> historicos = service.getHistoricoAjusteMarcacoes(codMarcacoes);
            System.out.println(GsonUtils.getGson().toJson(historicos));
        } catch (ProLogException e) {
            e.printStackTrace();
        }
    }
}