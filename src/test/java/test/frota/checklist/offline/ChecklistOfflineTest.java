package test.frota.checklist.offline;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineTest extends BaseTest {
    private final Long CPF_COLABORADOR = 3383283194L;
    private ChecklistOfflineService service;

    @Override
    public void initialize() throws Throwable {
        service = new ChecklistOfflineService();
        DatabaseManager.init();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void getChecklistOfflineAtivoEmpresaTest() throws ProLogException {
        final boolean checklistOfflineAtivoEmpresa = service.getChecklistOfflineAtivoEmpresa(CPF_COLABORADOR);

        Assert.assertTrue(checklistOfflineAtivoEmpresa);
    }

    @Test
    public void insertChecklistOffline() throws ProLogException {
        final ChecklistInsercao checklist = createChecklist();
        final ResponseWithCod responseChecklistWithCod = service.insertChecklistOffline(
                "a",
                9,
                checklist);

        Assert.assertNotNull(responseChecklistWithCod);
    }

    @Test
    public void getModelosChecklistOffline() throws Throwable {
        // TESTE DE CHECKLIST ATUALIZADO
        ChecklistOfflineSupport checklistOfflineSupport =
                service.getChecklistOfflineSupport(
                        "a",
                        10L,
                        5L,
                        false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportSemDados);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidade());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidade());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.ATUALIZADO,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        System.out.println(checklistOfflineSupport);

        // TESTE DE CHECKLIST SEM DADOS UNIDADE
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(
                        "a",
                        10L,
                        6L,
                        false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportSemDados);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidade());
        Assert.assertEquals(6L, (long) checklistOfflineSupport.getCodUnidade());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.SEM_DADOS,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        System.out.println(checklistOfflineSupport);

        // TESTE DE CHECKLIST ATUALIZACAO FORCADA - COM ATRIBUTO
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(
                        "a",
                        9L,
                        5L,
                        true);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportComDados);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidade());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidade());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.DESATUALIZADO,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        ChecklistOfflineSupportComDados checklistDesatualizado =
                ((ChecklistOfflineSupportComDados) checklistOfflineSupport);

        ChecklistOfflineData checklistOfflineData = checklistDesatualizado.getChecklistOfflineData();

        Assert.assertNotNull(checklistOfflineData);
        Assert.assertEquals("a", checklistOfflineData.getTokenSincronizacaoDadosUnidade());
        Assert.assertNotNull(checklistOfflineData.getVersaoDadosUnidadeChecklist());
        Assert.assertEquals(10L, (long) checklistOfflineData.getVersaoDadosUnidadeChecklist());

        Assert.assertNotNull(checklistOfflineData.getModelosChecklistsDisponiveis());
        Assert.assertNotNull(checklistOfflineData.getColaboradoresChecklistOffline());
        Assert.assertNotNull(checklistOfflineData.getVeiculosChecklistOffline());
        Assert.assertNotNull(checklistOfflineData.getEmpresaChecklistOffline());

        System.out.println(checklistOfflineData);

        // TESTE DE CHECKLIST ATUALIZACAO FORCADA - COM ATRIBUTO
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(
                        "a",
                        10L,
                        5L,
                        true);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportComDados);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidade());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidade());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.ATUALIZADO,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        checklistDesatualizado =
                ((ChecklistOfflineSupportComDados) checklistOfflineSupport);

        checklistOfflineData = checklistDesatualizado.getChecklistOfflineData();

        Assert.assertNotNull(checklistOfflineData);
        Assert.assertEquals("a", checklistOfflineData.getTokenSincronizacaoDadosUnidade());
        Assert.assertNotNull(checklistOfflineData.getVersaoDadosUnidadeChecklist());
        Assert.assertEquals(10L, (long) checklistOfflineData.getVersaoDadosUnidadeChecklist());

        Assert.assertNotNull(checklistOfflineData.getModelosChecklistsDisponiveis());
        Assert.assertNotNull(checklistOfflineData.getColaboradoresChecklistOffline());
        Assert.assertNotNull(checklistOfflineData.getVeiculosChecklistOffline());
        Assert.assertNotNull(checklistOfflineData.getEmpresaChecklistOffline());

        System.out.println(checklistOfflineData);
    }

    private ChecklistInsercao createChecklist() {
        final ChecklistInsercao checklist = new ChecklistInsercao();
        checklist.setCodUnidade(5L);
        checklist.setCodModelo(1L);
        checklist.setCodColaborador(2272L);
        checklist.setCodVeiculo(3195L);
        checklist.setPlacaVeiculo("PRO0001");
        checklist.setTipo(TipoChecklist.SAIDA);
        checklist.setKmColetadoVeiculo(0);
        checklist.setTempoRealizacaoCheckInMillis(10000);
        checklist.setRespostas(createRespostas());
        checklist.setDataHoraRealizacao(ProLogDateParser.toLocalDateTime("2019-03-30T00:26:10"));
        checklist.setFonteDataHoraRealizacao(FonteDataHora.LOCAL_CELULAR);
        checklist.setVersaoAppMomentoRealizacao(50);
        checklist.setVersaoAppMomentoSincronizacao(53);
        checklist.setDeviceId("device didID");
        checklist.setDeviceUptimeRealizacaoMillis(10000);
        checklist.setDeviceUptimeSincronizacaoMillis(11000);
        return checklist;
    }

    @NotNull
    private List<ChecklistResposta> createRespostas() {
        final List<ChecklistResposta> respostas = new ArrayList<>();
        respostas.add(createRespostaOk());
        respostas.add(createRespostaNok());
        respostas.add(createRespostaNokTipOutros());
        return respostas;
    }

    @NotNull
    private ChecklistResposta createRespostaNokTipOutros() {
        final ChecklistResposta resposta = new ChecklistResposta();
        // RESPOSTA NOK
        resposta.setCodPergunta(1122L);
        final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

        ChecklistAlternativaResposta alternativa = new ChecklistAlternativaResposta();
        alternativa.setCodAlternativa(372L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(373L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(397L);
        alternativa.setAlternativaSelecionada(true);
        alternativa.setTipoOutros(true);
        alternativa.setRespostaTipoOutros("TESTE TIPO OUTROS NA RESPOSTA");

        alternativas.add(alternativa);
        resposta.setAlternativasRespostas(alternativas);
        return resposta;
    }

    @NotNull
    private ChecklistResposta createRespostaNok() {
        final ChecklistResposta resposta = new ChecklistResposta();
        // RESPOSTA NOK
        resposta.setCodPergunta(1121L);
        final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

        ChecklistAlternativaResposta alternativa = new ChecklistAlternativaResposta();
        alternativa.setCodAlternativa(322L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(327L);
        alternativa.setAlternativaSelecionada(true);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(361L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(true);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        resposta.setAlternativasRespostas(alternativas);
        return resposta;
    }

    @NotNull
    private ChecklistResposta createRespostaOk() {
        final ChecklistResposta resposta = new ChecklistResposta();
        // RESPOSTA OK
        resposta.setCodPergunta(1120L);
        final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

        ChecklistAlternativaResposta alternativa = new ChecklistAlternativaResposta();
        alternativa.setCodAlternativa(319L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(320L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(false);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        alternativa = new ChecklistAlternativaResposta();

        alternativa.setCodAlternativa(321L);
        alternativa.setAlternativaSelecionada(false);
        alternativa.setTipoOutros(true);
        alternativa.setRespostaTipoOutros(null);

        alternativas.add(alternativa);
        resposta.setAlternativasRespostas(alternativas);
        return resposta;
    }
}
