package test.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

import java.util.Collections;
import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TipoVeiculoTest extends BaseTest {
    private static final Long COD_EMPRESA_ZALF = 3L;
    private static final Long CPF_COLABORADOR = 3383283194L;
    private TipoVeiculoService service;

    @Override
    public void initialize() {
        DatabaseManager.init();
        service = new TipoVeiculoService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void testInsertTipoVeiculoEmpresa() throws Throwable {
        final TipoVeiculo tipoVeiculoCriado = new TipoVeiculo();
        tipoVeiculoCriado.setCodEmpresa(COD_EMPRESA_ZALF);
        tipoVeiculoCriado.setNome("Tipo Teste");
        final Response response = service.insertTipoVeiculoPorEmpresa(tipoVeiculoCriado);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isOk());

        final List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        boolean estaNaListagem = false;
        for (TipoVeiculo tipoVeiculo : tiposVeiculos) {
            if (tipoVeiculo.getNome().equals(tipoVeiculoCriado.getNome())
                    && tipoVeiculo.getCodEmpresa().equals(tipoVeiculoCriado.getCodEmpresa())) {
                estaNaListagem = true;
            }
        }
        Assert.assertTrue(estaNaListagem);
    }

    @Test
    public void testUpdateTipoVeiculoEmpresa() throws Throwable {
        final List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        Collections.shuffle(tiposVeiculos);

        final TipoVeiculo tipoVeiculoSorteado = tiposVeiculos.get(0);
        Assert.assertNotNull(tipoVeiculoSorteado);
        tipoVeiculoSorteado.setNome("Teste tipoSorteado");
        final Response response = service.updateTipoVeiculo(tipoVeiculoSorteado);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isOk());

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoSorteado.getCodigo());

        Assert.assertNotNull(tipoVeiculoBuscado);
        Assert.assertEquals(tipoVeiculoSorteado.getCodigo(), tipoVeiculoBuscado.getCodigo());
        Assert.assertEquals(tipoVeiculoSorteado.getCodEmpresa(), tipoVeiculoBuscado.getCodEmpresa());
        Assert.assertEquals(tipoVeiculoSorteado.getNome(), tipoVeiculoBuscado.getNome());
    }

    @Test
    public void testDeleteTipoVeiculoEmpresa() throws Throwable {
        List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        Collections.shuffle(tiposVeiculos);

        final TipoVeiculo tipoVeiculoSorteado = tiposVeiculos.get(0);
        Assert.assertNotNull(tipoVeiculoSorteado);
        final Response response = service.deleteTipoVeiculoByEmpresa(COD_EMPRESA_ZALF, tipoVeiculoSorteado.getCodigo());

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isOk());

        // Ao buscar todos os tipos da empresa, o tipo deletado não deve estar presente.
        tiposVeiculos = service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        boolean estaNaListagem = false;
        for (TipoVeiculo tipoVeiculo : tiposVeiculos) {
            if (tipoVeiculo.getNome().equals(tipoVeiculoSorteado.getNome())
                    && tipoVeiculo.getCodEmpresa().equals(tipoVeiculoSorteado.getCodEmpresa())) {
                estaNaListagem = true;
            }
        }
        Assert.assertFalse(estaNaListagem);

        // Ao buscar através do código, o tipo de veículo deve ser retornado.
        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoSorteado.getCodigo());

        Assert.assertNotNull(tipoVeiculoBuscado);
        Assert.assertEquals(tipoVeiculoSorteado.getCodigo(), tipoVeiculoBuscado.getCodigo());
        Assert.assertEquals(tipoVeiculoSorteado.getCodEmpresa(), tipoVeiculoBuscado.getCodEmpresa());
        Assert.assertEquals(tipoVeiculoSorteado.getNome(), tipoVeiculoBuscado.getNome());
    }

    @Test
    public void testBuscaListagemTiposVeiculosEmpresa() throws Throwable {
        final List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        tiposVeiculos.forEach(tipoVeiculo -> {
            Assert.assertNotNull(tipoVeiculo);
            Assert.assertNotNull(tipoVeiculo.getCodigo());
            Assert.assertTrue(tipoVeiculo.getCodigo() > 0);
            Assert.assertNotNull(tipoVeiculo.getCodEmpresa());
            Assert.assertEquals(tipoVeiculo.getCodEmpresa(), COD_EMPRESA_ZALF);
            Assert.assertNotNull(tipoVeiculo.getNome());
            Assert.assertTrue(tipoVeiculo.getNome().trim().isEmpty());
        });
    }

    @Test
    public void testBuscaTipoVeiculoByCod() throws Throwable {
        final List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        Collections.shuffle(tiposVeiculos);

        final TipoVeiculo tipoVeiculo = service.getTipoVeiculo(tiposVeiculos.get(0).getCodigo());

        Assert.assertNotNull(tipoVeiculo);
        Assert.assertNotNull(tipoVeiculo.getCodigo());
        Assert.assertTrue(tipoVeiculo.getCodigo() > 0);
        Assert.assertNotNull(tipoVeiculo.getCodEmpresa());
        Assert.assertEquals(tipoVeiculo.getCodEmpresa(), COD_EMPRESA_ZALF);
        Assert.assertNotNull(tipoVeiculo.getNome());
        Assert.assertTrue(tipoVeiculo.getNome().trim().isEmpty());
    }
}
