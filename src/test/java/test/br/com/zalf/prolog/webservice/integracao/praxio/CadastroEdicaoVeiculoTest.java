package test.br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.SQLException;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 04/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class CadastroEdicaoVeiculoTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private static final String CPF_USUARIO = "35262190871";
    private IntegracaoPraxioService serviceIntegracao;
    private VeiculoService serviceVeiculo;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        serviceIntegracao = new IntegracaoPraxioService();
        serviceVeiculo = new VeiculoService();
    }

    @Override
    public void destroy() throws Throwable {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void cadastroVeiculoTest() throws SQLException {
        final VeiculoCadastroPraxio veiculoCadastro = createVeiculoCadastro();
        serviceIntegracao.inserirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoCadastro);

        final Veiculo veiculoByPlaca =
                serviceVeiculo.getVeiculoByPlaca(getValidToken(CPF_USUARIO),
                                                 veiculoCadastro.getPlacaVeiculo(),
                                                 null,
                                                 false);

        assertThat(veiculoByPlaca).isNotNull();
        assertThat(veiculoByPlaca.getKmAtual()).isEqualTo(veiculoCadastro.getKmAtualVeiculo());
        assertThat(veiculoByPlaca.getModelo().getCodigo()).isEqualTo(veiculoCadastro.getCodModeloVeiculo());
        assertThat(veiculoByPlaca.getTipo().getCodigo()).isEqualTo(veiculoCadastro.getCodTipoVeiculo());

        System.out.println(GsonUtils.getGson().toJson(veiculoByPlaca));
    }

    @Test
    public void edicaoVeiculoTest() throws SQLException {
        final VeiculoEdicaoPraxio veiculoEdicao = createVeiculoEdicao();
        serviceIntegracao.atualizarVeiculoPraxio(
                TOKEN_INTEGRACAO,
                veiculoEdicao.getCodUnidadeAlocado(),
                veiculoEdicao.getPlacaVeiculo(),
                veiculoEdicao);

        final Veiculo veiculoByPlaca =
                serviceVeiculo.getVeiculoByPlaca(getValidToken(CPF_USUARIO),
                                                 veiculoEdicao.getPlacaVeiculo(),
                                                 null,
                                                 false);

        assertThat(veiculoByPlaca).isNotNull();
        assertThat(veiculoByPlaca.getKmAtual()).isEqualTo(veiculoEdicao.getNovoKmVeiculo());
        assertThat(veiculoByPlaca.getModelo().getCodigo()).isEqualTo(veiculoEdicao.getNovoCodModeloVeiculo());
        assertThat(veiculoByPlaca.getTipo().getCodigo()).isEqualTo(veiculoEdicao.getNovoCodTipoVeiculo());

        System.out.println(GsonUtils.getGson().toJson(veiculoByPlaca));
    }

    @Test
    public void ativarDesativarVeiculoTest() {
        final SuccessResponseIntegracao response =
                serviceIntegracao.ativarDesativarVeiculoPraxio(TOKEN_INTEGRACAO, "PIC0001", true);

        assertThat(response).isNotNull();
    }

    @Test
    public void transferenciaVeiculoTest() throws SQLException {
        final VeiculoTransferenciaPraxio veiculoTransferencia = createVeiculoTransferencia();
        serviceIntegracao.transferirVeiculoPraxio(TOKEN_INTEGRACAO, veiculoTransferencia);

        final Veiculo veiculoByPlaca =
                serviceVeiculo.getVeiculoByPlaca(getValidToken(CPF_USUARIO),
                                                 veiculoTransferencia.getPlacaTransferida(),
                                                 null,
                                                 false);

        assertThat(veiculoByPlaca).isNotNull();
        assertThat(veiculoByPlaca.getCodUnidadeAlocado()).isEqualTo(veiculoTransferencia.getCodUnidadeDestino());

        System.out.println(GsonUtils.getGson().toJson(veiculoByPlaca));
    }

    @NotNull
    private VeiculoTransferenciaPraxio createVeiculoTransferencia() {
        return new VeiculoTransferenciaPraxio(
                96L,
                108L,
                CPF_USUARIO,
                "PIC0001",
                null);
    }

    @NotNull
    private VeiculoEdicaoPraxio createVeiculoEdicao() {
        return new VeiculoEdicaoPraxio(
                96L,
                "PIC0001",
                22222L,
                398L,
                327L);
    }

    @NotNull
    private VeiculoCadastroPraxio createVeiculoCadastro() {
        return new VeiculoCadastroPraxio(
                96L,
                "PIC0002",
                12324L,
                397L,
                327L);
    }
}
