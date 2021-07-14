package test.br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.PneuService;
import br.com.zalf.prolog.webservice.frota.pneu._model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoService;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.ServicoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaService;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaService;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo._model.VeiculoCadastroDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import javax.inject.Provider;
import java.math.BigDecimal;
import java.util.ArrayList;

import static com.google.common.truth.Truth.assertThat;
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

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

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
        final VeiculoCadastroDto veiculoCadastro = new VeiculoCadastroDto(
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_LIBERADA,
                "PRO-001",
                null,
                13L,
                1L,
                1111L,
                false);

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> new VeiculoService().insert(USER_TOKEN_INTEGRADO, veiculoCadastro));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testInsertVeiculoUnidadeBloqueada() {
        final VeiculoCadastroDto veiculoCadastro = new VeiculoCadastroDto(
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_BLOQUEADA,
                "PRO-001",
                null,
                13L,
                1L,
                1111L,
                false);

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> new VeiculoService().insert(USER_TOKEN_INTEGRADO, veiculoCadastro));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdateVeiculoUnidadeLiberada() {
        final VeiculoEdicao edicao = new VeiculoEdicao(
                -1L,
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_LIBERADA,
                "PRO-002",
                null,
                -1L,
                -1L,
                true,
                1,
                false);
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new VeiculoService().update(-1L, USER_TOKEN_INTEGRADO, edicao));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdateVeiculoUnidadeBloqueada() {
        final VeiculoEdicao edicao = new VeiculoEdicao(
                -1L,
                COD_EMPRESA_INTEGRADA,
                COD_UNIDADE_BLOQUEADA,
                "PRO-002",
                null,
                -1L,
                -1L,
                true,
                1,
                false);
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new VeiculoService().update(-1L, USER_TOKEN_INTEGRADO, edicao));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdateStatusVeiculoUnidadeLiberada() {
        // TODO:
        final VeiculoEdicaoStatus edicaoStatus = new VeiculoEdicaoStatus(-1L, true, false);
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new VeiculoService()
                        .updateStatus(-1L, USER_TOKEN_INTEGRADO, edicaoStatus));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdateStatusVeiculoUnidadeBloqueada() {
        final VeiculoEdicaoStatus edicaoStatus = new VeiculoEdicaoStatus(-1L, true, false);
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new VeiculoService()
                        .updateStatus(-1L, USER_TOKEN_INTEGRADO, edicaoStatus));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testInsertPneuUnidadeLiberada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodigoCliente("1223");
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);
        final Marca marca = new Marca();
        marca.setCodigo(312L);
        pneu.setMarca(marca);
        final ModeloPneu modelo = new ModeloPneu();
        modelo.setCodigo(12L);
        pneu.setModelo(modelo);
        pneu.setValor(new BigDecimal(1));
        pneu.setVidaAtual(1);
        pneu.setVidasTotal(3);
        pneu.setPressaoCorreta(120.0);
        final Pneu.Dimensao dimensao = new Pneu.Dimensao();
        dimensao.setCodigo(1L);
        pneu.setDimensao(dimensao);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new PneuService()
                        .insert(colaboradorAutenticadoProvider.get().getCodigo(),
                                USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, pneu, OrigemAcaoEnum.PROLOG_WEB, true));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testInsertPneuUnidadeBloqueada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodigoCliente("1223");
        pneu.setCodUnidadeAlocado(COD_UNIDADE_BLOQUEADA);
        final Marca marca = new Marca();
        marca.setCodigo(312L);
        pneu.setMarca(marca);
        final ModeloPneu modelo = new ModeloPneu();
        modelo.setCodigo(12L);
        pneu.setModelo(modelo);
        pneu.setValor(new BigDecimal(1));
        pneu.setVidaAtual(1);
        pneu.setVidasTotal(3);
        pneu.setPressaoCorreta(120.0);
        final Pneu.Dimensao dimensao = new Pneu.Dimensao();
        dimensao.setCodigo(1L);
        pneu.setDimensao(dimensao);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new PneuService()
                        .insert(colaboradorAutenticadoProvider.get().getCodigo(),
                                USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, pneu, OrigemAcaoEnum.PROLOG_WEB, true));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdatePneuUnidadeLiberada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new PneuService()
                        .update(colaboradorAutenticadoProvider.get().getCodigo(),
                                USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, COD_UNIDADE_LIBERADA, pneu));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testUpdatePneuUnidadeBloqueada() {
        final Pneu pneu = new PneuComum();
        pneu.setCodUnidadeAlocado(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new PneuService()
                        .update(colaboradorAutenticadoProvider.get().getCodigo(),
                                USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, COD_UNIDADE_BLOQUEADA, pneu));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
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

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new PneuTransferenciaService()
                        .insertTransferencia(USER_TOKEN_INTEGRADO, pneuTransferenciaRealizacao));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
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

        assertThat(new PneuTransferenciaService()
                .insertTransferencia(USER_TOKEN_INTEGRADO, pneuTransferenciaRealizacao)).isNotNull();
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

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new VeiculoTransferenciaService()
                        .insertProcessoTransferenciaVeiculo(USER_TOKEN_INTEGRADO, transferenciaVeiculoRealizacao));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
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

        assertThat(new VeiculoTransferenciaService()
                .insertProcessoTransferenciaVeiculo(USER_TOKEN_INTEGRADO, transferenciaVeiculoRealizacao)).isNotNull();
    }

    @Test
    void testFechaServicoPneuUnidadeLiberada() {
        final Servico servico = new ServicoMovimentacao();
        servico.setCodUnidade(COD_UNIDADE_LIBERADA);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ServicoService().fechaServico(USER_TOKEN_INTEGRADO, COD_UNIDADE_LIBERADA, servico));
        assertThat(throwable).isInstanceOf(BloqueadoIntegracaoException.class);
    }

    @Test
    void testFechaServicoPneuUnidadeBloqueada() {
        final Servico servico = new ServicoMovimentacao();
        servico.setCodUnidade(COD_UNIDADE_BLOQUEADA);

        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ServicoService().fechaServico(USER_TOKEN_INTEGRADO, COD_UNIDADE_BLOQUEADA, servico));
        assertThat(throwable).isNotInstanceOf(BloqueadoIntegracaoException.class);
    }
}
