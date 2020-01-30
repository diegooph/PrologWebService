package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicial;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicialResponse;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 1/28/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class CargaInicialTest extends BaseTest {
    private static final String TOKEN_TLX = "m218ko13bge3ktdidinvlrqhs4a5vfffcounabva10hri7pl6gv";

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    void testSobrecargaInicialTodosOsPneus() throws Throwable {
        final List<ApiPneuCargaInicial> pneusCargaInicial = new ArrayList<>();
        { // region - Busca pneus na base integrada.
            final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = connectionProvider.provideDatabaseConnection();
                stmt = conn.prepareStatement("select pc.cod_pneu_sistema_integrado," +
                        "       pc.cod_cliente_pneu_cadastro," +
                        "       p.cod_unidade," +
                        "       p.cod_modelo," +
                        "       p.cod_dimensao," +
                        "       p.pressao_recomendada," +
                        "       p.vida_atual," +
                        "       p.vida_total," +
                        "       p.dot," +
                        "       p.valor," +
                        "       p.pneu_novo_nunca_rodado," +
                        "       p.cod_modelo_banda," +
                        "       1000 as valor_banda," +
                        "       p.status," +
                        "       vp.placa," +
                        "       vp.posicao " +
                        "from integracao.pneu_cadastrado pc" +
                        "         join pneu p on pc.cod_pneu_cadastro_prolog = p.codigo" +
                        "         left join veiculo_pneu vp on p.codigo = vp.cod_pneu " +
                        "where pc.cod_empresa_cadastro = 45;");
                rSet = stmt.executeQuery();
                while (rSet.next()) {
                    pneusCargaInicial.add(createPneuCargaInicial(rSet));
                }
            } finally {
                connectionProvider.closeResources(conn, stmt, rSet);
            }

            assertThat(pneusCargaInicial).isNotEmpty();
        }

        final List<ApiPneuCargaInicial> pneusCargaInicialAtualizados = new ArrayList<>();
        { // region - Atualiza informações dos pneus
            for (ApiPneuCargaInicial apiPneuCargaInicial : pneusCargaInicial) {
                pneusCargaInicialAtualizados.add(atualiza(apiPneuCargaInicial));
            }

            assertThat(pneusCargaInicialAtualizados).isNotEmpty();
            assertThat(pneusCargaInicialAtualizados).hasSize(pneusCargaInicial.size());
        }

//        System.out.println(GsonUtils.getGson().toJson(pneusCargaInicialAtualizados));
//        saveJsonFile(pneusCargaInicialAtualizados);

        final ApiCadastroPneuService apiCadastroPneuService = new ApiCadastroPneuService();
        final List<ApiPneuCargaInicialResponse> apiPneuCargaInicialResponses =
                apiCadastroPneuService.inserirCargaInicialPneu(TOKEN_TLX, pneusCargaInicialAtualizados);

        assertThat(apiPneuCargaInicialResponses).isNotEmpty();
        assertThat(apiPneuCargaInicialResponses).hasSize(pneusCargaInicialAtualizados.size());

        for (final ApiPneuCargaInicialResponse response : apiPneuCargaInicialResponses) {
            assertThat(response.getSucesso()).isTrue();
        }
    }

    private void saveJsonFile(@NotNull final List<ApiPneuCargaInicial> pneusCargaInicialAtualizados) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            // Writing to a file
            mapper.writeValue(new File("/home/didi/Desktop/pneusAtualizar.json"), pneusCargaInicialAtualizados);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private ApiPneuCargaInicial atualiza(@NotNull final ApiPneuCargaInicial apiPneuCargaInicial) {
        final Integer vidaAtualPneu = apiPneuCargaInicial.getVidaAtualPneu();
        return new ApiPneuCargaInicial(
                apiPneuCargaInicial.getCodigoSistemaIntegrado(),
                apiPneuCargaInicial.getCodigoCliente(),
                265L,
                apiPneuCargaInicial.getCodModeloPneu(),
                apiPneuCargaInicial.getCodDimensaoPneu(),
                apiPneuCargaInicial.getPressaoCorretaPneu(),
                vidaAtualPneu > 1 ? vidaAtualPneu + 1 : vidaAtualPneu,
                vidaAtualPneu > 1 ? apiPneuCargaInicial.getVidaTotalPneu() + 1 : apiPneuCargaInicial.getVidaTotalPneu(),
                null,
                new BigDecimal(2000),
                apiPneuCargaInicial.getPneuNovoNuncaRodado(),
                apiPneuCargaInicial.getCodModeloBanda(),
                apiPneuCargaInicial.getValorBandaPneu(),
                apiPneuCargaInicial.getStatusPneu(),
                apiPneuCargaInicial.getPlacaVeiculoPneuAplicado(),
                apiPneuCargaInicial.getPosicaoPneuAplicado());
    }

    @NotNull
    private ApiPneuCargaInicial createPneuCargaInicial(@NotNull final ResultSet rSet) throws SQLException {
        final long codModeloBanda = rSet.getLong("cod_modelo_banda");
        return new ApiPneuCargaInicial(
                rSet.getLong("cod_pneu_sistema_integrado"),
                rSet.getString("cod_cliente_pneu_cadastro"),
                rSet.getLong("cod_unidade"),
                rSet.getLong("cod_modelo"),
                rSet.getLong("cod_dimensao"),
                rSet.getDouble("pressao_recomendada"),
                rSet.getInt("vida_atual"),
                rSet.getInt("vida_total"),
                rSet.getString("dot"),
                rSet.getBigDecimal("valor"),
                rSet.getBoolean("pneu_novo_nunca_rodado"),
                codModeloBanda > 0 ? codModeloBanda : null,
                rSet.getBigDecimal("valor_banda"),
                ApiStatusPneu.fromString(rSet.getString("status")),
                rSet.getString("placa"),
                rSet.getInt("posicao"));
    }
}
