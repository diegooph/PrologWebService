package test.br.com.zalf.prolog.webservice.pilares.frota.aferidor;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.TesteAferidorService;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.ComandoExecutadoTeste;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.TesteAferidorExecutado;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ProcedimentoTesteAferidorTest extends BaseTest {
    private TesteAferidorService service;

    @BeforeAll
    public void initialize() throws Throwable {
        service = new TesteAferidorService();
        DatabaseManager.init();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void buscaProcedimentoTestes() {
        final List<String> comandosExecucao = service.getProcedimentoTeste().getComandosExecucao();
        assertThat(comandosExecucao).isNotNull();
        assertThat(comandosExecucao).hasSize(2);
        assertThat(comandosExecucao).containsExactly("B", "S");
    }

    @ParameterizedTest
    @CsvSource({
            "B, B95",
            "S, S1.0.0",
            "D, Aferidor Zalf 0001",
            "S, S2.0.0",
            "B, B0",
            "get_temp, Temperature log: Min = 23oC, Max = 34oC"
    })
    public void insereProcedimentoTestes(String comando, String retorno) throws SQLException {
        final List<ComandoExecutadoTeste> comandosExecutados = new ArrayList<>();
        // Colocamos duas vezes os mesmos comandos para testar sempre com mais de um comando.
        comandosExecutados.add(new ComandoExecutadoTeste(comando, retorno));
        comandosExecutados.add(new ComandoExecutadoTeste(comando, retorno));

        final TesteAferidorExecutado testeExecutado = new TesteAferidorExecutado(
                2272L,
                "Aferidor Zalf 0001",
                comandosExecutados);

        final ResponseWithCod response = service.insereTeste(testeExecutado);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.OK);
        final Long codigo = response.getCodigo();
        assertThat(codigo).isGreaterThan(0L);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "PT.COD_COLABORADOR_EXECUCAO, " +
                    "PT.NOME_DISPOSITIVO, " +
                    "PT.VALORES_EXECUCAO " +
                    "FROM AFERIDOR.PROCEDIMENTO_TESTE PT " +
                    "WHERE PT.CODIGO = ?;");
            stmt.setLong(1, codigo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                assertThat(2272L).isEqualTo(rSet.getLong("COD_COLABORADOR_EXECUCAO"));
                assertThat("Aferidor Zalf 0001").isEqualTo(rSet.getString("NOME_DISPOSITIVO"));

                // ComandoExecutadoTeste implementa equals() e hashCode().
                final List<ComandoExecutadoTeste> comandosBuscados =
                        comandosExecutadosFromJson(rSet.getString("VALORES_EXECUCAO"));
                assertThat(comandosExecutados).isEqualTo(comandosBuscados);
            } else {
                throw new IllegalStateException("Procedimento de teste não encontrado com o código: " + codigo);
            }
        } finally {
            DatabaseConnection.close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<ComandoExecutadoTeste> comandosExecutadosFromJson(@NotNull final String jsonComandos) {
        return GsonUtils
                .getGson()
                .fromJson(jsonComandos, new TypeToken<List<ComandoExecutadoTeste>>(){}.getType());
    }
}