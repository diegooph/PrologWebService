package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.ApiPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCadastro;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicial;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicialResponse;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 25/02/2020
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class PneuCrudApiTest extends BaseTest {
    @NotNull
    private static final String TOKEN_INTEGRACAO = "NATAN";
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static String PLACA = "MMM0001";
    @NotNull
    private static final Random RANDOM = new Random();
    private ApiCadastroPneuService apiCadastroPneuService;
    private ApiPneuService apiPneuService;
    private DatabaseConnectionProvider connectionProvider;

    @BeforeAll
    public void initialize() {
        DatabaseManager.init();
        this.apiCadastroPneuService = new ApiCadastroPneuService();
        this.apiPneuService = new ApiPneuService();
        this.connectionProvider = new DatabaseConnectionProvider();
        // TODO - inserir Token e CHAVES necessárias nas tabelas de integração.
        // TODO - Deixar os objetos ainda mais automatizados criando modelos de pneu e banda a cada vez que rodar o teste
        // TODO - Utilizar os código (modelos de pneu e banda) para criar os pneus
    }

    @AfterAll
    public void destroy() {
        // TODO - remover Token das tabelas de integração.
        DatabaseManager.finish();
    }

    @NotNull
    private Long geraValorAleatorio() {
        return RANDOM.nextLong();
    }

    @Test
    @DisplayName("Teste Inserção Carga Inicial de Pneus sem erros")
    void adicionaCargaInicia3lPneuSemErroTest() throws Throwable {
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
        cargaInicial.add(criaPneuSemErroComPlacaPneuValida());
        cargaInicial.add(criaPneuSemErroComPosicaoPneuValida());

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verifica se os pneus foram inseridos
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicial(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente(),
                    apiPneuCargaInicial.getCodUnidadePneu(),
                    COD_EMPRESA);
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
        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (ApiPneuCargaInicialResponse apiPneuCargaInicialRespons : apiPneuCargaInicialResponses) {
            assertThat(apiPneuCargaInicialRespons.getSucesso()).isTrue();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código da unidade inválido")
    void adicionaCargaInicialPneuComErroCodUnidadeNaoExisteTest() {
        //Cenário
        final Long codUnidade = 909090L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                codUnidade,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
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
    void adicionaCargaInicialPneuComErroCodModeloPneuNaoExisteTest() {
        //Cenário
        final Long codModeloPneu = 5754343L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                codModeloPneu,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
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
    void adicionaCargaInicialPneuComErroCodDimensaoNaoExisteTest() {
        //Cenário
        final Long codDimensao = 4543235L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                codDimensao,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
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
    void adicionaCargaInicialPneuComErroPressaoIncorretaTest() {
        //Cenário
        final Double codPressao = -120.0;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                codPressao,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
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
    void adicionaCargaInicialPneuComErroValorPneuInvalidoTest() {
        //Cenário
        final BigDecimal valorPneu = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
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
    void adicionaCargaInicialPneuComErroVidaAtualMaiorQueTotalTest() {
        //Cenário
        final int vidaAtual = 5;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                vidaAtual,
                4,
                "1010",
                new BigDecimal(1500.0),
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
    void adicionaCargaInicialPneuComErroModeloBandaInvalidoTest() {
        //Cenário
        final Long modeloBanda = -1L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                3,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                modeloBanda,
                new BigDecimal(400.00),
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
    void adicionaCargaInicialPneuComErroValorBandaInvalidoTest() {
        //Cenário
        final BigDecimal valorBanda = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                3,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                12L,
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
    void adicionaCargaInicialPneuComErroPlacaPneuNaoExisteTest() {
        //Cenário
        final String placaVeiculo = "OOO9891";
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                7645L,
                new BigDecimal(400.00),
                ApiStatusPneu.EM_USO,
                placaVeiculo,
                111));

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
    void adicionaCargaInicialPneuComErroPosicaoPneuInvalidaTest() {
        //Cenário
        final int posicaoPneu = 7777;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                7645L,
                new BigDecimal(400.00),
                ApiStatusPneu.EM_USO,
                "PRO0004",
                posicaoPneu));

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
    @DisplayName("Teste Carga Inicial de um pneu existente no banco com vida atual = 3 sendo sobrescrito para vida atual = 1")
    void sobrescrevePneuJaCadastradoComVidaMenorQueAtualCargaInicialSemErroTest() throws Throwable {
        //Ativa configuração da empresa
        ativaSobrescritaPneuEmpresa(COD_EMPRESA);
        int vidaAtualPneu = 1;

        //Cenário específico da PLI-4 (Erro ao sobrescrever pneus que voltam para vida 1);
        //Cria pneu com vida atual = 3;
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();

        //Execução: Adiciona pneu;
        final SuccessResponseIntegracao successResponseIntegracao =
                apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Valida se pneu foi inserido;
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuInserido = buscaInformacoesPneuCargaInicial(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                COD_EMPRESA);

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
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuAtualizado = buscaInformacoesPneuCargaInicial(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                COD_EMPRESA);

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
        desativaSobrescritaPneuEmpresa(COD_EMPRESA);

        //Verificações
        final int vidaAtualPneuAtualizado =
                buscaVidaAtualPneuAtualizado(
                        apiPneuCadastro.getCodigoSistemaIntegrado(),
                        apiPneuCadastro.getCodigoCliente(),
                        apiPneuCadastro.getCodUnidadePneu(),
                        COD_EMPRESA);
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
                        apiPneuCadastro.getCodigoCliente(),
                        apiPneuCadastro.getCodUnidadePneu(),
                        COD_EMPRESA,
                        TOKEN_INTEGRACAO);
        final ApiPneuCadastro apiPneuCadastroInfoPneu = buscaInformacoesPneu(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                COD_EMPRESA);
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
        assertThat(apiPneuCadastroInfoPneu.getValorPneu()).isEqualTo(apiPneuCadastro.getValorPneu());
        assertThat(apiPneuCadastroInfoPneu.getPneuNovoNuncaRodado()).isEqualTo(apiPneuCadastro.
                getPneuNovoNuncaRodado());
        assertThat(apiPneuCadastroInfoPneu.getCodModeloPneu()).isEqualTo(apiPneuCadastro.getCodModeloPneu());
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com unidade inválida")
    void adicionaPneuComErroUnidadeInvalidaTest() {
        final Long codUnidade = 11153423L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                codUnidade,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

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
    void adicionaPneuComErroCodModeloPneuInvalidoTest() {
        final Long codModelo = 909090L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                codModelo,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

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
    void adicionaPneuComErroCodModeloBandaInvalidoTest() {
        final Long codModeloBanda = -1L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                2,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                codModeloBanda,
                new BigDecimal(100.00));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O modelo da banda " + codModeloBanda + " do pneu não está mapeado no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com código dimensão inválido")
    void adicionaPneuComErroCodDimensaoInvalidoTest() {
        final Long codDimensao = 9999999L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                codDimensao,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiCadastroPneuService().inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("A dimensão de código " + codDimensao + " do pneu não está mapeada no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu com pressão inválida")
    void adicionaPneuComErroPressaoInvalidaTest() {
        //Cenário
        final Double pressaoPneu = -120.0;
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                pressaoPneu,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

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
    void adicionaPneuComErroVidaAtualInvalidaTest() {
        //Cenário
        final int vidaAtual = 5;
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                vidaAtual,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

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
    void adicionaPneuComErroVidaTotalInvalidaTest() {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                5,
                1,
                "1010",
                new BigDecimal(1000.00),
                true,
                12L,
                new BigDecimal(100.00));

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
    void adicionaPneuComErroValorPneuInvalidoTest() {
        //Cenário
        final BigDecimal valor = new BigDecimal(-1.00);
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
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
    void adicionaPneuComErroValorBandaInvalidoTest() {
        //Cenário
        final BigDecimal valor = new BigDecimal(-1.00);
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                2,
                4,
                "1010",
                new BigDecimal(100.00),
                false,
                12L,
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
                            apiPneuCadastro.getCodigoCliente(),
                            apiPneuCadastro.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
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
                            pneuAlteracaoStatus.getCodigoCliente(),
                            pneuAlteracaoStatus.getCodUnidadePneu(),
                            COD_EMPRESA);
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
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade( 5L, COD_EMPRESA);
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                codSistemaIntegrado,
                apiPneuCadastro.getCodigoCliente(),
                5L,
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
                .isEqualTo("O pneu de código interno " + codSistemaIntegrado + " não está mapeado no Sistema ProLog");
    }

    @Test
    @DisplayName("Teste atualiza status do pneu com código unidade inválido")
    void atualizaStatusPneuComErroCodigoUnidadeInvalidoTest() throws Throwable {
        final Long codUnidade = 115431234L;
        //Busca Pneu
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade( 5L, COD_EMPRESA);
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                codUnidade,
                "12345678910",
                LocalDateTime.now(),
                true,
                11L,
                new BigDecimal(69.00)));

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
        ApiPneuCadastro apiPneuCadastro = buscaPneuUnidade( 5L, COD_EMPRESA);
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
                new BigDecimal(69.00)));
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
    //Configuração de sobrescrita de uma empresa.
    void ativaSobrescritaPneuEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL(" +
                    "COD_EMPRESA, " +
                    "SOBRESCREVE_PNEUS, " +
                    "SOBRESCREVE_VEICULOS) " +
                    "VALUES(?,?,?) ON CONFLICT(COD_EMPRESA) DO NOTHING");
            stmt.setLong(1, codEmpresa);
            stmt.setBoolean(2, true);
            stmt.setBoolean(3, false);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao ativar configuração de sobrescrita do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    void desativaSobrescritaPneuEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("DELETE FROM INTEGRACAO.EMPRESA_CONFIG_CARGA_INICIAL " +
                    "WHERE COD_EMPRESA = ?");
            stmt.setLong(1, codEmpresa);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao desativar configuração de sobrescrita do pneu");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Método responsável por buscar código sistema integrado de um pneu específico para o teste de inserção,
    private Long buscaCodSistemaIntegradoPneuInserido(@NotNull final Long codSistemaIntegrado,
                                                      @NotNull final String codCliente,
                                                      @NotNull final Long codUnidade,
                                                      @NotNull final Long codEmpresa,
                                                      @NotNull final String token) throws Throwable {
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
            stmt.setLong(2, codEmpresa);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, codCliente);
            stmt.setString(5, token);
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
    private ApiPneuCadastro buscaInformacoesPneu(@NotNull final Long codSistemaIntegrado,
                                                 @NotNull final String codCliente,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Long codEmpresa) throws Throwable {
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
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
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
    private ApiPneuCadastro buscaPneuUnidade(@NotNull final Long codUnidade,
                                             @NotNull Long codEmpresa) throws Throwable {
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
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
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
    private ApiPneuCargaInicial buscaInformacoesPneuCargaInicial(@NotNull final Long codSistemaIntegrado,
                                                                 @NotNull final String codCliente,
                                                                 @NotNull final Long codUnidade,
                                                                 @NotNull final Long codEmpresa) throws Throwable {
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
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
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

    //Método responsável por buscar vida atual do pneu cadastrado no prolog.
    private int buscaVidaAtualPneuAtualizado(@NotNull final Long codSistemaIntegrado,
                                             @NotNull final String codCliente,
                                             @NotNull final Long codUnidade,
                                             @NotNull final Long codEmpresa) throws Throwable {
        Long codPneuCadastroProlog = buscaCodPneuCadastroProlog(codSistemaIntegrado, codCliente, codUnidade, codEmpresa);
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
            stmt.setLong(3, codUnidade);
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
    private Long buscaCodPneuCadastroProlog(@NotNull final Long codSistemaIntegrado,
                                            @NotNull final String codCliente,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codEmpresa) throws Throwable {
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
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codEmpresa);
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
    private boolean verificaSePneuFoiAtualizado(@NotNull final Long codPneuProlog,
                                                @NotNull final String status) throws Throwable {
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

    //Método responsável por remover um pneu de uma placa em uma posicao específica.
    private void removePneuDeUmaPlacaPosicaoEspecifica(@NotNull final String placa,
                                                       final int posicao,
                                                       @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("DELETE FROM VEICULO_PNEU WHERE " +
                    "PLACA = ? AND " +
                    "POSICAO = ? AND " +
                    "COD_UNIDADE = ?;");
            stmt.setString(1, placa);
            stmt.setInt(2, posicao);
            stmt.setLong(3, codUnidade);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao deletar pneus da placa");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Objetos Pneu para testes em Carga Inicial sem erro.
    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComCodigoClienteValido() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                11L,
                new BigDecimal(500),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComUnidadeValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComModeloPneuValido() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComDimensaoValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComPressaoValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComVidaAtualValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComDotValido() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComModeloDeBandaValido() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                11L,
                new BigDecimal(100.00),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComPlacaPneuValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                11L,
                new BigDecimal(100.00),
                ApiStatusPneu.ESTOQUE,
                "LLL1234",
                904);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComPosicaoPneuValida() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                11L,
                new BigDecimal(100.00),
                ApiStatusPneu.ESTOQUE,
                "LLL1234",
                904);
    }

    //Objeto Pneu preenchido para testes sem erro.
    @NotNull
    private ApiPneuCadastro criaPneuParaInsertSemErro() {
        return new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                3,
                4,
                "1010",
                new BigDecimal(1000.00),
                false,
                12L,
                new BigDecimal(100.00));
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
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusDescarteSemErro(ApiPneuCadastro apiPneuCadastro) {
        return new ApiPneuAlteracaoStatusDescarte(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                11L,
                new BigDecimal(400.00));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEstoqueSemErro(ApiPneuCadastro apiPneuCadastro) {
        return new ApiPneuAlteracaoStatusEstoque(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                12L,
                new BigDecimal(120.00));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEmUsoSemErro(ApiPneuCadastro apiPneuCadastro) throws Throwable {
        int posicao = 900;
        removePneuDeUmaPlacaPosicaoEspecifica(PLACA, posicao, 5L);
        return new ApiPneuAlteracaoStatusVeiculo(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "03383283194",
                Now.localDateTimeUtc(),
                PLACA,
                posicao,
                true,
                11L,
                new BigDecimal(300.00));
    }
}