package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicial;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicialResponse;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 11/1/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class CrudPneuApiTest extends BaseTest {
    @NotNull
    private static final String TOKEN_INTEGRACAO = "DIDI";
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    private ApiCadastroPneuService apiCadastroPneuService;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        this.apiCadastroPneuService = new ApiCadastroPneuService();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    @DisplayName("Carga inicial de Pneus Novos (não existem na base)")
    void cargaInicialPneusNovosSemErrosTest() throws Throwable {
        final List<ApiPneuCargaInicial> cargaInicial = new ArrayList<>();
        cargaInicial.add(new ApiPneuCargaInicial(
                1L,
                "PN1",
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
                null));

        cargaInicial.add(new ApiPneuCargaInicial(
                2L,
                "PN2",
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
                null));

        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_INTEGRACAO, cargaInicial);

        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses).hasSize(2);
        apiPneuCargaInicialResponses
                .forEach(apiPneuCargaInicialResponse -> {
                    assertThat(apiPneuCargaInicialResponse).isNotNull();
                    assertThat(apiPneuCargaInicialResponse.getSucesso()).isTrue();
                    assertThat(apiPneuCargaInicialResponse.getCodigoCliente()).isEqualTo("");
                    assertThat(apiPneuCargaInicialResponse.getCodPneuProLog()).isNotNull();
                });

    }

    @NotNull
    private Long getRandomCodModeloPneu() throws SQLException {
        final List<Marca> marcasModelos = Injection.providePneuDao().getMarcaModeloPneuByCodEmpresa(COD_EMPRESA);
        return 1L;
    }
}
