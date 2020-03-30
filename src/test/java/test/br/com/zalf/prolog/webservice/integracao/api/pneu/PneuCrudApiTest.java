package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.ApiPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.*;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioResource;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 25/02/2020
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class PneuCrudApiTest extends BaseTest {
    @NotNull
    private static final Random RANDOM = new Random();
    @NotNull
    private static final String TOKEN_INTEGRACAO = "TOKEN" + RANDOM.nextInt(999999999);
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static final Long COD_UNIDADE = 5L;
    private ApiCadastroPneuService apiCadastroPneuService;
    private ApiPneuService apiPneuService;
    private IntegracaoPraxioResource integracaoPraxioResource;
    private DatabaseConnectionProvider connectionProvider;

    @BeforeAll
    public void initialize() {
        try {
            DatabaseManager.init();
            this.apiCadastroPneuService = new ApiCadastroPneuService();
            this.apiPneuService = new ApiPneuService();
            this.connectionProvider = new DatabaseConnectionProvider();
            this.integracaoPraxioResource = new IntegracaoPraxioResource();
            insereTokenIntegracaoParaEmpresa();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @AfterAll
    public void destroy() {
        try {
            removeTokenIntegracaoCriado();
            DatabaseManager.finish();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private Long geraCodSistemaIntegrado() {
        return RANDOM.nextLong();
    }

    private String geraCodCliente() {
        return "PN" + RANDOM.nextInt(999999999);
    }

    @Test
    @DisplayName("Teste Inserção Carga Inicial de Pneus sem erros")
    void adicionaCargaInicialPneuSemErroTest() throws Throwable {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(criaPneuSemErroComCodigoClienteValido());
        cargaInicial.add(criaPneuSemErroComUnidadeValida());
        cargaInicial.add(criaPneuSemErroComModeloPneuValido());
        cargaInicial.add(criaPneuSemErroComDimensaoValida());
        cargaInicial.add(criaPneuSemErroComPressaoValida());
        cargaInicial.add(criaPneuSemErroComVidaAtualValida());
        cargaInicial.add(criaPneuSemErroComDotValido());
        cargaInicial.add(criaPneuSemErroComModeloDeBandaValido());

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isTrue();
        }

        //Verifica se os pneus foram inseridos
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());

            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEstoque(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus em posições do veículo")
    void adicionaCargaInicialPneuEmVeiculo() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        //Cria pneu com as posições.
        for (Integer integer : posicoesPlaca) {
            cargaInicial.add(criaPneuComPosicoesEspecificas(integer,
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu.
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus em posições do veículo logo após movendo todos eles para " +
            "estoque")
    void adicionaCargaInicialPneuEmVeiculoDepoisAtualizaTodosPneusParaEstoque() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        for (Integer integer : posicoesPlaca) {
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(integer,
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu salvo.
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }

        //Cria pneu para atualizar status em estoque.
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusEstoqueSemErroCargaInicial(apiPneuCargaInicial));
        }

        //Excecução (Atualiza os pneu para estoque).
        final SuccessResponseIntegracao successResponseIntegracaoPneusAtualizados =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Valida informações se todos os pneus foram movidos para estoque.
        assertThat(successResponseIntegracaoPneusAtualizados.getMsg()).isEqualTo("Pneus atualizados com " +
                "sucesso");
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus em posições do veículo logo após movendo todos eles para " +
            "descarte")
    void adicionaCargaInicialPneuEmVeiculoDepoisAtualizaTodosPneusParaDescarte() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        for (Integer integer : posicoesPlaca) {
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(integer,
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu salvo.
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }

        //Cria pneu para atualizar status em descarte.
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusDescarteSemErroCargaInicial(apiPneuCargaInicial));
        }

        //Excecução (Atualiza os pneu para descarte).
        final SuccessResponseIntegracao successResponseIntegracaoPneusAtualizados =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        assertThat(successResponseIntegracaoPneusAtualizados.getMsg()).isEqualTo("Pneus atualizados com " +
                "sucesso");
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus em posições do veículo logo após movendo todos eles para " +
            "análise")
    void adicionaCargaInicialPneuEmVeiculoDepoisAtualizaTodosPneusParaAnalise() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        for (Integer integer : posicoesPlaca) {
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(integer,
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu salvo.
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }

        //Cria pneu para atualizar status em análise.
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusAnaliseSemErroCargaInicial(apiPneuCargaInicial));
        }

        //Excecução (Atualiza os pneu para análise).
        final SuccessResponseIntegracao successResponseIntegracaoPneusAtualizados =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        assertThat(successResponseIntegracaoPneusAtualizados.getMsg()).isEqualTo("Pneus atualizados com " +
                "sucesso");
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus e transferindo eles a outra unidade")
    void adicionaCargaInicialPneuETransfereElesParaOutraUnidade() throws Throwable {
        //Cria pneus
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(criaPneuSemErroComCodigoClienteValido());
        cargaInicial.add(criaPneuSemErroComUnidadeValida());
        cargaInicial.add(criaPneuSemErroComModeloPneuValido());
        cargaInicial.add(criaPneuSemErroComDimensaoValida());
        cargaInicial.add(criaPneuSemErroComPressaoValida());
        cargaInicial.add(criaPneuSemErroComVidaAtualValida());
        cargaInicial.add(criaPneuSemErroComDotValido());
        cargaInicial.add(criaPneuSemErroComModeloDeBandaValido());

        //Adiciona pneus
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isTrue();
        }

        List<String> pneusTransferencia = new ArrayList<>();
        //Verifica se os pneus foram inseridos corretamente
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());

            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEstoque(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
            pneusTransferencia.add(apiPneuCargaInicial.getCodigoCliente());
        }

        //Realiza transferência de unidade
        final Long novaUnidade = buscaUmaUnidadeDaEmpresa();
        final Long cpfColaboradorUnidadeOrigem = buscaUmCpfDaUnidade();
        final String observacao = "Teste de Pneus";

        //Cria objeto ApiPneuTransferencia
        final ApiPneuTransferencia apiPneuTransferencia = new ApiPneuTransferencia(
                COD_UNIDADE,
                novaUnidade,
                cpfColaboradorUnidadeOrigem.toString(),
                pneusTransferencia,
                observacao);

        //Realiza chamada para o método de transferencia de pneu
        final SuccessResponseIntegracao pneusTransferidos =
                apiCadastroPneuService.transferirPneu(TOKEN_INTEGRACAO, apiPneuTransferencia);

        assertThat(pneusTransferidos.getMsg()).isEqualTo("Transferência de pneus realizada com sucesso no " +
                "Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando pneus e transferindo eles a outra unidade")
    void adicionaCargaInicialPneuEmUmVeiculoDepoisTransfereVeiculo() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        //Verifica se veículo foi salvo.
        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        //Busca posições do veículo criado.
        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        for (Integer integer : posicoesPlaca) {
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(integer,
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }

        //Adiciona pneus
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente());
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu salvo.
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(codSistemaIntegradoPneu).isEqualTo(apiPneuCargaInicial.getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
        }

        //Chama o método para transferir placa


    }

    @Test
    @DisplayName("Teste Carga Inicial adicionando dois pneus no veículo e logo após inverter as posições dos pneus")
    void adicionaCargaInicialPneuEmVeiculoDepoisInvertePosicaoDosPneus() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        //Verifica se veículo foi salvo.
        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        //Busca posições do veículo criado.
        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());

        //Cria pneu com as posições para carga inicial.
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(0), veiculoCadastroPraxio.getPlacaVeiculo()));
        cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(1), veiculoCadastroPraxio.getPlacaVeiculo()));

        //Adiciona carga inicial.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            //Busca informações do pneu.
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente());
            //Valida todas as informações do pneu salvo.
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCargaInicial.
                    getCodigoSistemaIntegrado());
            assertThat(apiPneuCargaInicialInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCargaInicial.
                    getCodigoCliente());
            assertThat(apiPneuCargaInicialInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCargaInicial.
                    getCodUnidadePneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodModeloPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCargaInicial.
                    getCodDimensaoPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCargaInicial.
                    getPressaoCorretaPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaAtualPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCargaInicial.
                    getVidaTotalPneu());
            assertThat(apiPneuCargaInicialInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCargaInicial.
                    getPneuNovoNuncaRodado());
            assertThat(apiPneuCargaInicialInfoPneu.getPlacaVeiculoPneuAplicado()).isEqualTo(apiPneuCargaInicial.
                    getPlacaVeiculoPneuAplicado());
            assertThat(apiPneuCargaInicialInfoPneu.getPosicaoPneuAplicado()).isEqualTo(apiPneuCargaInicial.
                    getPosicaoPneuAplicado());
        }
        //Cria pneu para atualizar posições.
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();

        //Inverte posições.
        apiPneuAlteracaoStatus.add(criaPneuParaTrocarPosicaoPneuEmUso(cargaInicial.get(0), posicoesPlaca.get(1)));
        apiPneuAlteracaoStatus.add(criaPneuParaTrocarPosicaoPneuEmUso(cargaInicial.get(1), posicoesPlaca.get(0)));

        //Atualiza pneus.
        final SuccessResponseIntegracao successResponseIntegracaoPneusAtualizados =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Valida se os pneus foram atualizados corretamente.
        assertThat(successResponseIntegracaoPneusAtualizados.getMsg()).isEqualTo("Pneus atualizados com " +
                "sucesso");
    }

    @Test
    @DisplayName("Teste Carga Inicial com código da unidade inválido")
    void adicionaCargaInicialPneuComErroCodUnidadeNaoExisteTest() throws Throwable {
        //Cenário
        final Long codUnidade = 909090L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                codUnidade,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código do modelo do pneu inválido")
    void adicionaCargaInicialPneuComErroCodModeloPneuNaoExisteTest() throws Throwable {
        //Cenário
        final Long codModeloPneu = 5754343L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                codModeloPneu,
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código dimensão inválido")
    void adicionaCargaInicialPneuComErroCodDimensaoNaoExisteTest() throws Throwable {
        //Cenário
        final Long codDimensao = 4543235L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                codDimensao,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com pressão inválida")
    void adicionaCargaInicialPneuComErroPressaoIncorretaTest() throws Throwable {
        //Cenário
        final Double codPressao = -120.0;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                codPressao,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com valor do pneu inválido")
    void adicionaCargaInicialPneuComErroValorPneuInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valorPneu = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                valorPneu,
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com vida atual do pneu maior que a vida total")
    void adicionaCargaInicialPneuComErroVidaAtualMaiorQueTotalTest() throws Throwable {
        //Cenário
        final int vidaAtual = 5;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                vidaAtual,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com modelo de banda inválido")
    void adicionaCargaInicialPneuComErroModeloBandaInvalidoTest() throws Throwable {
        //Cenário
        final Long modeloBanda = -1L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                3,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                modeloBanda,
                new BigDecimal("400.00"),
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com valor da banda inválido")
    void adicionaCargaInicialPneuComErroValorBandaInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valorBanda = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                3,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                valorBanda,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com placa inválida")
    void adicionaCargaInicialPneuComErroPlacaPneuNaoExisteTest() throws Throwable {
        //Cenário
        final String placa = buscaPlacaUnidade();
        final List<Integer> posicoes = buscaPosicaoesPlaca(placa);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("400.00"),
                ApiStatusPneu.EM_USO,
                placa + "ERRO",
                posicoes.get(0)));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com posição do pneu em relação ao veículo inválida")
    void adicionaCargaInicialPneuComErroPosicaoPneuInvalidaTest() throws Throwable {
        //Cenário
        final String placa = buscaPlacaUnidade();
        final List<Integer> posicoes = buscaPosicaoesPlaca(placa);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("400.00"),
                ApiStatusPneu.EM_USO,
                placa,
                posicoes.get(0) + 9090));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial de um pneu existente no banco com vida atual = 3 sendo sobrescrito para vida " +
            "atual = 1")
    void sobrescrevePneuJaCadastradoComVidaMenorQueAtualCargaInicialSemErroTest() throws Throwable {
        //Ativa configuração da empresa
        ativaSobrescritaPneuEmpresa();
        int vidaAtualPneu = 1;

        //Cenário específico da PLI-4 (Erro ao sobrescrever pneus que voltam para vida 1);
        //Cria pneu com vida atual = 3;
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();

        //Execução: Adiciona pneu;
        final SuccessResponseIntegracao successResponseIntegracao =
                apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();

        //Valida se pneu foi inserido;
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuInserido = buscaInformacoesPneuCargaInicialEstoque(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente());

        //Valida todas as informações do pneu inserido;
        assertThat(apiPneuCargaInicialInfoPneuInserido).isNotNull();
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCadastro.
                getCodigoSistemaIntegrado());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCadastro.
                getCodigoSistemaIntegrado());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodigoCliente()).isEqualTo(apiPneuCadastro.
                getCodigoCliente());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodUnidadePneu()).isEqualTo(apiPneuCadastro.
                getCodUnidadePneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodModeloPneu()).isEqualTo(apiPneuCadastro.
                getCodModeloPneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getCodDimensaoPneu()).isEqualTo(apiPneuCadastro.
                getCodDimensaoPneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getPressaoCorretaPneu()).isEqualTo(apiPneuCadastro.
                getPressaoCorretaPneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getVidaAtualPneu()).isEqualTo(apiPneuCadastro.
                getVidaAtualPneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getVidaTotalPneu()).isEqualTo(apiPneuCadastro.
                getVidaTotalPneu());
        assertThat(apiPneuCargaInicialInfoPneuInserido.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCadastro.
                getPneuNovoNuncaRodado());

        //Usa pneu já inserido para carga inicial, mas o pneu agora passa a ter vida atual = 1;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                apiPneuCadastro.getCodModeloPneu(),
                apiPneuCadastro.getCodDimensaoPneu(),
                apiPneuCadastro.getPressaoCorretaPneu(),
                vidaAtualPneu,
                apiPneuCadastro.getVidaTotalPneu(),
                apiPneuCadastro.getDotPneu(),
                apiPneuCadastro.getValorPneu(),
                apiPneuCadastro.getPneuNovoNuncaRodado(),
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se pneu foi inserido;
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuAtualizado = buscaInformacoesPneuCargaInicialEstoque(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente());

        //Valida todas as informações do pneu inserido;
        assertThat(apiPneuCargaInicialInfoPneuAtualizado).isNotNull();
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCadastro.
                getCodigoSistemaIntegrado());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCadastro.
                getCodigoSistemaIntegrado());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodigoCliente()).isEqualTo(apiPneuCadastro.
                getCodigoCliente());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodUnidadePneu()).isEqualTo(apiPneuCadastro.
                getCodUnidadePneu());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodModeloPneu()).isEqualTo(apiPneuCadastro.
                getCodModeloPneu());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getCodDimensaoPneu()).isEqualTo(apiPneuCadastro.
                getCodDimensaoPneu());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getPressaoCorretaPneu()).isEqualTo(apiPneuCadastro.
                getPressaoCorretaPneu());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getVidaAtualPneu()).isEqualTo(vidaAtualPneu);
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getVidaTotalPneu()).isEqualTo(apiPneuCadastro.
                getVidaTotalPneu());
        assertThat(apiPneuCargaInicialInfoPneuAtualizado.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCadastro.
                getPneuNovoNuncaRodado());

        //Desativa configuração da empresa
        desativaSobrescritaPneuEmpresa();

        //Verificações
        final int vidaAtualPneuAtualizado =
                buscaVidaAtualPneuAtualizado(
                        apiPneuCadastro.getCodigoSistemaIntegrado(),
                        apiPneuCadastro.getCodigoCliente());
        assertThat(vidaAtualPneuAtualizado).isEqualTo(vidaAtualPneu);
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isTrue();
        }
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu sem erro")
    void adicionaPneuSemErroTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();
        //Execução
        final SuccessResponseIntegracao successResponseIntegracao =
                apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);
        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
        //Verifica se realmente o pneu foi salvo no banco
        final Long codSistemaIntegradoPneu =
                buscaCodSistemaIntegradoPneuInserido(
                        apiPneuCadastro.getCodigoSistemaIntegrado(),
                        apiPneuCadastro.getCodigoCliente());
        final ApiPneuCadastro apiPneuCadastroInfoPneu = buscaInformacoesPneu(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente());
        //Valida todas as informações do pneu.
        assertThat(codSistemaIntegradoPneu).isNotNull();
        assertThat(apiPneuCadastro.getCodigoSistemaIntegrado()).isEqualTo(codSistemaIntegradoPneu);
        assertThat(apiPneuCadastroInfoPneu.getCodigoSistemaIntegrado()).isEqualTo(apiPneuCadastro.
                getCodigoSistemaIntegrado());
        assertThat(apiPneuCadastroInfoPneu.getCodigoCliente()).isEqualTo(apiPneuCadastro.getCodigoCliente());
        assertThat(apiPneuCadastroInfoPneu.getCodUnidadePneu()).isEqualTo(apiPneuCadastro.getCodUnidadePneu());
        assertThat(apiPneuCadastroInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCadastro.getCodModeloPneu());
        assertThat(apiPneuCadastroInfoPneu.getCodDimensaoPneu()).isEqualTo(apiPneuCadastro.getCodDimensaoPneu());
        assertThat(apiPneuCadastroInfoPneu.getPressaoCorretaPneu()).isEqualTo(apiPneuCadastro.getPressaoCorretaPneu());
        assertThat(apiPneuCadastroInfoPneu.getVidaAtualPneu()).isEqualTo(apiPneuCadastro.getVidaAtualPneu());
        assertThat(apiPneuCadastroInfoPneu.getVidaTotalPneu()).isEqualTo(apiPneuCadastro.getVidaTotalPneu());
        assertThat(apiPneuCadastroInfoPneu.getDotPneu()).isEqualTo(apiPneuCadastro.getDotPneu());
        assertThat(apiPneuCadastroInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCadastro.
                getPneuNovoNuncaRodado());
        assertThat(apiPneuCadastroInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCadastro.getCodModeloPneu());
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com unidade inválida")
    void adicionaPneuComErroUnidadeInvalidaTest() throws Throwable {
        final Long codUnidade = 11153423L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                codUnidade,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));
        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A Unidade " + codUnidade + " repassada não existe no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com código modelo inválido")
    void adicionaPneuComErroCodModeloPneuInvalidoTest() throws Throwable {
        final Long codModelo = 909090L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                codModelo,
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O modelo do pneu " + codModelo + " não está mapeado no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com código modelo banda inválido")
    void adicionaPneuComErroCodModeloBandaInvalidoTest() throws Throwable {
        final Long codModeloBanda = -1L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                2,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                codModeloBanda,
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O modelo da banda " + codModeloBanda + " do pneu não está mapeado no " +
                        "Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com código dimensão inválido")
    void adicionaPneuComErroCodDimensaoInvalidoTest() throws Throwable {
        final Long codDimensao = 9999999L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                codDimensao,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A dimensão de código " + codDimensao + " do pneu não está mapeada no " +
                        "Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com pressão inválida")
    void adicionaPneuComErroPressaoInvalidaTest() throws Throwable {
        //Cenário
        final Double pressaoPneu = -120.0;
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                pressaoPneu,
                1,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A pressão recomendada para o pneu não pode ser um número negativo");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com vida atual inválida")
    void adicionaPneuComErroVidaAtualInvalidaTest() throws Throwable {
        //Cenário
        final int vidaAtual = 5;
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                vidaAtual,
                4,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A vida total do pneu não pode ser menor que a vida atual");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com vida atual inválida")
    void adicionaPneuComErroVidaTotalInvalidaTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                5,
                1,
                "1010",
                new BigDecimal("1000.00"),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A vida total do pneu não pode ser menor que a vida atual");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com valor pneu inválido")
    void adicionaPneuComErroValorPneuInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valor = new BigDecimal("-1.00");
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                valor,
                true,
                null,
                null);

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O valor do pneu não pode ser um número negativo");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com valor banda inválido")
    void adicionaPneuComErroValorBandaInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valor = new BigDecimal("-1.00");
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                2,
                4,
                "1010",
                new BigDecimal("100.00"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                valor);

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O valor da banda do pneu não pode ser um número negativo");
    }

    @Test
    @DisplayName("Teste Atualiza status do pneu sem erros")
    void atualizaStatusPneuSemErroTest() throws Throwable {
        //Cria e salva 4 pneus
        List<ApiPneuCadastro> pneusSalvos = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            //Cenário
            final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();
            //Execução
            final SuccessResponseIntegracao successResponseIntegracaoInserido =
                    apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);
            //Verificações
            assertThat(successResponseIntegracaoInserido).isNotNull();
            assertThat(successResponseIntegracaoInserido.getMsg()).isNotEmpty();
            //Verifica se realmente o pneu foi salvo no banco
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCadastro.getCodigoSistemaIntegrado(),
                            apiPneuCadastro.getCodigoCliente());
            assertThat(codSistemaIntegradoPneu).isNotNull();
            assertThat(apiPneuCadastro.getCodigoSistemaIntegrado()).isEqualTo(codSistemaIntegradoPneu);
            //Guarda pneus
            pneusSalvos.add(apiPneuCadastro);
        }

        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusAnaliseSemErro(pneusSalvos.get(0)));
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusDescarteSemErro(pneusSalvos.get(1)));
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusEmUsoSemErro(pneusSalvos.get(2)));
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusEstoqueSemErro(pneusSalvos.get(3)));

        //Excecução
        final SuccessResponseIntegracao successResponseIntegracao =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Verifica se os dados foram salvos como previstos
        for (ApiPneuAlteracaoStatus pneuAlteracaoStatus : apiPneuAlteracaoStatus) {
            final Long codPneuProlog =
                    buscaCodPneuCadastroProlog(
                            pneuAlteracaoStatus.getCodigoSistemaIntegrado(),
                            pneuAlteracaoStatus.getCodigoCliente());
            final boolean verificaPneu = verificaSePneuFoiAtualizado(
                    codPneuProlog,
                    pneuAlteracaoStatus.getStatusPneu().toString());
            assertThat(verificaPneu).isTrue();
        }

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
    }

    @Test
    @DisplayName("Teste Atualiza status do pneu com erro no código sistema integrado")
    void atualizaStatusPneuComErroCodSistemaIntegradoTest() throws Throwable {
        final Long codSistemaIntegrado = 611772312L;
        //Busca Pneu
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade();
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                codSistemaIntegrado,
                apiPneuCadastro.getCodigoCliente(),
                COD_UNIDADE,
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiPneuService().atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O pneu de código interno " + codSistemaIntegrado + " não está mapeado no " +
                        "Sistema ProLog");
    }

    @Test
    @DisplayName("Teste atualiza status do pneu com código unidade inválido")
    void atualizaStatusPneuComErroCodigoUnidadeInvalidoTest() throws Throwable {
        final Long codUnidade = 115431234L;
        //Busca Pneu
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade();
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                codUnidade,
                "12345678910",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("69.00")));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiPneuService().atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A Unidade " + codUnidade + " repassada não existe no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste atualiza status do pneu com código modelo de banda inválido")
    void atualizaStatusPneuComErroCodigoModeloBandaInvalidoTest() throws Throwable {
        final Long codModeloBandaPneu = 10908787L;
        //Busca Pneu
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade();
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusEstoque(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                codModeloBandaPneu,
                new BigDecimal("69.00")));
        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiPneuService().atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus));
        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O modelo da banda do pneu " + codModeloBandaPneu + " não está mapeado no " +
                        "Sistema ProLog");
    }

    //Métodos com acesso ao banco de dados.
    //Método responśavel por criar TOKEN de autenticação.
    private void insereTokenIntegracaoParaEmpresa() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.TOKEN_INTEGRACAO(COD_EMPRESA, TOKEN_INTEGRACAO) " +
                    "VALUES (?, ?) ON CONFLICT (COD_EMPRESA) DO UPDATE SET TOKEN_INTEGRACAO = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setString(2, TOKEN_INTEGRACAO);
            stmt.setString(3, TOKEN_INTEGRACAO);
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                throw new SQLException("Erro ao criar TOKEN");
            }
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por remover TOKEN de autenticação.
    private void removeTokenIntegracaoCriado() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("DELETE FROM INTEGRACAO.TOKEN_INTEGRACAO WHERE COD_EMPRESA = ? " +
                    "AND TOKEN_INTEGRACAO = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setString(2, TOKEN_INTEGRACAO);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao deletar TOKEN");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Método responsável pela configuração de sobrescrita de uma empresa.
    private void ativaSobrescritaPneuEmpresa() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL(" +
                    "COD_EMPRESA, " +
                    "SOBRESCREVE_PNEUS, " +
                    "SOBRESCREVE_VEICULOS) " +
                    "VALUES(?,?,?) ON CONFLICT(COD_EMPRESA) DO NOTHING");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setBoolean(2, true);
            stmt.setBoolean(3, false);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao ativar configuração de sobrescrita do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    private Long buscaUmaUnidadeDaEmpresa() throws Throwable{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> codUnidadeEmpresa = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT U.CODIGO FROM UNIDADE U WHERE U.COD_EMPRESA = ? AND CODIGO <> ?");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setLong(2, COD_UNIDADE);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                codUnidadeEmpresa.add(rSet.getLong("CODIGO"));
            }
            if (!codUnidadeEmpresa.isEmpty()) {
                return codUnidadeEmpresa.get(0);
            } else {
                throw new SQLException("Erro ao buscar código unidade");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar código unidade");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    private Long buscaUmCpfDaUnidade() throws Throwable{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> listaComCpfs = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT C.CPF FROM COLABORADOR_DATA C WHERE C.COD_UNIDADE = ?");
            stmt.setLong(1, COD_UNIDADE);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                listaComCpfs.add(rSet.getLong("CPF"));
            }
            if (!listaComCpfs.isEmpty()) {
                return listaComCpfs.get(0);
            } else {
                throw new SQLException("Erro ao buscar CPF");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar CPF");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar modelo banda pneu na empresa.
    private Long buscaCodModeloBandaPneuEmpresa() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> codModelosBandaEmpresa = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT MB.CODIGO FROM MODELO_BANDA MB WHERE MB.COD_EMPRESA = ?");
            stmt.setLong(1, COD_EMPRESA);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                codModelosBandaEmpresa.add(rSet.getLong("CODIGO"));
            }
            if (!codModelosBandaEmpresa.isEmpty()) {
                return codModelosBandaEmpresa.get(0);
            } else {
                throw new SQLException("Erro ao buscar código modelo banda do pneu");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar código modelo banda do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar modelo pneu na empresa.
    private Long buscaCodModeloPneuEmpresa() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> codModelosPneuEmpresa = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = ?");
            stmt.setLong(1, COD_EMPRESA);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                codModelosPneuEmpresa.add(rSet.getLong("CODIGO"));
            }
            if (!codModelosPneuEmpresa.isEmpty()) {
                return codModelosPneuEmpresa.get(0);
            } else {
                throw new SQLException("Erro ao buscar código modelo do pneu");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar código modelo do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar um código de dimensão.
    private Long buscaCodDimensao() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> dimensoes = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM DIMENSAO_PNEU;");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                dimensoes.add(rSet.getLong("CODIGO"));
            }
            if (!dimensoes.isEmpty()) {
                return dimensoes.get(0);
            } else {
                throw new SQLException("Erro ao buscar dimensão");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar dimensão");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar modelo do veículo.
    private Long buscaCodModeloVeiculo() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> modelos = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM MODELO_VEICULO WHERE COD_EMPRESA = ?;");
            stmt.setLong(1, COD_EMPRESA);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                modelos.add(rSet.getLong("CODIGO"));
            }
            if (!modelos.isEmpty()) {
                return modelos.get(0);
            } else {
                throw new SQLException("Erro ao buscar modelo veículo");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar modelo veículo");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar tipo veículo.
    private Long buscaCodTipoVeiculo() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> tipos = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM VEICULO_TIPO WHERE COD_EMPRESA = ?;");
            stmt.setLong(1, COD_EMPRESA);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tipos.add(rSet.getLong("CODIGO"));
            }
            if (!tipos.isEmpty()) {
                return tipos.get(0);
            } else {
                throw new SQLException("Erro ao buscar tipos veículo");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar tipos veículo");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar placa disponível na unidade.
    private String buscaPlacaUnidade() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT V.PLACA FROM VEICULO_DATA V WHERE V.COD_UNIDADE = ? " +
                    "AND V.PLACA NOT IN (SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = ?);");
            stmt.setLong(1, COD_UNIDADE);
            stmt.setLong(2, COD_UNIDADE);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                placas.add(rSet.getString("PLACA"));
            }
            if (!placas.isEmpty()) {
                return placas.get(0);
            } else {
                throw new SQLException("Erro ao buscar placa");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar placa");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar posições de uma placa;
    private List<Integer> buscaPosicaoesPlaca(final String placa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Integer> posicoes = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT VDPP.POSICAO_PROLOG FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP " +
                    "WHERE VDPP.COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA FROM VEICULO_TIPO VT " +
                    "WHERE VT.CODIGO = (SELECT V.COD_TIPO FROM VEICULO_DATA V WHERE V.PLACA = ?));");
            stmt.setString(1, placa);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                posicoes.add(rSet.getInt("POSICAO_PROLOG"));
            }
            if (posicoes.isEmpty()) {
                throw new SQLException("Erro ao buscar posições");
            }
            return posicoes;
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar posições");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por desativar sobrescrita do pneu.
    void desativaSobrescritaPneuEmpresa() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("DELETE FROM INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL " +
                    "WHERE COD_EMPRESA = ?");
            stmt.setLong(1, COD_EMPRESA);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao desativar configuração de sobrescrita do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Método responsável por buscar código sistema integrado de um pneu específico para o teste de inserção,
    private Long buscaCodSistemaIntegradoPneuInserido(@NotNull final Long codSistemaIntegrado,
                                                      @NotNull final String codCliente) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT COD_PNEU_SISTEMA_INTEGRADO FROM INTEGRACAO.PNEU_CADASTRADO " +
                    "WHERE COD_PNEU_SISTEMA_INTEGRADO = ? " +
                    "AND COD_EMPRESA_CADASTRO = ? " +
                    "AND COD_UNIDADE_CADASTRO = ? " +
                    "AND  COD_CLIENTE_PNEU_CADASTRO = ? " +
                    "AND TOKEN_AUTENTICACAO_CADASTRO = ?");
            stmt.setLong(1, codSistemaIntegrado);
            stmt.setLong(2, COD_EMPRESA);
            stmt.setLong(3, COD_UNIDADE);
            stmt.setString(4, codCliente);
            stmt.setString(5, TOKEN_INTEGRACAO);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_PNEU_SISTEMA_INTEGRADO");
            } else {
                return null;
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar todas as informações do pneu.
    private ApiPneuCadastro buscaInformacoesPneu(final Long codSistemaIntegrado,
                                                 final String codCliente) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ApiPneuCadastro apiPneuCargaInicial = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE,\n" +
                    "       P.COD_UNIDADE,\n" +
                    "       P.COD_MODELO,\n" +
                    "       P.COD_DIMENSAO,\n" +
                    "       P.PRESSAO_RECOMENDADA,\n" +
                    "       P.VIDA_ATUAL,\n" +
                    "       P.VIDA_TOTAL,\n" +
                    "       P.DOT,\n" +
                    "       P.VALOR,\n" +
                    "       P.PNEU_NOVO_NUNCA_RODADO,\n" +
                    "       P.COD_MODELO_BANDA\n" +
                    "FROM PNEU_DATA P\n" +
                    "WHERE COD_EMPRESA = ?\n" +
                    "  AND COD_UNIDADE = ?\n" +
                    "  AND CODIGO_CLIENTE = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setLong(2, COD_UNIDADE);
            stmt.setString(3, codCliente);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                apiPneuCargaInicial = new ApiPneuCadastro(
                        codSistemaIntegrado,
                        rSet.getString("CODIGO_CLIENTE"),
                        rSet.getLong("COD_UNIDADE"),
                        rSet.getLong("COD_MODELO"),
                        rSet.getLong("COD_DIMENSAO"),
                        rSet.getDouble("PRESSAO_RECOMENDADA"),
                        rSet.getInt("VIDA_ATUAL"),
                        rSet.getInt("VIDA_TOTAL"),
                        rSet.getString("DOT"),
                        rSet.getBigDecimal("VALOR"),
                        rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
                        rSet.getLong("COD_MODELO_BANDA"),
                        new BigDecimal(0)
                );
            }
            return apiPneuCargaInicial;
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar informações do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar dados pneu.
    private ApiPneuCadastro buscaPneuUnidade() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ApiPneuCadastro apiPneuCargaInicial = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE,\n" +
                    "       P.COD_UNIDADE,\n" +
                    "       P.COD_MODELO,\n" +
                    "       P.COD_DIMENSAO,\n" +
                    "       P.PRESSAO_RECOMENDADA,\n" +
                    "       P.VIDA_ATUAL,\n" +
                    "       P.VIDA_TOTAL,\n" +
                    "       P.DOT,\n" +
                    "       P.VALOR,\n" +
                    "       P.PNEU_NOVO_NUNCA_RODADO,\n" +
                    "       P.COD_MODELO_BANDA,\n" +
                    "       IP.COD_PNEU_SISTEMA_INTEGRADO\n" +
                    "FROM PNEU_DATA P JOIN INTEGRACAO.PNEU_CADASTRADO IP ON IP.COD_PNEU_CADASTRO_PROLOG = P.CODIGO " +
                    "WHERE P.COD_EMPRESA = ? AND P.COD_UNIDADE = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setLong(2, COD_UNIDADE);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                apiPneuCargaInicial = new ApiPneuCadastro(
                        rSet.getLong("COD_PNEU_SISTEMA_INTEGRADO"),
                        rSet.getString("CODIGO_CLIENTE"),
                        rSet.getLong("COD_UNIDADE"),
                        rSet.getLong("COD_MODELO"),
                        rSet.getLong("COD_DIMENSAO"),
                        rSet.getDouble("PRESSAO_RECOMENDADA"),
                        rSet.getInt("VIDA_ATUAL"),
                        rSet.getInt("VIDA_TOTAL"),
                        rSet.getString("DOT"),
                        rSet.getBigDecimal("VALOR"),
                        rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
                        rSet.getLong("COD_MODELO_BANDA"),
                        new BigDecimal(0)
                );
            }
            return apiPneuCargaInicial;
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar informações do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar todas as informações do pneu na carga inicial.
    private ApiPneuCargaInicial buscaInformacoesPneuCargaInicialEstoque(final Long codSistemaIntegrado,
                                                                        final String codCliente)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ApiPneuCargaInicial apiPneuCargaInicial = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE,\n" +
                    "       P.COD_UNIDADE,\n" +
                    "       P.COD_MODELO,\n" +
                    "       P.COD_DIMENSAO,\n" +
                    "       P.PRESSAO_RECOMENDADA,\n" +
                    "       P.VIDA_ATUAL,\n" +
                    "       P.VIDA_TOTAL,\n" +
                    "       P.DOT,\n" +
                    "       P.VALOR,\n" +
                    "       P.PNEU_NOVO_NUNCA_RODADO,\n" +
                    "       P.COD_MODELO_BANDA,\n" +
                    "       P.STATUS\n" +
                    "FROM PNEU_DATA P\n" +
                    "WHERE COD_EMPRESA = ?\n" +
                    "  AND COD_UNIDADE = ?\n" +
                    "  AND CODIGO_CLIENTE = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setLong(2, COD_UNIDADE);
            stmt.setString(3, codCliente);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                apiPneuCargaInicial = new ApiPneuCargaInicial(
                        codSistemaIntegrado,
                        rSet.getString("CODIGO_CLIENTE"),
                        rSet.getLong("COD_UNIDADE"),
                        rSet.getLong("COD_MODELO"),
                        rSet.getLong("COD_DIMENSAO"),
                        rSet.getDouble("PRESSAO_RECOMENDADA"),
                        rSet.getInt("VIDA_ATUAL"),
                        rSet.getInt("VIDA_TOTAL"),
                        rSet.getString("DOT"),
                        new BigDecimal(0),
                        rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
                        rSet.getLong("COD_MODELO_BANDA"),
                        new BigDecimal(0),
                        ApiStatusPneu.ESTOQUE,
                        null,
                        900
                );
            }
            return apiPneuCargaInicial;
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar informações do pneu para carga inicial");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por pegar todas as informações do pneu na carga inicial.
    private ApiPneuCargaInicial buscaInformacoesPneuCargaInicialEmUso(final Long codSistemaIntegrado,
                                                                      final String codCliente)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        ApiPneuCargaInicial apiPneuCargaInicial = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT P.CODIGO_CLIENTE,\n" +
                    "       P.COD_UNIDADE,\n" +
                    "       P.COD_MODELO,\n" +
                    "       P.COD_DIMENSAO,\n" +
                    "       P.PRESSAO_RECOMENDADA,\n" +
                    "       P.VIDA_ATUAL,\n" +
                    "       P.VIDA_TOTAL,\n" +
                    "       P.DOT,\n" +
                    "       P.VALOR,\n" +
                    "       P.PNEU_NOVO_NUNCA_RODADO,\n" +
                    "       P.COD_MODELO_BANDA,\n" +
                    "       P.STATUS,\n" +
                    "       VP.PLACA,\n" +
                    "       VP.POSICAO\n" +
                    "FROM PNEU_DATA P  JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU\n" +
                    "WHERE P.COD_EMPRESA = ?\n" +
                    "  AND P.COD_UNIDADE = ?\n" +
                    "  AND P.CODIGO_CLIENTE = ?;");
            stmt.setLong(1, COD_EMPRESA);
            stmt.setLong(2, COD_UNIDADE);
            stmt.setString(3, codCliente);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                apiPneuCargaInicial = new ApiPneuCargaInicial(
                        codSistemaIntegrado,
                        rSet.getString("CODIGO_CLIENTE"),
                        rSet.getLong("COD_UNIDADE"),
                        rSet.getLong("COD_MODELO"),
                        rSet.getLong("COD_DIMENSAO"),
                        rSet.getDouble("PRESSAO_RECOMENDADA"),
                        rSet.getInt("VIDA_ATUAL"),
                        rSet.getInt("VIDA_TOTAL"),
                        rSet.getString("DOT"),
                        new BigDecimal(0),
                        rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
                        rSet.getLong("COD_MODELO_BANDA"),
                        new BigDecimal(0),
                        ApiStatusPneu.EM_USO,
                        rSet.getString("PLACA"),
                        rSet.getInt("POSICAO")
                );
            }
            return apiPneuCargaInicial;
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar informações do pneu para carga inicial");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar vida atual do pneu cadastrado no prolog.
    private int buscaVidaAtualPneuAtualizado(final Long codSistemaIntegrado,
                                             final String codCliente) throws Throwable {
        Long codPneuCadastroProlog = buscaCodPneuCadastroProlog(codSistemaIntegrado, codCliente);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT VIDA_ATUAL FROM PNEU WHERE " +
                    "CODIGO = ? AND " +
                    "CODIGO_CLIENTE = ? AND " +
                    "COD_UNIDADE = ?");
            stmt.setLong(1, codPneuCadastroProlog);
            stmt.setString(2, codCliente);
            stmt.setLong(3, COD_UNIDADE);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("VIDA_ATUAL");
            } else {
                throw new SQLException("Erro ao buscar vida atual do pneu");
            }
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por buscar código do pneu cadastrado no prolog.
    private Long buscaCodPneuCadastroProlog(final Long codSistemaIntegrado,
                                            final String codCliente) throws Throwable {
        Long codPneuCadastroProlog = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT COD_PNEU_CADASTRO_PROLOG FROM INTEGRACAO.PNEU_CADASTRADO WHERE " +
                    "COD_UNIDADE_CADASTRO = ? AND " +
                    "COD_EMPRESA_CADASTRO = ? AND " +
                    "COD_CLIENTE_PNEU_CADASTRO = ? AND " +
                    "COD_PNEU_SISTEMA_INTEGRADO = ?");
            stmt.setLong(1, COD_UNIDADE);
            stmt.setLong(2, COD_EMPRESA);
            stmt.setString(3, codCliente);
            stmt.setLong(4, codSistemaIntegrado);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                codPneuCadastroProlog = rSet.getLong("COD_PNEU_CADASTRO_PROLOG");
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao buscar código pneu Prolog");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
        return codPneuCadastroProlog;
    }

    //Método responsável por verificar se pneu foi atualizado.
    private boolean verificaSePneuFoiAtualizado(final Long codPneuProlog,
                                                final String status) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT * FROM PNEU WHERE CODIGO = ? AND STATUS = ?");
            stmt.setLong(1, codPneuProlog);
            stmt.setString(2, status);
            rSet = stmt.executeQuery();
            return rSet.next();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao verificar pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Objetos Pneu para testes em Carga Inicial sem erro.
    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComCodigoClienteValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("500.00"),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComUnidadeValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComModeloPneuValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComDimensaoValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComPressaoValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComVidaAtualValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComDotValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComModeloDeBandaValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                11L,
                new BigDecimal("100.00"),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuComPosicoesEspecificas(final int posicao,
                                                               final String placa) throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                2,
                4,
                "1010",
                new BigDecimal("1500.0"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"),
                ApiStatusPneu.EM_USO,
                placa,
                posicao);
    }

    //Objeto Pneu preenchido para testes sem erro.
    @NotNull
    private ApiPneuCadastro criaPneuParaInsertSemErro() throws Throwable {
        return new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(),
                buscaCodDimensao(),
                120.0,
                3,
                4,
                "1010",
                new BigDecimal("1000.00"),
                false,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("100.00"));
    }

    //Objetos Pneu para testes na atualização do Status de um pneu sem erro
    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusAnaliseSemErro(ApiPneuCadastro apiPneuCadastro) {
        return new ApiPneuAlteracaoStatusAnalise(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null);
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusDescarteSemErro(ApiPneuCadastro apiPneuCadastro)
            throws Throwable {
        return new ApiPneuAlteracaoStatusDescarte(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("400.00"));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEstoqueSemErro(ApiPneuCadastro apiPneuCadastro)
            throws Throwable {
        return new ApiPneuAlteracaoStatusEstoque(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("120.00"));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEmUsoSemErro(ApiPneuCadastro apiPneuCadastro)
            throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);
        //Verifica se veículo foi inserido.
        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        //Busca posições do veículo.
        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());

        //Cria Objeto.
        return new ApiPneuAlteracaoStatusVeiculo(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "03383283194",
                Now.localDateTimeUtc(),
                veiculoCadastroPraxio.getPlacaVeiculo(),
                posicoesPlaca.get(0),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("300.00"));
    }

    //Objeto Pneu para atualizar STATUS em carga inicial
    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEstoqueSemErroCargaInicial(
            ApiPneuCargaInicial apiPneuCargaInicial) {
        return new ApiPneuAlteracaoStatusEstoque(
                apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                apiPneuCargaInicial.getCodigoCliente(),
                apiPneuCargaInicial.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                false,
                null,
                null);
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusDescarteSemErroCargaInicial(
            ApiPneuCargaInicial apiPneuCargaInicial) throws Throwable {
        return new ApiPneuAlteracaoStatusDescarte(
                apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                apiPneuCargaInicial.getCodigoCliente(),
                apiPneuCargaInicial.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("400.00"));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusAnaliseSemErroCargaInicial(
            ApiPneuCargaInicial apiPneuCargaInicial) throws Throwable {
        return new ApiPneuAlteracaoStatusAnalise(
                apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                apiPneuCargaInicial.getCodigoCliente(),
                apiPneuCargaInicial.getCodUnidadePneu(),
                "03383283194",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("400.00"));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaTrocarPosicaoPneuEmUso(ApiPneuCargaInicial apiPneuCargaInicial,
                                                                      int posicao) throws Throwable {
        assert apiPneuCargaInicial.getPlacaVeiculoPneuAplicado() != null;
        return new ApiPneuAlteracaoStatusVeiculo(
                apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                apiPneuCargaInicial.getCodigoCliente(),
                apiPneuCargaInicial.getCodUnidadePneu(),
                "03383283194",
                Now.localDateTimeUtc(),
                apiPneuCargaInicial.getPlacaVeiculoPneuAplicado(),
                posicao,
                true,
                buscaCodModeloBandaPneuEmpresa(),
                new BigDecimal("300.00"));
    }

    //Método responsável por criar um novo veículo para cadastrar.
    private VeiculoCadastroPraxio criaVeiculoParaCadastro() throws Throwable {
        return new VeiculoCadastroPraxio(
                COD_UNIDADE,
                "PLA" + RANDOM.nextInt(9999),
                1000L,
                buscaCodModeloVeiculo(),
                buscaCodTipoVeiculo()
        );
    }
}