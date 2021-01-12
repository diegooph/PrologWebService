package test.br.com.zalf.prolog.webservice.pilares.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.ControleJornadaAjusteService;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteAtivacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.MarcacaoAjusteEdicao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

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
        final MarcacaoAjusteAtivacao marcacaoAjuste = createMarcacaoAjusteAtivacaoInativacao();
        System.out.println(GsonUtils.getGson().toJson(marcacaoAjuste));
        service.ativarMarcacaoAjuste("pkb88p0n605emj86l007g3m8u4", marcacaoAjuste);
    }

    @Test
    public void testEdicaoMarcacao() throws Throwable {
        final MarcacaoAjusteEdicao marcacaoAjuste = createMarcacaoAjusteEdicao();
        System.out.println(GsonUtils.getGson().toJson(marcacaoAjuste));
        service.editarMarcacaoAjuste("pkb88p0n605emj86l007g3m8u4", marcacaoAjuste);
    }

    @NotNull
    private MarcacaoAjusteEdicao createMarcacaoAjusteEdicao() {
        final MarcacaoAjusteEdicao ajusteEdicao = new MarcacaoAjusteEdicao();
        ajusteEdicao.setDataHoraNovaInserida(PrologDateParser.toLocalDateTime("2018-02-23T16:30:00"));
        ajusteEdicao.setCodJustificativaAjuste(1L);
        ajusteEdicao.setObservacaoAjuste("Dummy Data Test Edicao");
        ajusteEdicao.setDataHoraAjuste(LocalDateTime.now());
        ajusteEdicao.setCodMarcacaoEdicao(778L);
        return ajusteEdicao;
    }

    private MarcacaoAjusteAtivacao createMarcacaoAjusteAtivacaoInativacao() {
        final MarcacaoAjusteAtivacao ajusteAtivacaoInativacao = new MarcacaoAjusteAtivacao();
        ajusteAtivacaoInativacao.setCodMarcacaoAtivacao(57059L);
        ajusteAtivacaoInativacao.setCodJustificativaAjuste(1L);
        ajusteAtivacaoInativacao.setObservacaoAjuste("Dummy Data Test Inativacao");
        ajusteAtivacaoInativacao.setDataHoraAjuste(LocalDateTime.now());
        return ajusteAtivacaoInativacao;
    }

    @NotNull
    private MarcacaoAjusteAdicao createMarcacaoAjusteAdicao() {
        final MarcacaoAjusteAdicao ajusteAdicao = new MarcacaoAjusteAdicao();
        ajusteAdicao.setCodMarcacaoVinculo(57056L);
        ajusteAdicao.setDataHoraInserida(PrologDateParser.toLocalDateTime("2018-02-27T21:20:45"));
        ajusteAdicao.setCodJustificativaAjuste(1L);
        ajusteAdicao.setObservacaoAjuste("Dummy Data Test Adicao FIM");
        ajusteAdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setTipoInicioFim(TipoInicioFim.MARCACAO_FIM);
        return ajusteAdicao;
    }

    @NotNull
    private MarcacaoAjusteAdicaoInicioFim createMarcacaoAjusteAdicaoInicioFim() {
        final MarcacaoAjusteAdicaoInicioFim adicaoInicioFim = new MarcacaoAjusteAdicaoInicioFim();
        adicaoInicioFim.setCodColaboradorMarcacao(2272L);
        adicaoInicioFim.setCodTipoMarcacaoReferente(15L);
        adicaoInicioFim.setDataHoraInicio(PrologDateParser.toLocalDateTime("2018-09-16T15:40:41"));
        adicaoInicioFim.setDataHoraFim(PrologDateParser.toLocalDateTime("2018-09-16T19:38:41"));
        adicaoInicioFim.setCodJustificativaAjuste(5L);
        adicaoInicioFim.setObservacaoAjuste("Dummy Data Test Adicao Inicio e Fim");
        adicaoInicioFim.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return adicaoInicioFim;
    }
}
