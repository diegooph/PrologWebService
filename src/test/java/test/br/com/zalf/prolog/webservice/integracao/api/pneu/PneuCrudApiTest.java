package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
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

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 25/02/2020
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class PneuCrudApiTest extends BaseTest {
    @NotNull
    private static final String TOKEN_INTEGRACAO = "NATAN";
    private ApiCadastroPneuService apiCadastroPneuService;
    private ApiPneuService apiPneuService;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        this.apiCadastroPneuService = new ApiCadastroPneuService();
        this.apiPneuService = new ApiPneuService();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    public Long geraValorAleatorio() {
        int random = (int) (Math.random() * ((99999 - 1) + 1)) + 1;
        Long valor = Long.valueOf(random);
        return valor;
    }

    @Test
    @DisplayName("Inserção carga inicial de Pneus sem erros")
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
        cargaInicial.add(criaPneuSemErroComPlacaPneuValida());
        cargaInicial.add(criaPneuSemErroComPosicaoPneuValida());

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses = apiCadastroPneuService
                .inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isTrue();
        }
    }

    @Test
    @DisplayName("Inserção carga inicial de Pneus com erros")
    void adicionaCargaInicialPneuComErroTest() throws Throwable {
        //Cenário
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(criaPneuComErroCodigoClienteJaExiste());
        cargaInicial.add(criaPneuComErroCodigoUnidadeNaoExiste());
        cargaInicial.add(criaPneuComErroCodigoModeloNaoExiste());
        cargaInicial.add(criaPneuComErroCodigoDimensaoNaoExiste());
        cargaInicial.add(criaPneuComErroPressaoIncorreta());
        cargaInicial.add(criaPneuComErroVidaAtualMaiorQueTotal());
        cargaInicial.add(criaPneuComErroDotInvalido());
        cargaInicial.add(criaPneuComErroModeloBandaNaoExiste());
        cargaInicial.add(criaPneuComErroPlacaPneuNaoExistente());
        cargaInicial.add(criaPneuComErroPosicaoPneuAplicadoNaoExistente());

        //Execução
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses = apiCadastroPneuService
                .inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        //Verificações
        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses.size()).isEqualTo(cargaInicial.size());
        for (int i = 0; i < apiPneuCargaInicialResponses.size(); i++) {
            assertThat(apiPneuCargaInicialResponses.get(i).getSucesso()).isFalse();
        }
    }

    @Test
    @DisplayName("Inserção de um novo Pneu sem erro")
    void adicionaPneuSemErrosTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInsertSemErro();

        //Execução
        final SuccessResponseIntegracao successResponseIntegracao = apiCadastroPneuService
                .inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
    }

    @Test
    @DisplayName("Inserção de um novo Pneu com erro")
    void adicionaPneuComErrosTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = criaPneuParaInserirComErro();

        try {
            //Execução
            final SuccessResponseIntegracao successResponseIntegracao = apiCadastroPneuService
                    .inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

            //Verificações
            assertThat(successResponseIntegracao).isNotNull();
            assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
        } catch (final Throwable t) {
            assertThat(t).isNotNull();
        }
    }

    @Test
    @DisplayName("Atualiza status do pneu sem erros")
    void atualizaStatusPneuSemErroTest() throws Throwable {
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                13218L,
                "95687",
                5L,
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null
        ));

        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                94617L,
                "71157",
                5L,
                "12345678910",
                LocalDateTime.now(),
                true,
                11L,
                new BigDecimal(69.00)
        ));

        //Excecução
        final SuccessResponseIntegracao successResponseIntegracao = apiPneuService
                .atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
    }

    @Test
    @DisplayName("Atualiza status do pneu com erro")
    void atualizaStatusPneuComErroTest() throws Throwable {
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null
        ));

        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                "12345678910",
                LocalDateTime.now(),
                true,
                11L,
                new BigDecimal(69.00)
        ));

        try {
            //Excecução
            final SuccessResponseIntegracao successResponseIntegracao = apiPneuService
                    .atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

            //Verificações
            assertThat(successResponseIntegracao).isNotNull();
            assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
        } catch (final Throwable t) {
            assertThat(t).isNotNull();
        }
    }

    //Objetos Pneu para testes em Carga Inicial sem erro.
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
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

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

    //Objetos Pneu para testes em Carga Inicial com erro.
    private ApiPneuCargaInicial criaPneuComErroCodigoClienteJaExiste() {
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroCodigoUnidadeNaoExiste() {
        return new ApiPneuCargaInicial(
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroCodigoModeloNaoExiste() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                575L,
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroCodigoDimensaoNaoExiste() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                455L,
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroPressaoIncorreta() {
        return new ApiPneuCargaInicial(
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroVidaAtualMaiorQueTotal() {
        return new ApiPneuCargaInicial(
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
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroDotInvalido() {
        return new ApiPneuCargaInicial(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
                129L,
                1L,
                120.0,
                1,
                4,
                "99999",
                new BigDecimal(1500.0),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroModeloBandaNaoExiste() {
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
                7645L,
                new BigDecimal(400.00),
                ApiStatusPneu.ESTOQUE,
                null,
                null
        );
    }

    private ApiPneuCargaInicial criaPneuComErroPlacaPneuNaoExistente() {
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
                7645L,
                new BigDecimal(400.00),
                ApiStatusPneu.EM_USO,
                "OOO9891",
                111
        );
    }

    private ApiPneuCargaInicial criaPneuComErroPosicaoPneuAplicadoNaoExistente() {
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
                7645L,
                new BigDecimal(400.00),
                ApiStatusPneu.EM_USO,
                " PRECISA-SE DE UMA PLACA",
                7777
        );
    }

    //Objeto Pneu preenchido para testes sem erro.
    private ApiPneuCadastro criaPneuParaInsertSemErro() {
        return new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                5L,
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
    }

    //Objeto Pneu preenchido para testes com erro.
    private ApiPneuCadastro criaPneuParaInserirComErro() {
        return new ApiPneuCadastro(
                geraValorAleatorio(),
                geraValorAleatorio().toString(),
                1115L,
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
    }
}
