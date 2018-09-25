package test.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.ControleJornadaAjusteService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.util.Calendar;

/**
 * Created on 25/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaAjusteTest extends BaseTest {
    private ControleJornadaAjusteService service;

    @Override
    public void initialize() {
        service = new ControleJornadaAjusteService();
    }

    @Test
    public void testInserirAjusteAdicao() throws Throwable {
        final MarcacaoAjusteAdicao marcacaoAjusteAdicao = createMarcacaoAjusteAdicao();
        System.out.println(GsonUtils.getGson().toJson(marcacaoAjusteAdicao));
        service.adicionarMarcacaoAjuste("pkb88p0n605emj86l007g3m8u4", marcacaoAjusteAdicao);
    }

    @NotNull
    private MarcacaoAjusteAdicao createMarcacaoAjusteAdicao() {
        final MarcacaoAjusteAdicao ajusteAdicao = new MarcacaoAjusteAdicao();
        ajusteAdicao.setCodMarcacaoVinculo(2546L);
        ajusteAdicao.setDataHoraInserida(ProLogDateParser.toLocalDateTime("2018-02-27T17:25:45"));
        ajusteAdicao.setCodJustificativaAjuste(1L);
        ajusteAdicao.setObservacaoAjuste("Dummy Data Test");
        ajusteAdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setMarcacaoInicioFim(MarcacaoInicioFim.MARCACAO_INICIO);
        return ajusteAdicao;
    }
}
