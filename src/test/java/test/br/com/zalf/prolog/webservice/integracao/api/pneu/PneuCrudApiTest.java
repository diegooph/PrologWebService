package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.ApiPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCadastro;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicial;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicialResponse;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatusAnalise;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatusDescarte;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;
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
    private static final String TOKEN_INTEGRACAO = "NATAN";
    @NotNull
    private static final Random RANDOM = new Random();
    private ApiCadastroPneuService apiCadastroPneuService;
    private ApiPneuService apiPneuService;

    @BeforeAll
    public void initialize() {
        DatabaseManager.init();
        this.apiCadastroPneuService = new ApiCadastroPneuService();
        this.apiPneuService = new ApiPneuService();
        // TODO - inserir Token e CHAVES necessárias nas tabelas de integração.
    }

    @AfterAll
    public void destroy() {
        // TODO - remover Token das tabelas de integração.
        DatabaseManager.finish();
    }

    @Test
    @DisplayName("Teste Inserção Carga Inicial de Pneus sem erros")
    void adicionaCargaInicialPneuSemErroTest() {
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

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isTrue();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código da unidade inválido")
    void adicionaCargaInicialPneuComErroCodUnidadeNaoExisteTest() {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                909090L,
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
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código do modelo do pneu inválido")
    void adicionaCargaInicialPneuComErroCodModeloPneuNaoExisteTest() {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                5754343L,
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
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com código dimensão inválido")
    void adicionaCargaInicialPneuComErroCodDimensaoNaoExisteTest() {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                4543235L,
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
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com pressão inválida")
    void adicionaCargaInicialPneuComErroPressaoIncorretaTest() {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                -120.0,
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
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com vida atual do pneu maior que a vida total")
    void adicionaCargaInicialPneuComErroVidaAtualMaiorQueTotalTest() {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                5,
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
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com placa inválida")
    void adicionaCargaInicialPneuComErroPlacaPneuNaoExisteTest() {
        //Cenário
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
                "OOO9891",
                111));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial com posição do pneu em relação ao veículo inválida")
    void adicionaCargaInicialPneuComErroPosicaoPneuInvalidaTest() {
        //Cenário
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
                // TODO - Melhorar o cenário da placa.
                " PRECISA-SE DE UMA PLACA",
                7777));

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Teste Carga Inicial de um pneu existente no banco com vida atual = 3 sendo sobrescrito para vida atual = 1")
    void sobrescrevePneuJaCadastradoComVidaMenorQueAtualCargaInicialSemErroTest() {
        // TODO - Ativar a sobrescrita de pneus.
        //Cenário específico da PLI-4 (Erro ao sobrescrever pneus que voltam para vida 1);
        //Cria pneu com vida atual = 3;
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();

        //Execução: Adiciona pneu;
        final SuccessResponseIntegracao successResponseIntegracao =
                apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Verificações: Valida inserção;
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getCodigo()).isEqualTo(200L);
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();

        //Usa pneu já inserido para carga inicial, mas o pneu agora passa a ter vida atual = 1;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                apiPneuCadastro.getCodModeloPneu(),
                apiPneuCadastro.getCodDimensaoPneu(),
                apiPneuCadastro.getPressaoCorretaPneu(),
                1,
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

        // TODO - desativar a sobrescrita de pneus.

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());

        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isTrue();
        }
    }

    @Test
    @DisplayName("Teste Inserção de um novo Pneu sem erro")
    void adicionaPneuSemErroTest() {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();

        //Execução
        final SuccessResponseIntegracao successResponseIntegracao =
                apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getCodigo()).isEqualTo(200L);
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
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
    void adicionaPneuComErroCodModeloPneuInvalido() {
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
    @DisplayName("Teste Inserção de um novo Pneu com código dimensão inválido")
    void adicionaPneuComErroCodDimensaoInvalido() {
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
    void adicionaPneuComErroPressaoInvalida() {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                -120.0,
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
    void adicionaPneuComErroVidaAtualInvalida() {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                5,
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
    void adicionaPneuComErroVidaTotalInvalida() {
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
    @DisplayName("Teste Atualiza status do pneu sem erros")
    void atualizaStatusPneuSemErroTest() {
        //Cenário
        // TODO - Colocar todos os status aqui EM_USO, ESTOQUE, ANALISE, DESCARTE.
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusAnaliseSemErro());
        apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusDescarteSemErro());

        //Excecução
        final SuccessResponseIntegracao successResponseIntegracao =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getCodigo()).isEqualTo(200L);
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
    }

    @Test
    @DisplayName("Teste Atualiza status do pneu com erro no código sistema integrado")
    void atualizaStatusPneuComErroCodSistemaIntegradoTest() {
        final Long codSistemaIntegrado = 611772312L;
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                codSistemaIntegrado,
                "71157",
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
    void atualizaStatusPneuComCodigoUnidadeInvalido() {
        final Long codUnidade = 115431234L;
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                94617L,
                "71157",
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
                .isEqualTo("A Unidade " + codUnidade + " não está configurada para esta empresa");
    }

    @Test
    @DisplayName("Teste atualiza status do pneu com código modelo de banda inválido")
    void atualizaStatusPneuComCodigoModeloBandaInvalido() {
        final Long codModeloPneu = 10908787L;
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                94617L,
                "71157",
                5L,
                "12345678910",
                LocalDateTime.now(),
                true,
                codModeloPneu,
                new BigDecimal(69.00)));

        //Excecução
        final Throwable throwable = assertThrows(
                ProLogException.class,
                () -> new ApiPneuService().atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus));

        //Verificações
        assertThat(throwable.getMessage())
                .isEqualTo("O modelo da banda do pneu " + codModeloPneu + " não está mapeado no Sistema ProLog");
    }

    @NotNull
    private Long geraValorAleatorio() {
        return RANDOM.nextLong();
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
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusAnaliseSemErro() {
        return new ApiPneuAlteracaoStatusAnalise(
                13218L,
                "95687",
                5L,
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null);
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusDescarteSemErro() {
        return new ApiPneuAlteracaoStatusDescarte(
                94617L,
                "71157",
                5L,
                "12345678910",
                LocalDateTime.now(),
                true,
                11L,
                new BigDecimal(69.00));
    }
}