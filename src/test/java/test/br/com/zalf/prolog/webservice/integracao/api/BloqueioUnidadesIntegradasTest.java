package test.br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.PneuService;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoService;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaService;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaService;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created on 2/12/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class BloqueioUnidadesIntegradasTest {
    private static final String USER_TOKEN_INTEGRADO = "f7hhsc8hchcpjolr1lt68tu2a4";
    private static final Long COD_EMPRESA_INTEGRADA = 48L;
    private static final Long COD_UNIDADE_LIBERADA = 228L;
    private static final Long COD_UNIDADE_BLOQUEADA = 230L;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    void testInsertVeiculoUnidadeLiberada() {
        final VeiculoCadastro veiculoCadastro = new VeiculoCadastro(
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_LIBERADA,
                "PRO-001",
                10L,
                13L,
                1L,
                1111L);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().insert(USER_TOKEN_INTEGRADO, veiculoCadastro);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertVeiculoUnidadeBloqueada() {
        final VeiculoCadastro veiculoCadastro = new VeiculoCadastro(
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_BLOQUEADA,
                "PRO-001",
                10L,
                13L,
                1L,
                1111L);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().insert(USER_TOKEN_INTEGRADO, veiculoCadastro);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdateVeiculoUnidadeLiberada() {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("PRO-002");
        veiculo.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);


        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().update(USER_TOKEN_INTEGRADO, "PRO-001", veiculo);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdateVeiculoUnidadeBloqueada() {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("PRO-002");
        veiculo.setCodUnidadeAlocado(COD_UNIDADE_BLOQUEADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().update(USER_TOKEN_INTEGRADO, "PRO-001", veiculo);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdateStatusVeiculoUnidadeLiberada() {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("PRO-002");
        veiculo.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);


        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().updateStatus(USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, "PRO-001", veiculo);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdateStatusVeiculoUnidadeBloqueada() {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("PRO-002");
        veiculo.setCodUnidadeAlocado(COD_UNIDADE_BLOQUEADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().updateStatus(USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, "PRO-001", veiculo);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testDeleteVeiculoUnidadeLiberada() {
        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().delete(USER_TOKEN_INTEGRADO, "BUP8601");
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testDeleteVeiculoUnidadeBloqueada() {
        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoService().delete(USER_TOKEN_INTEGRADO, "PRO-001");
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertPneuUnidadeLiberada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuService().insert(USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, pneu, true);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertPneuUnidadeBloqueada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuService().insert(USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, pneu, true);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdatePneuUnidadeLiberada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuService().update(USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, COD_UNIDADE_LIBERADA, pneu);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testUpdatePneuUnidadeBloqueada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuService().update(USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, COD_UNIDADE_BLOQUEADA, pneu);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertTransferenciaPneuUnidadeLiberada() {
        final PneuTransferenciaRealizacao pneuTransferenciaRealizacao =
                new PneuTransferenciaRealizacao(
                        COD_UNIDADE_LIBERADA,
                        COD_UNIDADE_LIBERADA,
                        222L,
                        new ArrayList<>(),
                        "teste");

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuTransferenciaService().insertTransferencia(USER_TOKEN_INTEGRADO, pneuTransferenciaRealizacao);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertTransferenciaPneuUnidadeBloqueada() {
        final PneuTransferenciaRealizacao pneuTransferenciaRealizacao =
                new PneuTransferenciaRealizacao(
                        COD_UNIDADE_LIBERADA,
                        COD_UNIDADE_BLOQUEADA,
                        222L,
                        new ArrayList<>(),
                        "teste");

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new PneuTransferenciaService().insertTransferencia(USER_TOKEN_INTEGRADO, pneuTransferenciaRealizacao);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertTransferenciaVeiculoUnidadeLiberada() {
        final ProcessoTransferenciaVeiculoRealizacao transferenciaVeiculoRealizacao =
                new ProcessoTransferenciaVeiculoRealizacao(
                        COD_EMPRESA_INTEGRADA,
                        COD_UNIDADE_LIBERADA,
                        COD_UNIDADE_LIBERADA,
                        222L,
                        new ArrayList<>(),
                        "teste");

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoTransferenciaService().insertProcessoTransferenciaVeiculo(USER_TOKEN_INTEGRADO, transferenciaVeiculoRealizacao);
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testInsertTransferenciaVeiculoUnidadeBloqueada() {
        final ProcessoTransferenciaVeiculoRealizacao transferenciaVeiculoRealizacao =
                new ProcessoTransferenciaVeiculoRealizacao(
                        COD_EMPRESA_INTEGRADA,
                        COD_UNIDADE_LIBERADA,
                        COD_UNIDADE_BLOQUEADA,
                        222L,
                        new ArrayList<>(),
                        "teste");

        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new VeiculoTransferenciaService().insertProcessoTransferenciaVeiculo(USER_TOKEN_INTEGRADO, transferenciaVeiculoRealizacao);
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testAberturaServicoPneuUnidadeLiberada() {
        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new ServicoService().getVeiculoAberturaServico(USER_TOKEN_INTEGRADO, 101L, "BUP8601");
        });
        assertTrue(throwable instanceof BloqueadoIntegracaoException);
    }

    @Test
    void testAberturaServicoPneuUnidadeBloqueada() {
        final Throwable throwable = assertThrows(ProLogException.class, () -> {
            new ServicoService().getVeiculoAberturaServico(USER_TOKEN_INTEGRADO, 101L, "KUU8785");
        });
        assertFalse(throwable instanceof BloqueadoIntegracaoException);
    }
}
