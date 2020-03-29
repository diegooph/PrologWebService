package test.br.com.zalf.prolog.webservice.integracao.nepomuceno;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 2020-03-17
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestMethodOrder(MethodOrderer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TipoVeiculoCodigoAuxiliarTest extends BaseTest {
    @NotNull
    private static final String VALID_CPF = "03383283194";
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static final Long COD_DIAGRAMA = 1L;
    @NotNull
    private static final Random RANDOM = new Random();
    private TipoVeiculoService service;
    private String userToken;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        service = new TipoVeiculoService();
        userToken = getValidToken(VALID_CPF);
    }

    @AfterAll
    public void destroy() {
        service = null;
        userToken = null;
        DatabaseManager.finish();
    }

    @Order(1)
    @Test
    void testInsereTipoVeiculoSemCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(responseWithCod.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNull();

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(2)
    @Test
    void testInsereTipoVeiculoComCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(responseWithCod.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculo.getCodAuxiliar());

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(3)
    @Test
    void testAtualizaTipoVeiculoInsereCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A1:B1");

        service.updateTipoVeiculo(userToken, tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(tipoVeiculoAlterado.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculoAlterado.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculoAlterado.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculoAlterado.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculoAlterado.getCodAuxiliar());

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(4)
    @Test
    void testAtualizaTipoVeiculoRemoveCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar(null);

        service.updateTipoVeiculo(userToken, tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNull();

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(5)
    @Test
    void testAtualizaTipoVeiculoAtualizaCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A2:B3");

        service.updateTipoVeiculo(userToken, tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculoAlterado.getCodAuxiliar());

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(6)
    @Test
    void testInsereTipoVeiculoComMultiCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2,A2:B3,A1:B1");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(responseWithCod.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculo.getCodAuxiliar());

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(7)
    @Test
    void testAtualizaTipoVeiculoAtualizaMultiCodAuxiliar() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCod = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCod.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A1:B2,A2:B3");

        service.updateTipoVeiculo(userToken, tipoVeiculoAlterado);

        final TipoVeiculo tipoVeiculoBuscado = service.getTipoVeiculo(tipoVeiculoAlterado.getCodigo());

        assertThat(tipoVeiculoBuscado).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodigo()).isEqualTo(responseWithCod.getCodigo());
        assertThat(tipoVeiculoBuscado.getNome()).isEqualTo(tipoVeiculo.getNome());
        assertThat(tipoVeiculoBuscado.getCodEmpresa()).isEqualTo(tipoVeiculo.getCodEmpresa());
        assertThat(tipoVeiculoBuscado.getCodDiagrama()).isEqualTo(tipoVeiculo.getCodDiagrama());
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isNotNull();
        assertThat(tipoVeiculoBuscado.getCodAuxiliar()).isEqualTo(tipoVeiculoAlterado.getCodAuxiliar());

        removeCodAuxiliarFromTipoVeiculo(tipoVeiculoBuscado.getCodigo());
    }

    @Order(8)
    @Test
    void testInsereTipoVeiculoComCodAuxiliarRepetido() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseTipoVeiculo = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculo1 = new TipoVeiculo();
        tipoVeiculo1.setNome(getName());
        tipoVeiculo1.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo1.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo1.setCodAuxiliar("A1:B2,A1:B1");
        final Throwable throwable = assertThrows(
                ProLogException.class, () -> service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo1));

        assertThat(throwable).isInstanceOf(GenericException.class);

        removeCodAuxiliarFromTipoVeiculo(responseTipoVeiculo.getCodigo());
    }

    @Order(9)
    @Test
    void testInsereTipoVeiculoComMultiCodAuxiliarRepetido() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2,A2:B2");
        final ResponseWithCod responseTipoVeiculo = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculo1 = new TipoVeiculo();
        tipoVeiculo1.setNome(getName());
        tipoVeiculo1.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo1.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo1.setCodAuxiliar("A1:B2");

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo1));

        assertThat(throwable).isInstanceOf(GenericException.class);

        removeCodAuxiliarFromTipoVeiculo(responseTipoVeiculo.getCodigo());
    }

    @Order(10)
    @Test
    void testAtualizaTipoVeiculoAtualizaMultiCodAuxiliarRepetido() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2");
        final ResponseWithCod responseWithCodTipVeiculo = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo);

        final TipoVeiculo tipoVeiculo1 = new TipoVeiculo();
        tipoVeiculo1.setNome(getName());
        tipoVeiculo1.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo1.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo1.setCodAuxiliar("A2:B2");
        final ResponseWithCod responseWithCodTipVeiculo1 = service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo1);

        final TipoVeiculo tipoVeiculoAlterado = service.getTipoVeiculo(responseWithCodTipVeiculo.getCodigo());
        tipoVeiculoAlterado.setCodAuxiliar("A1:B2,A2:B2");

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> service.updateTipoVeiculo(userToken, tipoVeiculoAlterado));

        assertThat(throwable).isInstanceOf(GenericException.class);

        removeCodAuxiliarFromTipoVeiculo(responseWithCodTipVeiculo.getCodigo());
        removeCodAuxiliarFromTipoVeiculo(responseWithCodTipVeiculo1.getCodigo());
    }

    @Order(11)
    @Test
    void testInsereTipoVeiculoComCodAuxiliarForaPadrao() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1B2");

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo));

        assertThat(throwable).isInstanceOf(GenericException.class);
    }

    @Order(12)
    @Test
    void testInsereTipoVeiculoComMultiCodAuxiliarForaPadrao() throws Throwable {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setNome(getName());
        tipoVeiculo.setCodEmpresa(COD_EMPRESA);
        tipoVeiculo.setCodDiagrama(COD_DIAGRAMA);
        tipoVeiculo.setCodAuxiliar("A1:B2,A2B3");

        final Throwable throwable = assertThrows(
                ProLogException.class, () -> service.insertTipoVeiculoPorEmpresa(userToken, tipoVeiculo));

        assertThat(throwable).isInstanceOf(GenericException.class);
    }

    @NotNull
    private String getName() {
        return "TESTE " + RANDOM.nextInt(1000);
    }

    private void removeCodAuxiliarFromTipoVeiculo(@NotNull final Long codTipoVeiculo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement("update veiculo_tipo " +
                    "set cod_auxiliar = null " +
                    "where codigo = ?;");
            stmt.setLong(1, codTipoVeiculo);
            stmt.executeUpdate();
        } finally {
            provider.closeResources(conn, stmt);
        }
    }
}
