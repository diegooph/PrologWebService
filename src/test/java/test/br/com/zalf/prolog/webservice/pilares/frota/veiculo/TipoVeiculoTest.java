package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.SQLException;
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
    private String userToken;

    @Override
    public void initialize() throws SQLException {
        DatabaseManager.init();
        service = new TipoVeiculoService();
        userToken = getValidToken(String.valueOf(CPF_COLABORADOR));
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
        final ResponseWithCod response = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculoCriado);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isOk());
        Assert.assertNotNull(response.getCodigo());
        Assert.assertTrue(response.getCodigo() > 0L);

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
        final Response response = service.updateTipoVeiculo(userToken, tipoVeiculoSorteado);

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
        final TipoVeiculo tipoVeiculoCriado = new TipoVeiculo();
        tipoVeiculoCriado.setCodEmpresa(COD_EMPRESA_ZALF);
        tipoVeiculoCriado.setNome("Tipo Teste");

        final ResponseWithCod responseInsert = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculoCriado);
        Assert.assertNotNull(responseInsert);
        Assert.assertTrue(responseInsert.isOk());
        Assert.assertNotNull(responseInsert.getCodigo());
        Assert.assertTrue(responseInsert.getCodigo() > 0L);
        tipoVeiculoCriado.setCodigo(responseInsert.getCodigo());

        // Deleta.
        final Response responseDelete = service.deleteTipoVeiculoByEmpresa(
                COD_EMPRESA_ZALF,
                tipoVeiculoCriado.getCodigo());

        Assert.assertNotNull(responseDelete);
        Assert.assertTrue(responseDelete.isOk());

        final List<TipoVeiculo> tiposVeiculos =
                service.getTiposVeiculosByEmpresa(getValidToken(CPF_COLABORADOR.toString()), COD_EMPRESA_ZALF);

        Assert.assertNotNull(tiposVeiculos);
        Assert.assertFalse(tiposVeiculos.isEmpty());

        boolean estaNaListagem = false;
        for (final TipoVeiculo tipoVeiculo : tiposVeiculos) {
            if (tipoVeiculo.getCodigo().equals(tipoVeiculoCriado.getCodigo())) {
                estaNaListagem = true;
            }
        }
        Assert.assertFalse(estaNaListagem);
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
            Assert.assertFalse(tipoVeiculo.getNome().trim().isEmpty());
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
        Assert.assertFalse(tipoVeiculo.getNome().trim().isEmpty());
    }
}
