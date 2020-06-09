package test.br.com.zalf.prolog.webservice.integracao.nepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.ProtheusNepomucenoUtils;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.SistemaProtheusNepomucenoDaoImpl;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoAfericaoProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoListagemProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
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
import java.util.*;
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
    private static final String URL_CRONOGRAMA = "http://mercurio.expressonepomuceno.com.br:9052/rest/CRONOGRAMA_AFERICAO";
    @NotNull
    private static final String URL_NOVA_AFERICAO = "http://mercurio.expressonepomuceno.com.br:9052/rest/NOVA_AFERICAO";
    @NotNull
    private static final String COD_FILIAIS = "09:02,09:04,01:21";
    @NotNull
    private static final String COD_TODAS_FILIAIS =
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
     * Buscamos todas as placas e para cada uma, verificamos se ela está mapeada em um cod_auxiliar no nosso banco.
     * <p>
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
                requester.getListagemVeiculosUnidadesSelecionadas(URL_CRONOGRAMA, COD_FILIAIS);
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

    /**
     * Este teste valida para todas as placas, se existe alguma com mapeamento de posições incorretas.
     * <p>
     * Determina-se mapeamento incorreto:
     * a) caso a Posição Nepomuceno não esteja mapeada no Prolog.
     * b) caso a Posição Nepomuceno esteja mapeada para mais de uma posição Prolog.
     */
    @Test
    void testPosicoesMapeadasIncorretamente() throws Throwable {
        final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
        final ProtheusNepomucenoRequester requester = new ProtheusNepomucenoRequesterImpl();
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();

        final List<VeiculoListagemProtheusNepomuceno> placasFiltradas =
                requester.getListagemVeiculosUnidadesSelecionadas(URL_CRONOGRAMA, COD_FILIAIS)
                        .stream()
                        .filter(v -> !v.deveRemover())
                        .collect(Collectors.toList());

        final Map<VeiculoAfericaoProtheusNepomuceno, Throwable> veiculosComProblemas = new HashMap<>();
        final Map<VeiculoListagemProtheusNepomuceno, Throwable> veiculosComProblemasSerios = new HashMap<>();
        Connection conn = null;
        try {
            conn = provider.provideDatabaseConnection();
            for (final VeiculoListagemProtheusNepomuceno placa : placasFiltradas) {
                try {
                    final VeiculoAfericaoProtheusNepomuceno veiculoAfericao =
                            requester.getPlacaPneusAfericaoPlaca(
                                    URL_NOVA_AFERICAO,
                                    placa.getCodEmpresaFilialVeiculo(),
                                    placa.getPlacaVeiculo());

                    final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper = new ProtheusNepomucenoPosicaoPneuMapper(
                            veiculoAfericao.getCodEstruturaVeiculo(),
                            sistema.getMapeamentoPosicoesProlog(conn, COD_EMPRESA, veiculoAfericao.getCodEstruturaVeiculo()));

                    try {
                        ProtheusNepomucenoUtils
                                .validatePosicoesMapeadasVeiculo(
                                        veiculoAfericao.getCodEstruturaVeiculo(),
                                        veiculoAfericao.getPosicoesPneusAplicados(),
                                        posicaoPneuMapper);
                    } catch (final ProtheusNepomucenoException e) {
                        veiculosComProblemas.put(veiculoAfericao, e);
                    }
                } catch (final Throwable t) {
                    veiculosComProblemasSerios.put(placa, t);
                }
            }
        } finally {
            provider.closeResources(conn);
        }

        final Map<String, List<VeiculoAfericaoProtheusNepomuceno>> agrupadoEstrutura = new HashMap<>();
        for (final VeiculoAfericaoProtheusNepomuceno veiculoProblema : veiculosComProblemas.keySet()) {
            if (!agrupadoEstrutura.containsKey(veiculoProblema.getCodEstruturaVeiculo())) {
                agrupadoEstrutura.put(veiculoProblema.getCodEstruturaVeiculo(), new ArrayList<>());
            }
            agrupadoEstrutura.get(veiculoProblema.getCodEstruturaVeiculo()).add(veiculoProblema);
        }

        System.out.println("Veículos com problemas de mapeamento:\n");
        veiculosComProblemas
                .keySet()
                .stream()
                .map(s -> s.getCodEstruturaVeiculo() + " - "
                        + s.getPlacaVeiculo())
                .forEach(System.out::println);

        System.out.println("\n\nVeículos com problemas aleatórios:\n");
        veiculosComProblemasSerios
                .keySet()
                .stream()
                .map(s -> s.getCodEmpresaFilialVeiculo() + " - "
                        + s.getCodEstruturaVeiculo() + " - "
                        + s.getPlacaVeiculo())
                .forEach(System.out::println);
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
