package test.br.com.zalf.prolog.webservice.integracao.nepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoListagemProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequester;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-06-02
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ModelosNaoMapeadosTest {
    @NotNull
    private static final Long COD_EMPRESA = 3L;
    @NotNull
    private static final String URL = "http://mercurio.expressonepomuceno.com.br:9052/rest/CRONOGRAMA_AFERICAO";
    @NotNull
    private static final String COD_FILIAIS =
            "09:02,01:21,09:04,01:06,01:16,01:56,01:23,01:52,01:11,01:29,01:01,01:63,01:59,01:32,01:03,01:27";

    @BeforeAll
    public void initialize() {
        DatabaseManager.init();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    /**
     * Para realizarmos esse teste, iremos:
     * 1 - Buscar os modelos mapeados no banco de dados;
     * 2 - Buscar todas as placas da Nepomuceno (através da busca de cronograma);
     * 3 - Criaremos um SET contendo os modelos não mapeados;
     * 4 - Para cada placa retornada, iremos validar se a familia:modelo está presente nos modelos mapeados;
     * 5 - Ao final, mostraremos na tela o que não está mapeado;
     */
    @Test
    void testBuscaModelosNaoMapeados() throws Throwable {
        final List<String> modelosMapeados = buscaModelosMapeados();

        final ProtheusNepomucenoRequester requester = new ProtheusNepomucenoRequesterImpl();
        final List<VeiculoListagemProtheusNepomuceno> listaPlacas =
                requester.getListagemVeiculosUnidadesSelecionadas(URL, "09:02,09:04,01:21");
        final List<String> modelosNepomuceno =
                listaPlacas
                        .stream()
                        .map(VeiculoListagemProtheusNepomuceno::getCodEstruturaVeiculo)
                        .distinct()
                        .collect(Collectors.toList());

        final List<VeiculoListagemProtheusNepomuceno> placasSemPneus = listaPlacas
                .stream()
                .filter(s -> s.getCodEstruturaVeiculo().contains("FA008") || s.getCodEstruturaVeiculo().contains("FA011"))
                .filter(s -> s.getQtdPneusAplicadosVeiculo() > 0)
                .collect(Collectors.toList());

        modelosNepomuceno.removeAll(modelosMapeados);

        // Remover modelos da familia FA008 e FA011
        modelosNepomuceno.removeIf(s -> s.contains("FA008") || s.contains("FA011"));

        System.out.println(modelosNepomuceno);
    }

    @NotNull
    private List<String> buscaModelosMapeados() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement("select regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar " +
                    "from veiculo_tipo vt " +
                    "where vt.cod_empresa = ?;");
            stmt.setLong(1, COD_EMPRESA);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<String> modelosMapeados = new ArrayList<>();
                do {
                    modelosMapeados.add(rSet.getString("cod_auxiliar"));
                } while (rSet.next());
                return modelosMapeados;
            } else {
                return Collections.emptyList();
            }
        } finally {
            provider.closeResources(conn, stmt, rSet);
        }
    }
}
