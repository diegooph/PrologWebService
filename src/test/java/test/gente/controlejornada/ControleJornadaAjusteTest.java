package test.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.ControleJornadaAjusteService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.time.LocalDateTime;
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

    @Test
    public void testInserirAjusteAdicaoInicioFim() throws Throwable {
        final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste = createMarcacaoAjusteAdicaoInicioFim();
        System.out.println(GsonUtils.getGson().toJson(marcacaoAjuste));
        service.adicionarMarcacaoAjusteInicioFim("pkb88p0n605emj86l007g3m8u4", marcacaoAjuste);
    }

    @Test
    public void testAtivarInativarMarcacao() throws Throwable {
        final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste = createMarcacaoAjusteAtivacaoInativacao();
        System.out.println(GsonUtils.getGson().toJson(marcacaoAjuste));
        service.ativarInativarMarcacaoAjuste("pkb88p0n605emj86l007g3m8u4", marcacaoAjuste);
    }

    private MarcacaoAjusteAtivacaoInativacao createMarcacaoAjusteAtivacaoInativacao() {
        final MarcacaoAjusteAtivacaoInativacao ajusteAtivacaoInativacao = new MarcacaoAjusteAtivacaoInativacao();
        ajusteAtivacaoInativacao.setDeveAtivar(true);
        ajusteAtivacaoInativacao.setCodMarcacaoAtivacaoInativacao(57059L);
        ajusteAtivacaoInativacao.setCodJustificativaAjuste(1L);
        ajusteAtivacaoInativacao.setObservacaoAjuste("Dummy Data Test Inativacao");
        ajusteAtivacaoInativacao.setDataHoraAjuste(LocalDateTime.now());
        return ajusteAtivacaoInativacao;
    }

    @NotNull
    private MarcacaoAjusteAdicao createMarcacaoAjusteAdicao() {
        final MarcacaoAjusteAdicao ajusteAdicao = new MarcacaoAjusteAdicao();
        ajusteAdicao.setCodMarcacaoVinculo(57056L);
        ajusteAdicao.setDataHoraInserida(ProLogDateParser.toLocalDateTime("2018-02-27T21:20:45"));
        ajusteAdicao.setCodJustificativaAjuste(1L);
        ajusteAdicao.setObservacaoAjuste("Dummy Data Test Adicao FIM");
        ajusteAdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setMarcacaoInicioFim(MarcacaoInicioFim.MARCACAO_FIM);
        return ajusteAdicao;
    }

    @NotNull
    private MarcacaoAjusteAdicaoInicioFim createMarcacaoAjusteAdicaoInicioFim() {
        final MarcacaoAjusteAdicaoInicioFim adicaoInicioFim = new MarcacaoAjusteAdicaoInicioFim();
        adicaoInicioFim.setCodColaboradorMarcacao(2272L);
        adicaoInicioFim.setCodTipoMarcacaoReferente(15L);
        adicaoInicioFim.setDataHoraInicio(ProLogDateParser.toLocalDateTime("2018-09-16T15:40:41"));
        adicaoInicioFim.setDataHoraFim(ProLogDateParser.toLocalDateTime("2018-09-16T19:38:41"));
        adicaoInicioFim.setCodJustificativaAjuste(5L);
        adicaoInicioFim.setObservacaoAjuste("Dummy Data Test Adicao Inicio e Fim");
        adicaoInicioFim.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return adicaoInicioFim;
    }
}
