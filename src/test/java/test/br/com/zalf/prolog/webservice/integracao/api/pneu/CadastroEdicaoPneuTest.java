package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.ApiCadastroPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicial;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiPneuCargaInicialResponse;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 22/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class CadastroEdicaoPneuTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private ApiCadastroPneuService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiCadastroPneuService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void cargaInicialPneusTest() {
        final List<ApiPneuCargaInicial> pneusCargaInicial = new ArrayList<>();
        pneusCargaInicial.add(createPneuCorretoEstoque());
        pneusCargaInicial.add(createPneuCorretoRecapado());
        pneusCargaInicial.add(createPneuCorretoAplicado());
        pneusCargaInicial.add(createPneuCorretoAnalise());
        pneusCargaInicial.add(createPneuErroCodigoSistemaJaExiste());
        pneusCargaInicial.add(createPneuErroCodigoClienteJaExiste());
        pneusCargaInicial.add(createPneuErroCodigoUnidadeNaoExiste());
        pneusCargaInicial.add(createPneuErroCodigoModeloNaoExiste());
        pneusCargaInicial.add(createPneuErroCodigoDimensaoNaoExiste());
        pneusCargaInicial.add(createPneuErroVidaAtualMaiorVidaTotal());
        pneusCargaInicial.add(createPneuErroValorPneuNegativo());
        pneusCargaInicial.add(createPneuErroRecapadoSemBanda());
        pneusCargaInicial.add(createPneuErroCodigoBandaNaoExiste());
        pneusCargaInicial.add(createPneuErroValorBandaNegativo());
        pneusCargaInicial.add(createPneuErroPlacaNaoExiste());
        pneusCargaInicial.add(createPneuErroPosicaoInvalida());
        pneusCargaInicial.add(createPneuErroPosicaoJaUsada());
        pneusCargaInicial.add(createPneuCorretoDescarte());

        final List<ApiPneuCargaInicialResponse> response =
                service.inserirCargaInicialPneu(TOKEN_INTEGRACAO, pneusCargaInicial);
        assertThat(response).isNotNull();
        assertThat(pneusCargaInicial.size()).isEqualTo(response.size());
    }

    private ApiPneuCargaInicial createPneuCorretoEstoque() {
        return new ApiPneuCargaInicial(
                11111L,
                "PN001",
                96L,
                458L,
                1L,
                120.0,
                1,
                3,
                "1199",
                new BigDecimal(10000),
                true,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuCorretoRecapado() {
        return new ApiPneuCargaInicial(
                22222L,
                "PN002",
                104L,
                458L,
                2L,
                120.0,
                2,
                4,
                "1098",
                new BigDecimal(1000),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuCorretoAplicado() {
        return new ApiPneuCargaInicial(
                33333L,
                "PN003",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1000),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.EM_USO,
                "GFA2929",
                211);
    }

    private ApiPneuCargaInicial createPneuCorretoAnalise() {
        return new ApiPneuCargaInicial(
                44444L,
                "PN004",
                96L,
                458L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ANALISE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoSistemaJaExiste() {
        return new ApiPneuCargaInicial(
                44444L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ANALISE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoClienteJaExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN004",
                96L,
                458L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoUnidadeNaoExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                9633L,
                458L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoModeloNaoExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458899L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoDimensaoNaoExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1989L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroVidaAtualMaiorVidaTotal() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                4,
                1,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroValorPneuNegativo() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                1,
                4,
                "1045",
                new BigDecimal(-1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroRecapadoSemBanda() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                null,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroCodigoBandaNaoExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                23123L,
                null,
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroValorBandaNegativo() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                455L,
                new BigDecimal(-200),
                ApiStatusPneu.ESTOQUE,
                null,
                null);
    }

    private ApiPneuCargaInicial createPneuErroPlacaNaoExiste() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.EM_USO,
                "XXX0000",
                111);
    }

    private ApiPneuCargaInicial createPneuErroPosicaoInvalida() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.EM_USO,
                "GFA2929",
                1);
    }

    private ApiPneuCargaInicial createPneuErroPosicaoJaUsada() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.EM_USO,
                "GFA2929",
                111);
    }

    private ApiPneuCargaInicial createPneuCorretoDescarte() {
        return new ApiPneuCargaInicial(
                55555L,
                "PN005",
                96L,
                458L,
                1L,
                120.0,
                2,
                4,
                "1045",
                new BigDecimal(1500),
                false,
                455L,
                new BigDecimal(200),
                ApiStatusPneu.DESCARTE,
                "GFA2929",
                211);
    }
}
