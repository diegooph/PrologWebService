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
    private String TOKEN_INTEGRACAO;
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static final Long COD_UNIDADE = 5L;
    @NotNull
    private static String PLACA = "MMM0001";
    @NotNull
    private static final Random RANDOM = new Random();
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
            TOKEN_INTEGRACAO = criaTokenIntegracaoParaEmpresa(COD_EMPRESA, geraTokenIntegracao());
        } catch (final Throwable throwable) {
            System.out.println(throwable);
        }
    }

    @AfterAll
    public void destroy() {
        try {
            removeTokenIntegracaoCriado(COD_EMPRESA, TOKEN_INTEGRACAO);
            DatabaseManager.finish();
        } catch (final Throwable throwable) {
            System.out.println(throwable);
        }
    }

    private String geraTokenIntegracao(){
        return "TOKEN" + RANDOM.nextInt(999999);
    }

    private Long geraCodSistemaIntegrado() {
        return RANDOM.nextLong();
    }

    private String geraCodCliente() {
        return "PN" + RANDOM.nextInt(999999);
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

        //Verifica se os pneus foram inseridos
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEstoque(
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
        for(int i = 0; i < posicoesPlaca.size(); i++){
            //Cria pneu com as posições.
            cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(i),
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for(ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses){
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente(),
                    apiPneuCargaInicial.getCodUnidadePneu(),
                    COD_EMPRESA);
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
    @DisplayName("Teste Carga Inicial adicionando pneus em posições do veículo logo após movendo todos eles para estoque")
    void adicionaCargaInicialPneuEmVeiculoDepoisAtualizaTodosPneusParaEstoque() throws Throwable {
        //Cria veículo.
        VeiculoCadastroPraxio veiculoCadastroPraxio = criaVeiculoParaCadastro();

        //Adiciona veículo.
        final SuccessResponseIntegracao successResponseIntegracao = integracaoPraxioResource.
                inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastroPraxio);

        assertThat(successResponseIntegracao.getMsg()).isEqualTo("Veículo inserido no ProLog com sucesso");

        final List<Integer> posicoesPlaca = buscaPosicaoesPlaca(veiculoCadastroPraxio.getPlacaVeiculo());
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        for(int i = 0; i < posicoesPlaca.size(); i++){
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(i),
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for(ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses){
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente(),
                    apiPneuCargaInicial.getCodUnidadePneu(),
                    COD_EMPRESA);
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
        for(int i = 0; i < cargaInicial.size(); i++){
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusEstoqueSemErroCargaInicial(cargaInicial.get(i)));
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
        for(int i = 0; i < posicoesPlaca.size(); i++){
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(i),
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for(ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses){
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente(),
                    apiPneuCargaInicial.getCodUnidadePneu(),
                    COD_EMPRESA);
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
        for(int i = 0; i < cargaInicial.size(); i++){
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusDescarteSemErroCargaInicial(cargaInicial.get(i)));
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
        for(int i = 0; i < posicoesPlaca.size(); i++){
            //Cria pneu com as posições
            cargaInicial.add(criaPneuComPosicoesEspecificas(posicoesPlaca.get(i),
                    veiculoCadastroPraxio.getPlacaVeiculo()));
        }
        //Execução.
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Valida se todos pneus foram salvos com sucesso.
        for(ApiPneuCargaInicialResponse apiPneuCargaInicialResponse : apiPneuCargaInicialResponses){
            assertThat(apiPneuCargaInicialResponse.getMensagem()).isEqualTo("Pneu cadastrado com sucesso no " +
                    "Sistema ProLog");
        }

        //Valida informações dos pneus salvos.
        //Verifica se os pneus foram inseridos com as informações corretas.
        for (ApiPneuCargaInicial apiPneuCargaInicial : cargaInicial) {
            final Long codSistemaIntegradoPneu =
                    buscaCodSistemaIntegradoPneuInserido(
                            apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                            apiPneuCargaInicial.getCodigoCliente(),
                            apiPneuCargaInicial.getCodUnidadePneu(),
                            COD_EMPRESA,
                            TOKEN_INTEGRACAO);
            final ApiPneuCargaInicial apiPneuCargaInicialInfoPneu = buscaInformacoesPneuCargaInicialEmUso(
                    apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                    apiPneuCargaInicial.getCodigoCliente(),
                    apiPneuCargaInicial.getCodUnidadePneu(),
                    COD_EMPRESA);
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
        for(int i = 0; i < cargaInicial.size(); i++){
            apiPneuAlteracaoStatus.add(criaPneuParaAtualizarStatusAnaliseSemErroCargaInicial(cargaInicial.get(i)));
        }

        //Excecução (Atualiza os pneu para descarte).
        final SuccessResponseIntegracao successResponseIntegracaoPneusAtualizados =
                apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

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
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    void adicionaCargaInicialPneuComErroCodDimensaoNaoExisteTest() throws Throwable {
        //Cenário
        final Long codDimensao = 4543235L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
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
    void adicionaCargaInicialPneuComErroPressaoIncorretaTest() throws Throwable {
        //Cenário
        final Double codPressao = -120.0;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    void adicionaCargaInicialPneuComErroValorPneuInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valorPneu = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
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
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    void adicionaCargaInicialPneuComErroModeloBandaInvalidoTest() throws Throwable {
        //Cenário
        final Long modeloBanda = -1L;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    void adicionaCargaInicialPneuComErroValorBandaInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valorBanda = new BigDecimal(-1);
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                3,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
        final String placaVeiculo = "OOO9891";
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    void adicionaCargaInicialPneuComErroPosicaoPneuInvalidaTest() throws Throwable {
        //Cenário
        final int posicaoPneu = 7777;
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    @DisplayName("Teste Carga Inicial de um pneu existente no banco com vida atual = 3 sendo sobrescrito para vida " +
            "atual = 1")
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
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuInserido = buscaInformacoesPneuCargaInicialEstoque(
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
        final ApiPneuCargaInicial apiPneuCargaInicialInfoPneuAtualizado = buscaInformacoesPneuCargaInicialEstoque(
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
    void adicionaPneuComErroUnidadeInvalidaTest() throws Throwable {
        final Long codUnidade = 11153423L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                codUnidade,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    void adicionaPneuComErroCodModeloBandaInvalidoTest() throws Throwable {
        final Long codModeloBanda = -1L;
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                codDimensao,
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(100.00));

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
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                pressaoPneu,
                1,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    void adicionaPneuComErroVidaAtualInvalidaTest() throws Throwable {
        //Cenário
        final int vidaAtual = 5;
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                vidaAtual,
                4,
                "1010",
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    void adicionaPneuComErroVidaTotalInvalidaTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                5,
                1,
                "1010",
                new BigDecimal(1000.00),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    void adicionaPneuComErroValorPneuInvalidoTest() throws Throwable {
        //Cenário
        final BigDecimal valor = new BigDecimal(-1.00);
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
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
        final BigDecimal valor = new BigDecimal(-1.00);
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                2,
                4,
                "1010",
                new BigDecimal(100.00),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    //Método responśavel por criar TOKEN de autenticação.
    private String criaTokenIntegracaoParaEmpresa(@NotNull Long codEmpresa,
                                                  @NotNull String token) throws Throwable{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.TOKEN_INTEGRACAO(COD_EMPRESA, TOKEN_INTEGRACAO) " +
                    "VALUES (?, ?) ON CONFLICT (COD_EMPRESA) DO UPDATE SET TOKEN_INTEGRACAO = ?" +
                    "RETURNING TOKEN_INTEGRACAO");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, token);
            stmt.setString(3, token);
            rSet = stmt.executeQuery();
            if(rSet.next()){
                return rSet.getString("TOKEN_INTEGRACAO");
            } else {
                return null;
            }
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao criar TOKEN");
        } finally {
            connectionProvider.closeResources(conn, stmt, rSet);
        }
    }

    //Método responsável por remover TOKEN de autenticação.
    void removeTokenIntegracaoCriado(@NotNull Long codEmpresa,
                                     @NotNull String token) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("DELETE FROM INTEGRACAO.TOKEN_INTEGRACAO WHERE COD_EMPRESA = ? " +
                    "AND TOKEN_INTEGRACAO = ?;");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, token);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao deletar TOKEN");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Método responsável pela configuração de sobrescrita de uma empresa.
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

    //Método responsável por buscar modelo banda pneu na empresa.
    private Long buscaCodModeloBandaPneuEmpresa(@NotNull Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> codModelosBandaEmpresa = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT MB.CODIGO FROM MODELO_BANDA MB WHERE MB.COD_EMPRESA = ?");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while(rSet.next()){
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
    private Long buscaCodModeloPneuEmpresa(@NotNull Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> codModelosPneuEmpresa = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = ?");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while(rSet.next()){
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
            while(rSet.next()){
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
    private Long buscaCodModeloVeiculo(@NotNull Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> modelos = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM MODELO_VEICULO WHERE COD_EMPRESA = ?;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while(rSet.next()){
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
    private Long buscaCodTipoVeiculo(@NotNull Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Long> tipos = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM VEICULO_TIPO WHERE COD_EMPRESA = ?;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            while(rSet.next()){
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
    private String buscaPlacaUnidade(@NotNull Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<String> placas = new ArrayList<>();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT V.PLACA FROM VEICULO_DATA V WHERE V.COD_UNIDADE = ? " +
                    "AND V.PLACA NOT IN (SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while(rSet.next()){
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

    //Método responsável por inserir placa na integração.
    private void adicionaPlacaEmIntegracao(@NotNull String placa,
                                           @NotNull Long codEmpresa,
                                           @NotNull Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        LocalDateTime hora = LocalDateTime.now();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            stmt = conn.prepareStatement("INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO, " +
                    "COD_UNIDADE_CADASTRO, COD_VEICULO_CADASTRO_PROLOG, PLACA_VEICULO_CADASTRO, " +
                    "DATA_HORA_CADASTRO_PROLOG, DATA_HORA_ULTIMA_EDICAO)\n" +
                    "VALUES(?, ?, (SELECT CODIGO FROM VEICULO_DATA WHERE PLACA = ?), ?, ?, ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, placa);
            stmt.setString(4, placa);
            stmt.setObject(5, hora);
            stmt.setObject(6, hora);
            stmt.executeUpdate();
        } catch (final Throwable throwable) {
            throw new SQLException("Erro ao inserir placa");
        } finally {
            connectionProvider.closeResources(conn, stmt);
        }
    }

    //Método responsável por pegar posições de uma placa;
    private List<Integer> buscaPosicaoesPlaca(@NotNull String placa) throws Throwable {
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
            while(rSet.next()){
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
    private ApiPneuCargaInicial buscaInformacoesPneuCargaInicialEstoque(@NotNull final Long codSistemaIntegrado,
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

    //Método responsável por pegar todas as informações do pneu na carga inicial.
    private ApiPneuCargaInicial buscaInformacoesPneuCargaInicialEmUso(@NotNull final Long codSistemaIntegrado,
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
                        ApiStatusPneu.EM_USO,
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
        Long codPneuCadastroProlog = buscaCodPneuCadastroProlog(codSistemaIntegrado, codCliente, codUnidade,
                codEmpresa);
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
    private ApiPneuCargaInicial criaPneuSemErroComCodigoClienteValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(500),
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
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComModeloPneuValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComDimensaoValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComPressaoValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComVidaAtualValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComDotValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComModeloDeBandaValido() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
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
    private ApiPneuCargaInicial criaPneuSemErroComPlacaPneuValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(100.00),
                ApiStatusPneu.ESTOQUE,
                "LLL1234",
                904);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuComPosicoesEspecificas(@NotNull final int posicao,
                                                               @NotNull final String placa) throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                2,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(100.00),
                ApiStatusPneu.EM_USO,
                placa,
                posicao);
    }

    @NotNull
    private ApiPneuCargaInicial criaPneuSemErroComPosicaoPneuValida() throws Throwable {
        return new ApiPneuCargaInicial(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                1,
                4,
                "1010",
                new BigDecimal(1500.0),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(100.00),
                ApiStatusPneu.ESTOQUE,
                "LLL1234",
                904);
    }

    //Objeto Pneu preenchido para testes sem erro.
    @NotNull
    private ApiPneuCadastro criaPneuParaInsertSemErro() throws Throwable {
        return new ApiPneuCadastro(
                geraCodSistemaIntegrado(),
                geraCodCliente(),
                COD_UNIDADE,
                buscaCodModeloPneuEmpresa(COD_EMPRESA),
                buscaCodDimensao(),
                120.0,
                3,
                4,
                "1010",
                new BigDecimal(1000.00),
                false,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
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
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusDescarteSemErro(ApiPneuCadastro apiPneuCadastro)
            throws Throwable {
        return new ApiPneuAlteracaoStatusDescarte(
                apiPneuCadastro.getCodigoSistemaIntegrado(),
                apiPneuCadastro.getCodigoCliente(),
                apiPneuCadastro.getCodUnidadePneu(),
                "12345678910",
                LocalDateTime.now(),
                true,
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(400.00));
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
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(120.00));
    }

    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEmUsoSemErro(ApiPneuCadastro apiPneuCadastro)
            throws Throwable {
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
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(300.00));
    }

    //Objeto Pneu para atualizar STATUS em carga inicial
    @NotNull
    private ApiPneuAlteracaoStatus criaPneuParaAtualizarStatusEstoqueSemErroCargaInicial(
            ApiPneuCargaInicial apiPneuCargaInicial) throws Throwable {
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
                buscaCodModeloBandaPneuEmpresa(COD_EMPRESA),
                new BigDecimal(400.00));
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
                false,
                null,
                null);
    }

    //Método responsável por criar um novo veículo para cadastrar.
    private VeiculoCadastroPraxio criaVeiculoParaCadastro() throws Throwable {
        return new VeiculoCadastroPraxio(
                COD_UNIDADE,
                "PLA"+RANDOM.nextInt(9999),
                1000L,
                buscaCodModeloVeiculo(COD_EMPRESA),
                buscaCodTipoVeiculo(COD_EMPRESA)
        );
    }
}