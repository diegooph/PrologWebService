package test.br.com.zalf.prolog.webservice.integracao.nopomuceno;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.Random;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2020-03-17
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TipoVeiculoCodigoAuxiliarTest extends BaseTest {
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static final Long COD_DIAGRAMA = 1L;
    @NotNull
    private static final Random RANDOM = new Random();
    private TipoVeiculoService service;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        service = new TipoVeiculoService();
    }

    @AfterAll
    public void destroy() {
        service = null;
        DatabaseManager.finish();
    }

    @Test
    void testInsereTipoVeiculoSemCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome("TESTE " + RANDOM.nextInt(100));
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(tipoVeiculo);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(responseWithCod.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNull();
    }

    @Test
    void testInsereTipoVeiculoComCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome("TESTE " + RANDOM.nextInt(100));
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(tipoVeiculo);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(responseWithCod.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculo.getCodAuxiliar());
    }

    @Test
    void testAtualizaTipoVeiculoInsereCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome("TESTE " + RANDOM.nextInt(100));
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A1:B1");

        service.updateTipoVeiculo(tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(tipoVeiculoAlterado.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculoAlterado.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculoAlterado.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculoAlterado.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculoAlterado.getCodAuxiliar());
    }

    @Test
    void testAtualizaTipoVeiculoRemoveCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome("TESTE " + RANDOM.nextInt(100));
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar(null);

        service.updateTipoVeiculo(tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNull();
    }

    @Test
    void testAtualizaTipoVeiculoAtualizaCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome("TESTE " + RANDOM.nextInt(100));
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A2:B3");

        service.updateTipoVeiculo(tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculoAlterado.getCodAuxiliar());
    }
}
