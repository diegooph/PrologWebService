package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.ApiPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCadastro;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.*;
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

    @Test
    @DisplayName("Inserção de um novo Pneu")
    void adicionaPneuSemErrosTest() throws Throwable {
        //Cenário
        final ApiPneuCadastro apiPneuCadastro = new ApiPneuCadastro(
                888L,
                "PNEU_02",
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
                new BigDecimal(100.00)
        );

        //Execução
        final SuccessResponseIntegracao successResponseIntegracao = apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Verificações
        assertThat(successResponseIntegracao.getMsg()).isNotEmpty();
    }

    @Test
    @DisplayName("Atualiza status do pneu sem erros")
    void atualizaStatusPneuSemErroTest() throws Throwable {
        //Cenário
        final List<ApiPneuAlteracaoStatus> apiPneuAlteracaoStatus = new ArrayList<>();
        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusAnalise(
                999L,
                "PNEU_01",
                5L,
                "03383283194",
                LocalDateTime.now(),
                false,
                null,
                null
        ));

        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusDescarte(
                888L,
                "PNEU_02",
                5L,
                "03383283194",
                LocalDateTime.now(),
                true,
                10L,
                new BigDecimal(100.89)
        ));

//        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusEstoque(
//                999L,
//                "PNEU_01",
//                5L,
//                "03383283194",
//                LocalDateTime.now(),
//                false,
//                null,
//                null
//
//        ));

//        apiPneuAlteracaoStatus.add(new ApiPneuAlteracaoStatusVeiculo(
//                999L,
//                "PNEU_01",
//                5L,
//                "03383283194",
//                LocalDateTime.now(),
//                "PRO1010",
//                111,
//                false,
//                null,
//                null
//        ));

        //Excecução
        final SuccessResponseIntegracao successResponseIntegracao = apiPneuService.atualizaStatusPneus(TOKEN_INTEGRACAO, apiPneuAlteracaoStatus);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
    }
}
