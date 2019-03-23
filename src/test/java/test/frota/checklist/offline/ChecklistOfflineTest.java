package test.frota.checklist.offline;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

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
    public void getModelosChecklistOffline() throws Throwable {

        // TESTE DE CHECKLIST ATUALIZADO
        ChecklistOfflineSupport checklistOfflineSupport =
                service.getChecklistOfflineSupport(10L, 5L, false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportAtualizado);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.ATUALIZADO,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        System.out.println(checklistOfflineSupport);

        // TESTE DE CHECKLIST SEM DADOS UNIDADE
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(10L, 6L, false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportSemDados);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertEquals(6L, (long) checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.SEM_DADOS,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        System.out.println(checklistOfflineSupport);

        // TESTE DE CHECKLIST ATUALIZACAO FORCADA - COM ATRIBUTO
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(10L, 5L, true);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportAtualizacaoForcada);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        ChecklistOfflineSupportAtualizacaoForcada checklistForcado =
                ((ChecklistOfflineSupportAtualizacaoForcada) checklistOfflineSupport);

        Assert.assertNotNull(checklistForcado.getTokenSincronizacaoDadosUnidade());
        Assert.assertEquals("a", checklistForcado.getTokenSincronizacaoDadosUnidade());
        Assert.assertNotNull(checklistForcado.getVersaoDadosUnidadeChecklist());
        Assert.assertEquals(10L, (long) checklistForcado.getVersaoDadosUnidadeChecklist());

        Assert.assertNotNull(checklistForcado.getModelosChecklistsDisponiveis());
        Assert.assertNotNull(checklistForcado.getColaboradoresChecklistOffline());
        Assert.assertNotNull(checklistForcado.getVeiculosChecklistOffline());
        Assert.assertNotNull(checklistForcado.getEmpresaChecklistOffline());

        System.out.println(checklistForcado);

        // TESTE DE CHECKLIST ATUALIZACAO FORCADA - SEM ATRIBUTO
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(100L, 5L, false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportAtualizacaoForcada);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.ATUALIZACAO_FORCADA,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        checklistForcado = ((ChecklistOfflineSupportAtualizacaoForcada) checklistOfflineSupport);

        Assert.assertNotNull(checklistForcado.getTokenSincronizacaoDadosUnidade());
        Assert.assertEquals("a", checklistForcado.getTokenSincronizacaoDadosUnidade());
        Assert.assertNotNull(checklistForcado.getVersaoDadosUnidadeChecklist());
        Assert.assertEquals(10L, (long) checklistForcado.getVersaoDadosUnidadeChecklist());

        Assert.assertNotNull(checklistForcado.getModelosChecklistsDisponiveis());
        Assert.assertNotNull(checklistForcado.getColaboradoresChecklistOffline());
        Assert.assertNotNull(checklistForcado.getVeiculosChecklistOffline());
        Assert.assertNotNull(checklistForcado.getEmpresaChecklistOffline());

        System.out.println(checklistForcado);

        // TESTE DE CHECKLIST DESATUALIZADO
        checklistOfflineSupport =
                service.getChecklistOfflineSupport(9L, 5L, false);

        Assert.assertNotNull(checklistOfflineSupport);
        Assert.assertTrue(checklistOfflineSupport instanceof ChecklistOfflineSupportDesatualizado);
        Assert.assertNotNull(checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertEquals(5L, (long) checklistOfflineSupport.getCodUnidadeDados());
        Assert.assertNotNull(checklistOfflineSupport.getEstadoChecklistOfflineSupport());
        Assert.assertEquals(
                EstadoChecklistOfflineSupport.DESATUALIZADO,
                checklistOfflineSupport.getEstadoChecklistOfflineSupport());

        final ChecklistOfflineSupportDesatualizado checklistDesatualizado =
                ((ChecklistOfflineSupportDesatualizado) checklistOfflineSupport);

        Assert.assertNotNull(checklistDesatualizado.getTokenSincronizacaoDadosUnidade());
        Assert.assertEquals("a", checklistDesatualizado.getTokenSincronizacaoDadosUnidade());
        Assert.assertNotNull(checklistDesatualizado.getVersaoDadosUnidadeChecklist());
        Assert.assertEquals(10L, (long) checklistDesatualizado.getVersaoDadosUnidadeChecklist());

        Assert.assertNotNull(checklistDesatualizado.getModelosChecklistsDisponiveis());
        Assert.assertNotNull(checklistDesatualizado.getColaboradoresChecklistOffline());
        Assert.assertNotNull(checklistDesatualizado.getVeiculosChecklistOffline());
        Assert.assertNotNull(checklistDesatualizado.getEmpresaChecklistOffline());

        System.out.println(checklistDesatualizado);
    }
}
