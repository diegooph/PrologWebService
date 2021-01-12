package test.br.com.zalf.prolog.webservice.integracao.nepomuceno;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2020-06-15
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class OperacoesIntegradasProtheusNepomucenoTest extends BaseTest {
    @NotNull
    private static final String CPF_COLABORADOR = "03383283194";
    @NotNull
    private static final List<Long> COD_UNIDADES_PROLOG = Arrays.asList(5L, 103L, 179L, 215L);

    @Override
    @BeforeAll
    public void initialize() {
        DatabaseManager.init();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @NotNull
    private Afericao createAfericaoPlaca(@NotNull final Long codUnidade,
                                         @NotNull final NovaAfericaoPlaca novaAfericaoPlaca) {
        final AfericaoPlaca afericaoPlaca = new AfericaoPlaca();
        // Alteramos os sulcos de todos os pneus.
        final Sulcos sulcos = new Sulcos();
        sulcos.setExterno(10.0);
        sulcos.setCentralExterno(10.0);
        sulcos.setCentralInterno(10.0);
        sulcos.setInterno(10.0);
        for (final Pneu pneu : novaAfericaoPlaca.getVeiculo().getListPneus()) {
            pneu.setSulcosAtuais(sulcos);
            pneu.setPressaoAtual(110);
        }
        afericaoPlaca.setVeiculo(novaAfericaoPlaca.getVeiculo());
        afericaoPlaca.setKmMomentoAfericao(11111L);

        afericaoPlaca.setCodUnidade(codUnidade);
        afericaoPlaca.setDataHora(Now.getLocalDateTimeUtc());
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(3383283194L);
        afericaoPlaca.setColaborador(colaborador);
        afericaoPlaca.setTempoRealizacaoAfericaoInMillis(TimeUnit.MINUTES.toMillis(5));
        afericaoPlaca.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO_PRESSAO);
        afericaoPlaca.setFormaColetaDadosAfericao(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        return afericaoPlaca;
    }

    private void insereMapeamentoUnidades() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement("update unidade " +
                    "set cod_auxiliar = '09:02' " +
                    "where codigo = 5 or codigo = 103;");
            stmt.addBatch();
            stmt = conn.prepareStatement("update unidade " +
                    "set cod_auxiliar = '09:04' " +
                    "where codigo = 215;");
            stmt.addBatch();
            stmt = conn.prepareStatement("update unidade " +
                    "set cod_auxiliar = '01:21,01:01' " +
                    "where codigo = 215;");
            stmt.addBatch();
            stmt.executeBatch();
        } finally {
            provider.closeResources(conn, stmt);
        }
    }

    private void removeMapeamentoUnidades() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement("update unidade " +
                    "set cod_auxiliar = null " +
                    "where cod_empresa = 3;");
            stmt.executeUpdate();
        } finally {
            provider.closeResources(conn, stmt);
        }
    }

    @Test
    void testBuscaCronogramaAfericaoSemUnidadesConfiguradas() throws Throwable {
        // Removemos o mapeamento para testar sem nenhum código mapeado
        removeMapeamentoUnidades();
        final AfericaoService service = new AfericaoService();
        final CronogramaAfericao cronogramaAfericao =
                service.getCronogramaAfericao(getValidToken(CPF_COLABORADOR), COD_UNIDADES_PROLOG);

        assertThat(cronogramaAfericao).isNotNull();
        assertThat(cronogramaAfericao.getModelosPlacasAfericao()).isEmpty();
        insereMapeamentoUnidades();
    }

    @Test
    void testBuscaCronogramaAfericaoComUnidadesConfiguradas() throws Throwable {
        final AfericaoService service = new AfericaoService();
        final CronogramaAfericao cronogramaAfericao =
                service.getCronogramaAfericao(getValidToken(CPF_COLABORADOR), COD_UNIDADES_PROLOG);

        assertThat(cronogramaAfericao).isNotNull();
        assertThat(cronogramaAfericao.getModelosPlacasAfericao()).isNotEmpty();
    }

    @Test
    void testBuscaNovaAfericao() throws Throwable {
        final AfericaoService service = new AfericaoService();

        final CronogramaAfericao cronogramaAfericao =
                service.getCronogramaAfericao(getValidToken(CPF_COLABORADOR), COD_UNIDADES_PROLOG);
        final ModeloPlacasAfericao.PlacaAfericao placa = cronogramaAfericao.getModelosPlacasAfericao()
                .stream()
                .filter(modeloPlacasAfericao -> modeloPlacasAfericao.getTotalVeiculosModelo() > 0)
                .map(ModeloPlacasAfericao::getPlacasAfericao)
                .map(Collection::stream)
                .map(placaAfericaoStream ->
                        placaAfericaoStream
                                .filter(placaAfericao -> placaAfericao.getQuantidadePneus() > 0)
                                .findAny()
                                .orElse(null))
                .findFirst()
                .orElse(null);

        assertThat(placa).isNotNull();

        final NovaAfericaoPlaca novaAfericaoPlaca = service.getNovaAfericaoPlaca(
                getValidToken(CPF_COLABORADOR),
                placa.getCodUnidadePlaca(),
                placa.getPlaca(),
                TipoProcessoColetaAfericao.PLACA.asString());

        assertThat(novaAfericaoPlaca).isNotNull();
        assertThat(novaAfericaoPlaca.getVeiculo()).isNotNull();
    }

    @Test
    void testRealizacaoAfericaoPlaca() throws Throwable {
        final AfericaoService service = new AfericaoService();

        final CronogramaAfericao cronogramaAfericao =
                service.getCronogramaAfericao(getValidToken(CPF_COLABORADOR), COD_UNIDADES_PROLOG);
        final ModeloPlacasAfericao.PlacaAfericao placa = cronogramaAfericao.getModelosPlacasAfericao()
                .stream()
                .filter(modeloPlacasAfericao -> modeloPlacasAfericao.getTotalVeiculosModelo() > 0)
                .map(ModeloPlacasAfericao::getPlacasAfericao)
                .map(Collection::stream)
                .map(placaAfericaoStream ->
                        placaAfericaoStream
                                .filter(placaAfericao -> placaAfericao.getQuantidadePneus() > 0)
                                .findAny()
                                .orElse(null))
                .findFirst()
                .orElse(null);

        assertThat(placa).isNotNull();

        final NovaAfericaoPlaca novaAfericaoPlaca = service.getNovaAfericaoPlaca(
                getValidToken(CPF_COLABORADOR),
                placa.getCodUnidadePlaca(),
                placa.getPlaca(),
                "PLACA");

        final Long codAfericao =
                service.insert(
                        getValidToken(CPF_COLABORADOR),
                        placa.getCodUnidadePlaca(),
                        createAfericaoPlaca(placa.getCodUnidadePlaca(), novaAfericaoPlaca));

        assertThat(codAfericao).isNotNull();
        assertThat(codAfericao).isGreaterThan(0);
    }
}
