package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCadastro;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PneuCrudApiTest extends BaseTest {
    @NotNull
    private static final String TOKEN_INTEGRACAO = "NATAN";
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    private ApiCadastroPneuService apiCadastroPneuService;

    @Before
    public void initialize() throws Throwable {
        DatabaseManager.init();
        this.apiCadastroPneuService = new ApiCadastroPneuService();
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
                10L,
                "Pneu-01",
                5L,
                10L,
                10L,
                120.00,
                1,
                4,
                "1010",
                new BigDecimal(1000.0),
                true,
                10L,
                new BigDecimal(100.60)
        );

        //Execução
        final SuccessResponseIntegracao successResponseIntegracao = apiCadastroPneuService.inserirPneuCadastro(TOKEN_INTEGRACAO, apiPneuCadastro);

        //Verificações
        assertThat(successResponseIntegracao).isNotNull();
    }


}
